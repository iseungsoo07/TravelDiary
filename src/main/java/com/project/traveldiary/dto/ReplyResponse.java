package com.project.traveldiary.dto;

import com.project.traveldiary.entity.Comment;
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
public class ReplyResponse {

    private Long parentCommentId;
    private String writer;
    private String content;

    public static ReplyResponse of(Comment comment) {
        return ReplyResponse.builder()
            .parentCommentId(comment.getParentCommentId())
            .writer(comment.getUser().getNickname())
            .content(comment.getContent())
            .build();
    }
}
