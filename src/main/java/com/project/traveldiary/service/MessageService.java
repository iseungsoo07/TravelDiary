package com.project.traveldiary.service;

import com.project.traveldiary.dto.MessageRequest;
import com.project.traveldiary.dto.MessageResponse;

public interface MessageService {

    MessageResponse sendMessage(Long id, MessageRequest messageRequest, String userId);

}
