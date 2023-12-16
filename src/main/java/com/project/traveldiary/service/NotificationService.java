package com.project.traveldiary.service;

import com.project.traveldiary.dto.NotificationRequest;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface NotificationService {

    SseEmitter subscribe(String userId, String lastEventId);

    void send(NotificationRequest notificationRequest);
}
