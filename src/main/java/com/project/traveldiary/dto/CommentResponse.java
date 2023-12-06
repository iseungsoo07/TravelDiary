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
public class CommentResponse {

    private Long parentCommentId;
    private String writer;
    private String content;

    public static CommentResponse of(Comment comment) {
        return CommentResponse.builder()
            .parentCommentId(comment.getParentCommentId())
            .writer(comment.getUser().getNickname())
            .content(comment.getContent())
            .build();
    }

}
