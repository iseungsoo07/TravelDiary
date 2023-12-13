package com.project.traveldiary.dto;

import com.project.traveldiary.entity.Message;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageDTO {
    private String sender;
    private String content;
    private LocalDateTime sendTime;

    public static MessageDTO of(Message message) {
        return MessageDTO.builder()
            .sender(message.getSender().getNickname())
            .content(message.getContent())
            .sendTime(message.getCreatedAt())
            .build();
    }
}
