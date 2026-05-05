package com.capstone.pethouse.domain.iot.repository;

import com.capstone.pethouse.domain.iot.entity.DeviceCommand;
import com.capstone.pethouse.domain.iot.enums.CommandStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeviceCommandRepository extends JpaRepository<DeviceCommand, Long> {

    /**
     * SN의 대기(W) 중인 명령 조회 (오래된 순).
     */
    List<DeviceCommand> findBySnAndStatusOrderBySeqAsc(String sn, CommandStatus status);
}
