package com.project.traveldiary.service;

import static com.project.traveldiary.type.ErrorCode.ALREADY_CHAT_CREATED;
import static com.project.traveldiary.type.ErrorCode.NOT_FOUND_USER;

import com.project.traveldiary.dto.ChatRoomResponse;
import com.project.traveldiary.dto.CreateChatResponse;
import com.project.traveldiary.entity.Chat;
import com.project.traveldiary.entity.User;
import com.project.traveldiary.exception.ChatException;
import com.project.traveldiary.exception.UserException;
import com.project.traveldiary.repository.ChatRepository;
import com.project.traveldiary.repository.MessageRepository;
import com.project.traveldiary.repository.UserRepository;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final UserRepository userRepository;
    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;

    @Override
    public CreateChatResponse createChat(String userId, Long id) {
        User user1 = userRepository.findByUserId(userId)
            .orElseThrow(() -> new UserException(NOT_FOUND_USER));

        User user2 = userRepository.findById(id)
            .orElseThrow(() -> new UserException(NOT_FOUND_USER));

        // 8,3과 3,8은 같은 채팅방을 사용
        if (chatRepository.existsByUser1AndUser2(user1, user2)
            || chatRepository.existsByUser1AndUser2(user2, user1)) {
            throw new ChatException(ALREADY_CHAT_CREATED);
        }

        Chat chat = Chat.builder()
            .user1(user1)
            .user2(user2)
            .build();

        Chat savedChat = chatRepository.save(chat);

        return CreateChatResponse.builder()
            .chatId(savedChat.getId())
            .user1(savedChat.getUser1().getNickname())
            .user2(savedChat.getUser2().getNickname())
            .build();
    }

    @Override
    public Page<ChatRoomResponse> getChatList(String userId, Pageable pageable) {
        User user = userRepository.findByUserId(userId)
            .orElseThrow(() -> new UserException(NOT_FOUND_USER));

        Page<Chat> chatPage = chatRepository.findByUser1OrUser2(user, user, pageable);

        return chatPage.map(chat -> ChatRoomResponse.builder()
            .receiver(Objects.equals(chat.getUser1().getId(), user.getId()) ?
                chat.getUser2().getNickname() : chat.getUser1().getNickname())
            .lastMessage(messageRepository.findByChatOrderByCreatedAtDesc(chat).get(0).getContent())
            .build());
    }

}
