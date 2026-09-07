package com.scar.jobflow_backend.notification;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    Optional<Notification> findByIdAndUserId(UUID id, UUID userId);

    Page<Notification> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);

    long countByUserIdAndReadAtIsNull(UUID userId);

    @Modifying
    @Query("""
        UPDATE Notification n
            SET n.readAt = CURRENT_TIMESTAMP
            WHERE n.user.id = :userId AND n.readAt IS NULL
    """)
    void markAllAsRead(@Param("userId") UUID userId);

}
