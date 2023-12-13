package com.project.traveldiary.controller;

import com.project.traveldiary.dto.ChatRoomResponse;
import com.project.traveldiary.dto.CreateChatResponse;
import com.project.traveldiary.security.TokenProvider;
import com.project.traveldiary.service.ChatRoomService;
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
public class ChatRoomController extends BaseController {

    private final ChatRoomService chatRoomService;

    public ChatRoomController(TokenProvider tokenProvider, ChatRoomService chatRoomService) {
        super(tokenProvider);
        this.chatRoomService = chatRoomService;
    }

    @PostMapping("/chatroom/{receiverId}")
    public ResponseEntity<CreateChatResponse> createChat(
        @RequestHeader("X-AUTH-TOKEN") String token, @PathVariable Long receiverId) {

        String userId = getCurrentUserId(token);

        return ResponseEntity.ok(chatRoomService.createChat(userId, receiverId));
    }

    @GetMapping("/chatroom/list")
    public ResponseEntity<Page<ChatRoomResponse>> getChatList(
        @RequestHeader("X-AUTH-TOKEN") String token,
        @PageableDefault Pageable pageable) {
        String userId = getCurrentUserId(token);

        return ResponseEntity.ok(chatRoomService.getChatList(userId, pageable));
    }

}
