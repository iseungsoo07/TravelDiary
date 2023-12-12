package com.project.traveldiary.controller;

import com.project.traveldiary.dto.ChatRoomResponse;
import com.project.traveldiary.dto.CreateChatResponse;
import com.project.traveldiary.security.TokenProvider;
import com.project.traveldiary.service.ChatService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatController extends BaseController {

    private final ChatService chatService;

    public ChatController(TokenProvider tokenProvider, ChatService chatService) {
        super(tokenProvider);
        this.chatService = chatService;
    }

    @PostMapping("/chat/{id}")
    public ResponseEntity<CreateChatResponse> createChat(
        @RequestHeader("X-AUTH-TOKEN") String token, @PathVariable Long id) {

        String userId = getCurrentUserId(token);

        return ResponseEntity.ok(chatService.createChat(userId, id));
    }

    @GetMapping("/chat/list")
    public ResponseEntity<Page<ChatRoomResponse>> getChatList(
        @RequestHeader("X-AUTH-TOKEN") String token,
        @PageableDefault Pageable pageable) {
        String userId = getCurrentUserId(token);

        return ResponseEntity.ok(chatService.getChatList(userId, pageable));
    }

}
