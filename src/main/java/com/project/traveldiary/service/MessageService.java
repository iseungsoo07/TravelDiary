package com.project.traveldiary.service;

import com.project.traveldiary.dto.MessageDTO;
import com.project.traveldiary.dto.MessageRequest;
import com.project.traveldiary.dto.MessageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MessageService {

    MessageResponse sendMessage(Long id, MessageRequest messageRequest, String userId);

    Page<MessageDTO> getMessages(Long chatRoomId, String userId, Pageable pageable);
}
