package com.scar.jobflow_backend.notification;

import com.scar.jobflow_backend.common.response.ApiResponse;
import com.scar.jobflow_backend.notification.dto.NotificationResponse;
import com.scar.jobflow_backend.security.CurrentUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final CurrentUserProvider currentUserProvider;

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream() {
        UUID userId = currentUserProvider.getCurrentUserId();
        return notificationService.subscribe(userId);
    }

    @GetMapping
    public ApiResponse<Page<NotificationResponse>> list(Pageable pageable) {
        return ApiResponse.success(notificationService.list(pageable));
    }

    @GetMapping("/unread-count")
    public ApiResponse<Long> getUnreadCount() {
        return ApiResponse.success(notificationService.getUnreadCount());
    }

    @PatchMapping("/{id}/read")
    public ApiResponse<Void> markAsRead(@PathVariable UUID id) {
        notificationService.markAsRead(id);
        return ApiResponse.success("Marked as read", null);
    }

    @PatchMapping("/mark-all-read")
    public ApiResponse<Void> markAllAsRead() {
        notificationService.markAllAsRead();
        return ApiResponse.success("All notifications marked as read", null);
    }
}