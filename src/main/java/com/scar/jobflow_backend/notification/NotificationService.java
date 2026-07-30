package com.scar.jobflow_backend.notification;

import com.scar.jobflow_backend.common.exception.ResourceNotFoundException;
import com.scar.jobflow_backend.notification.dto.NotificationResponse;
import com.scar.jobflow_backend.security.CurrentUserProvider;
import com.scar.jobflow_backend.user.User;
import com.scar.jobflow_backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private static final long SSE_TIMEOUT_MS = 30L * 60 * 1000;

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final NotificationMapper notificationMapper;
    private final CurrentUserProvider currentUserProvider;

    private final Map<UUID, List<SseEmitter>> emitters = new ConcurrentHashMap<>();

    public SseEmitter subscribe(UUID userId) {

        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT_MS);
        emitters.computeIfAbsent(userId, k -> new CopyOnWriteArrayList<>()).add(emitter);

        emitter.onCompletion(() -> removeEmitter(userId, emitter));
        emitter.onTimeout(() -> removeEmitter(userId, emitter));
        emitter.onError(e -> removeEmitter(userId, emitter));

        try {
            emitter.send(SseEmitter.event().name("connected").data("ok"));
        } catch (IOException e) {
            removeEmitter(userId, emitter);
        }

        return emitter;
    }

    public void removeEmitter(UUID userId, SseEmitter emitter) {
        List<SseEmitter> list = emitters.get(userId);
        if (list != null) {
            list.remove(emitter);
        }
    }

    @Transactional
    public void createAndPush(
            UUID userId,
            NotificationType type,
            String title,
            String message,
            String relatedEntityType,
            UUID relatedEntityId
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> ResourceNotFoundException.of("User", userId));

        Notification notification = Notification.builder()
                .user(user)
                .type(type)
                .title(title)
                .message(message)
                .relatedEntityType(relatedEntityType)
                .relatedEntityId(relatedEntityId)
                .build();

        Notification saved = notificationRepository.save(notification);
        NotificationResponse response = notificationMapper.toResponse(saved);

        pushToUser(userId, response);

    }

    private void pushToUser(UUID userId, NotificationResponse response) {

        List<SseEmitter> userEmitters = emitters.get(userId);
        if (userEmitters == null) return;

        for (SseEmitter emitter : userEmitters) {
            try {
                emitter.send(SseEmitter.event().name("notification").data(response));
            } catch (IOException e) {
                removeEmitter(userId, emitter);
            }
        }

    }

    public Page<NotificationResponse> list(Pageable pageable) {
        UUID userId = currentUserProvider.getCurrentUserId();
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(notificationMapper::toResponse);
    }

    public long getUnreadCount() {
        UUID userId = currentUserProvider.getCurrentUserId();
        return notificationRepository.countByUserIdAndReadAtIsNull(userId);
    }

    @Transactional
    public void markAsRead(UUID id) {
        UUID userId = currentUserProvider.getCurrentUserId();
        Notification notification = notificationRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> ResourceNotFoundException.of("Notification", id));
        notification.setReadAt(java.time.LocalDateTime.now());
        notificationRepository.save(notification);
    }

    @Transactional
    public void markAllAsRead() {
        UUID userId = currentUserProvider.getCurrentUserId();
        notificationRepository.markAllAsRead(userId);
    }

}
