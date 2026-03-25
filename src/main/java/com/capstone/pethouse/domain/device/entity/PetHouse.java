package com.capstone.pethouse.domain.device.entity;

import com.capstone.pethouse.domain.user.entity.User;
import com.capstone.pethouse.global.common.AuditingFields;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Objects;

@ToString(callSuper = true)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_pet_house_user_id_nickname",
                        columnNames = {"user_id", "nickname"}
                )
        }
)
@Entity
public class PetHouse extends AuditingFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long houseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PetHouseStatus status;

    @Column(nullable = false)
    private Boolean isOccupied;

    @Column(nullable = false)
    private LocalDateTime lastConnectedAt;

    private PetHouse(User user, String nickname, PetHouseStatus status, Boolean isOccupied, LocalDateTime lastConnectedAt) {
        this.user = user;
        this.nickname = nickname;
        this.status = status;
        this.isOccupied = isOccupied;
        this.lastConnectedAt = lastConnectedAt;
    }

    public static PetHouse of(User user, String nickname, PetHouseStatus status, Boolean isOccupied, LocalDateTime lastConnectedAt) {
        return new PetHouse(user, nickname, status, isOccupied, lastConnectedAt);
    }

    public static PetHouse createDefault(User user, String nickname) {
        return new PetHouse(
                user,
                nickname,
                PetHouseStatus.OFFLINE,
                false,
                LocalDateTime.now()
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PetHouse that)) return false;
        return this.houseId != null && Objects.equals(this.houseId, that.houseId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(houseId);
    }
}
