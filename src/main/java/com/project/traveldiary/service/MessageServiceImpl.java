package com.project.traveldiary.service;

import static com.project.traveldiary.type.ErrorCode.NOT_FOUND_CHAT;
import static com.project.traveldiary.type.ErrorCode.NOT_FOUND_USER;

import com.project.traveldiary.dto.MessageRequest;
import com.project.traveldiary.dto.MessageResponse;
import com.project.traveldiary.entity.Chat;
import com.project.traveldiary.entity.Message;
import com.project.traveldiary.entity.User;
import com.project.traveldiary.exception.ChatException;
import com.project.traveldiary.exception.UserException;
import com.project.traveldiary.repository.ChatRepository;
import com.project.traveldiary.repository.MessageRepository;
import com.project.traveldiary.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public MessageResponse sendMessage(Long id, MessageRequest messageRequest, String userId) {
        Chat chat = chatRepository.findById(id)
            .orElseThrow(() -> new ChatException(NOT_FOUND_CHAT));

        User user = userRepository.findByUserId(userId)
            .orElseThrow(() -> new UserException(NOT_FOUND_USER));

        Message message = Message.builder()
            .chat(chat)
            .sender(user)
            .content(messageRequest.getMessage())
            .build();

        Message savedMessage = messageRepository.save(message);

        String senderNickname = user.getNickname();
        String receiverNickname =
            savedMessage.getChat().getUser1().getNickname().equals(senderNickname)
                ? savedMessage.getChat().getUser2().getNickname()
                : savedMessage.getChat().getUser1().getNickname();

        MessageResponse messageResponse = MessageResponse.builder()
            .sender(senderNickname)
            .receiver(receiverNickname)
            .message(savedMessage.getContent())
            .build();

        messagingTemplate.convertAndSend("/sub/chat/" + id, messageResponse);

        return messageResponse;
    }
}
