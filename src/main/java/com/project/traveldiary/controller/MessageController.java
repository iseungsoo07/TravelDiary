package com.project.traveldiary.controller;

import com.project.traveldiary.dto.MessageDTO;
import com.project.traveldiary.dto.MessageRequest;
import com.project.traveldiary.dto.MessageResponse;
import com.project.traveldiary.security.TokenProvider;
import com.project.traveldiary.service.MessageService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MessageController extends BaseController {

    private final MessageService messageService;

    public MessageController(TokenProvider tokenProvider, MessageService messageService) {
        super(tokenProvider);
        this.messageService = messageService;
    }

    @MessageMapping("/chatroom/{id}/send")
    public ResponseEntity<MessageResponse> sendMessage(@DestinationVariable Long id,
        MessageRequest messageRequest, @RequestHeader("X-AUTH-TOKEN") String token) {

        String userId = getCurrentUserId(token);

        return ResponseEntity.ok(messageService.sendMessage(id, messageRequest, userId));
    }

    @GetMapping("/chatroom/{chatRoomId}/messages")
    public ResponseEntity<Page<MessageDTO>> getMessages(@PathVariable Long chatRoomId,
        @RequestHeader("X-AUTH-TOKEN") String token, @PageableDefault Pageable pageable) {

        String userId = getCurrentUserId(token);

        return ResponseEntity.ok(messageService.getMessages(chatRoomId, userId, pageable));

    }
}
