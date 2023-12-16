package com.project.traveldiary.service;

import static com.project.traveldiary.type.AlarmType.DIARY_COMMENT;
import static com.project.traveldiary.type.AlarmType.DIARY_LIKE;
import static com.project.traveldiary.type.AlarmType.FOLLOW;
import static com.project.traveldiary.type.AlarmType.MESSAGE;
import static com.project.traveldiary.type.ErrorCode.NOT_FOUND_USER;

import com.project.traveldiary.dto.NotificationRequest;
import com.project.traveldiary.dto.NotificationResponse;
import com.project.traveldiary.entity.Notification;
import com.project.traveldiary.entity.User;
import com.project.traveldiary.exception.UserException;
import com.project.traveldiary.repository.EmitterRepository;
import com.project.traveldiary.repository.NotificationRepository;
import com.project.traveldiary.repository.UserRepository;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;

    private final UserRepository userRepository;
    private final EmitterRepository emitterRepository;
    private final NotificationRepository notificationRepository;

    @Override
    public SseEmitter subscribe(String userId, String lastEventId) {
        User user = userRepository.findByUserId(userId)
            .orElseThrow(() -> new UserException(NOT_FOUND_USER));

        String emitterId = makeTimeIncludedId(user.getId());
        SseEmitter sseEmitter = emitterRepository.save(emitterId, new SseEmitter(DEFAULT_TIMEOUT));

        sseEmitter.onCompletion(() -> emitterRepository.deleteById(emitterId));
        sseEmitter.onTimeout(() -> emitterRepository.deleteById(emitterId));

        String eventId = makeTimeIncludedId(user.getId());
        sendNotification(sseEmitter, eventId, emitterId,
            "EventStream Created. [userId=" + user.getId() + "]");

        if (!lastEventId.isEmpty()) {
            Map<String, Object> eventCaches = emitterRepository.findAllEventCacheStartsWithUserId(
                String.valueOf(user.getId()));

            eventCaches.entrySet().stream()
                .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
                .forEach(entry -> sendNotification(sseEmitter, entry.getKey(), emitterId,
                    entry.getValue()));
        }

        return sseEmitter;
    }

    @Override
    public void send(NotificationRequest notificationRequest) {
        Notification notification = notificationRepository
            .save(createNotification(notificationRequest));

        String receiverId = String.valueOf(notification.getReceiver().getId());
        String eventId = makeTimeIncludedId(notification.getReceiver().getId());

        Map<String, SseEmitter> emitters = emitterRepository
            .findAllEmitterStartsWithUserId(receiverId);

        emitters.forEach((key, emitter) -> {
            emitterRepository.saveEventCache(key, notification);
            sendNotification(emitter, eventId, key, NotificationResponse.builder()
                .message(createNotificationMessage(notificationRequest))
                .sendTime(LocalDateTime.now())
                .path(notificationRequest.getPath())
                .build());
        });
    }

    private String makeTimeIncludedId(Long userId) {
        return userId + "_" + System.currentTimeMillis();
    }

    private void sendNotification(SseEmitter sseEmitter, String eventId, String emitterId,
        Object data) {
        try {
            sseEmitter.send(SseEmitter.event().id(eventId).data(data));
        } catch (IOException e) {
            emitterRepository.deleteById(emitterId);
        }
    }

    private Notification createNotification(NotificationRequest notificationRequest) {
        return Notification.builder()
            .receiver(notificationRequest.getReceiver())
            .alarmType(notificationRequest.getAlarmType())
            .params(notificationRequest.getParams())
            .path(notificationRequest.getPath())
            .checkedAt(null)
            .build();
    }

    private String createNotificationMessage(NotificationRequest notificationRequest) {
        String sender = notificationRequest.getParams().get("sender");

        User user = userRepository.findByUserId(sender)
            .orElseThrow(() -> new UserException(NOT_FOUND_USER));

        if (notificationRequest.getAlarmType() == DIARY_LIKE) {
            return user.getNickname() + "님이 " + notificationRequest.getReceiver().getNickname()
                + "님의 게시글에 좋아요를 눌렀습니다.";
        }

        if (notificationRequest.getAlarmType() == DIARY_COMMENT) {
            return user.getNickname() + "님이 " + notificationRequest.getReceiver().getNickname()
                + "님의 게시글에 댓글을 남겼습니다.";
        }

        if (notificationRequest.getAlarmType() == FOLLOW) {
            return user.getNickname() + "님이 " + notificationRequest.getReceiver().getNickname()
                + "님을 팔로우 했습니다.";
        }

        if (notificationRequest.getAlarmType() == MESSAGE) {
            return user.getNickname() + "님이 " + "채팅 메시지를 보냈습니다.";
        }

        return null;
    }

}
