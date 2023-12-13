package com.project.traveldiary.service;

import static com.project.traveldiary.type.ErrorCode.ALREADY_CHATROOM_CREATED;
import static com.project.traveldiary.type.ErrorCode.NOT_FOUND_USER;

import com.project.traveldiary.dto.ChatRoomResponse;
import com.project.traveldiary.dto.CreateChatResponse;
import com.project.traveldiary.entity.ChatRoom;
import com.project.traveldiary.entity.User;
import com.project.traveldiary.exception.ChatException;
import com.project.traveldiary.exception.UserException;
import com.project.traveldiary.repository.ChatRoomRepository;
import com.project.traveldiary.repository.MessageRepository;
import com.project.traveldiary.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService {

    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MessageRepository messageRepository;

    @Override
    public CreateChatResponse createChat(String userId, Long receiverId) {
        User user1 = userRepository.findByUserId(userId)
            .orElseThrow(() -> new UserException(NOT_FOUND_USER));

        User user2 = userRepository.findById(receiverId)
            .orElseThrow(() -> new UserException(NOT_FOUND_USER));

        // 8,3과 3,8은 같은 채팅방을 사용
        if (chatRoomRepository.existsByUser1AndUser2(user1, user2)
            || chatRoomRepository.existsByUser1AndUser2(user2, user1)) {
            throw new ChatException(ALREADY_CHATROOM_CREATED);
        }

        ChatRoom chatRoom = ChatRoom.builder()
            .user1(user1)
            .user2(user2)
            .build();

        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);

        return CreateChatResponse.builder()
            .chatId(savedChatRoom.getId())
            .user1(savedChatRoom.getUser1().getNickname())
            .user2(savedChatRoom.getUser2().getNickname())
            .build();
    }

    @Override
    public Page<ChatRoomResponse> getChatList(String userId, Pageable pageable) {
        User user = userRepository.findByUserId(userId)
            .orElseThrow(() -> new UserException(NOT_FOUND_USER));

        Page<ChatRoom> chatRoomPage = chatRoomRepository.findByUser1OrUser2(user, user, pageable);

        return chatRoomPage.map(chatRoom -> ChatRoomResponse.builder()
            .receiver(chatRoom.getReceiver(user))
            .lastMessage(
                messageRepository.findTopByChatRoomOrderByCreatedAtDesc(chatRoom).getContent())
            .build());
    }

}
