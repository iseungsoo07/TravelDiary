package com.project.traveldiary.service;

import static com.project.traveldiary.type.ErrorCode.CAN_GET_MESSAGES_OWN_CHATROOM;
import static com.project.traveldiary.type.ErrorCode.CAN_PARTICIPATE_OWN_CHATROOM;
import static com.project.traveldiary.type.ErrorCode.NOT_FOUND_CHATROOM;
import static com.project.traveldiary.type.ErrorCode.NOT_FOUND_USER;

import com.project.traveldiary.dto.MessageDTO;
import com.project.traveldiary.dto.MessageRequest;
import com.project.traveldiary.dto.MessageResponse;
import com.project.traveldiary.entity.ChatRoom;
import com.project.traveldiary.entity.Message;
import com.project.traveldiary.entity.User;
import com.project.traveldiary.exception.ChatException;
import com.project.traveldiary.exception.UserException;
import com.project.traveldiary.repository.ChatRoomRepository;
import com.project.traveldiary.repository.MessageRepository;
import com.project.traveldiary.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public MessageResponse sendMessage(Long id, MessageRequest messageRequest, String userId) {
        ChatRoom chatRoom = chatRoomRepository.findById(id)
            .orElseThrow(() -> new ChatException(NOT_FOUND_CHATROOM));

        User user = userRepository.findByUserId(userId)
            .orElseThrow(() -> new UserException(NOT_FOUND_USER));

        if (!chatRoom.isParticipant(user)) {
            throw new ChatException(CAN_PARTICIPATE_OWN_CHATROOM);
        }

        Message message = Message.builder()
            .chatRoom(chatRoom)
            .sender(user)
            .content(messageRequest.getMessage())
            .build();

        Message savedMessage = messageRepository.save(message);

        String senderNickname = user.getNickname();
        String receiverNickname = chatRoom.getReceiver(user);

        MessageResponse messageResponse = MessageResponse.builder()
            .sender(senderNickname)
            .receiver(receiverNickname)
            .message(savedMessage.getContent())
            .build();

        messagingTemplate.convertAndSend("/sub/chat/" + id, messageResponse);

        return messageResponse;
    }

    @Override
    public Page<MessageDTO> getMessages(Long chatRoomId, String userId, Pageable pageable) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
            .orElseThrow(() -> new ChatException(NOT_FOUND_CHATROOM));

        User user = userRepository.findByUserId(userId)
            .orElseThrow(() -> new UserException(NOT_FOUND_USER));

        if (!chatRoom.isParticipant(user)) {
            throw new ChatException(CAN_GET_MESSAGES_OWN_CHATROOM);
        }

        Page<Message> messagePage = messageRepository
            .findByChatRoomOrderByCreatedAtAsc(chatRoom, pageable);

        return messagePage.map(MessageDTO::of);
    }
}
