package com.scar.jobflow_backend.notification.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record NotificationResponse(
        UUID id,
        String type,
        String title,
        String message,
        String relatedEntityType,
        UUID relatedEntityId,
        boolean isRead,
        LocalDateTime createdAt
) {}
