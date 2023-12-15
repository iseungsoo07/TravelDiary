package com.project.traveldiary.controller;

import com.project.traveldiary.security.TokenProvider;
import com.project.traveldiary.service.NotificationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
public class NotificationController extends BaseController {

    private final NotificationService notificationService;

    public NotificationController(TokenProvider tokenProvider,
        NotificationService notificationService) {
        super(tokenProvider);
        this.notificationService = notificationService;
    }

    @GetMapping("/notification/subscribe/{userId}")
    public SseEmitter subscribe(@RequestHeader("X-AUTH-TOKEN") String token,
        @RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId) {

        String userId = getCurrentUserId(token);

        return notificationService.subscribe(userId, lastEventId);
    }

}
