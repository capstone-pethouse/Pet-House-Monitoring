package com.capstone.pethouse.domain.file.service;

import com.capstone.pethouse.domain.file.dto.FileVo;
import com.capstone.pethouse.domain.file.entity.FileInfo;
import com.capstone.pethouse.domain.file.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class FileService {

    private final FileRepository fileRepository;

    @Value("${file.upload-dir:./uploads}")
    private String uploadDir;

    @Transactional(readOnly = true)
    public Page<FileVo> getList(int pageNum, int pageSize, String deviceId, String searchQuery) {
        PageRequest pageRequest = PageRequest.of(Math.max(pageNum - 1, 0), pageSize);
        return fileRepository.findAllWithSearch(deviceId, searchQuery, pageRequest).map(FileVo::from);
    }

    @Transactional(readOnly = true)
    public FileVo get(Long seq, String deviceId) {
        FileInfo file = findOne(seq, deviceId);
        return FileVo.from(file);
    }

    /**
     * 업로드 흐름:
     *   1) 디스크 쓰기
     *   2) DB save (실패 시 catch에서 디스크 파일 삭제 → orphan 방지)
     */
    @Transactional
    public FileInfo upload(String deviceId, String filename, MultipartFile multipartFile) throws IOException {
        if (deviceId == null || deviceId.isBlank()) {
            throw new IllegalArgumentException("deviceId는 필수입니다.");
        }
        if (multipartFile == null || multipartFile.isEmpty()) {
            throw new IllegalArgumentException("file은 필수입니다.");
        }

        String resolvedName = resolveFilename(filename, multipartFile);
        Path destPath = ensureUploadDir().resolve(UUID.randomUUID() + "_" + resolvedName);
        Files.copy(multipartFile.getInputStream(), destPath, StandardCopyOption.REPLACE_EXISTING);

        try {
            FileInfo info = FileInfo.of(
                    deviceId,
                    resolvedName,
                    destPath.toAbsolutePath().toString(),
                    multipartFile.getContentType(),
                    multipartFile.getSize()
            );
            return fileRepository.save(info);
        } catch (RuntimeException e) {
            // DB 실패 시 orphan 디스크 파일 즉시 정리
            tryDelete(destPath);
            throw e;
        }
    }

    /**
     * 수정 흐름:
     *   1) 새 파일이 있으면 디스크에 먼저 쓰기
     *   2) 엔티티 업데이트 (영속성 컨텍스트만)
     *   3) 옛 파일 삭제는 트랜잭션 커밋 후로 지연 → DB 롤백 시 옛 파일 보존
     *   4) 실패 시 새 파일은 catch에서 정리
     */
    @Transactional
    public FileInfo updateFile(Long seq, String deviceId, String filename, MultipartFile multipartFile) throws IOException {
        FileInfo file = findOne(seq, deviceId);

        if (multipartFile == null || multipartFile.isEmpty()) {
            // 메타데이터만 수정 (파일 그대로)
            file.update(deviceId, filename, file.getFilePath(), file.getContentType(), file.getFileSize());
            return file;
        }

        String oldPath = file.getFilePath();
        String resolvedName = resolveFilename(filename, multipartFile);
        Path newPath = ensureUploadDir().resolve(UUID.randomUUID() + "_" + resolvedName);
        Files.copy(multipartFile.getInputStream(), newPath, StandardCopyOption.REPLACE_EXISTING);

        try {
            file.update(
                    deviceId,
                    resolvedName,
                    newPath.toAbsolutePath().toString(),
                    multipartFile.getContentType(),
                    multipartFile.getSize()
            );
            // 옛 파일 삭제는 커밋 후 (롤백되면 옛 파일 보존)
            registerAfterCommitDeletion(oldPath);
            return file;
        } catch (RuntimeException e) {
            tryDelete(newPath);
            throw e;
        }
    }

    /**
     * 삭제 흐름:
     *   1) DB 삭제
     *   2) 디스크 파일 삭제는 커밋 후 (롤백되면 디스크 파일 보존)
     */
    @Transactional
    public void delete(Long seq, String deviceId) {
        FileInfo file = findOne(seq, deviceId);
        String filePath = file.getFilePath();
        fileRepository.delete(file);
        registerAfterCommitDeletion(filePath);
    }

    @Transactional(readOnly = true)
    public Resource loadAsResource(Long seq, String deviceId) {
        FileInfo file = findOne(seq, deviceId);
        try {
            Path path = Paths.get(file.getFilePath());
            Resource resource = new UrlResource(path.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new IllegalStateException("파일을 읽을 수 없습니다: " + file.getFilename());
            }
            return resource;
        } catch (MalformedURLException e) {
            throw new IllegalStateException("파일 경로가 올바르지 않습니다.", e);
        }
    }

    @Transactional(readOnly = true)
    public FileInfo getEntity(Long seq, String deviceId) {
        return findOne(seq, deviceId);
    }

    private FileInfo findOne(Long seq, String deviceId) {
        if (deviceId != null && !deviceId.isBlank()) {
            return fileRepository.findBySeqAndDeviceId(seq, deviceId)
                    .orElseThrow(() -> new IllegalArgumentException("파일을 찾을 수 없습니다."));
        }
        return fileRepository.findById(seq)
                .orElseThrow(() -> new IllegalArgumentException("파일을 찾을 수 없습니다."));
    }

    private Path ensureUploadDir() throws IOException {
        Path dir = Paths.get(uploadDir);
        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }
        return dir;
    }

    private String resolveFilename(String filename, MultipartFile multipartFile) {
        if (filename != null && !filename.isBlank()) return filename;
        String orig = multipartFile.getOriginalFilename();
        return (orig != null && !orig.isBlank()) ? orig : "unknown";
    }

    /**
     * 트랜잭션 커밋 후 디스크 파일 삭제 등록.
     * 롤백되면 디스크 파일은 그대로 → 데이터 손실 방지.
     */
    private void registerAfterCommitDeletion(String filePath) {
        if (filePath == null) return;
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    tryDelete(Paths.get(filePath));
                }
            });
        } else {
            tryDelete(Paths.get(filePath));
        }
    }

    private void tryDelete(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            log.warn("파일 삭제 실패: {}", path);
        }
    }
}
