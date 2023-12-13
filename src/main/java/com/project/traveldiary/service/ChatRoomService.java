package com.project.traveldiary.service;

import com.project.traveldiary.dto.ChatRoomResponse;
import com.project.traveldiary.dto.CreateChatResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ChatRoomService {

    CreateChatResponse createChat(String userId, Long receiverId);

    Page<ChatRoomResponse> getChatList(String userId, Pageable pageable);
}
