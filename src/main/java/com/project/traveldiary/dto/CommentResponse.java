package com.project.traveldiary.dto;

import com.project.traveldiary.entity.Comment;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentResponse {

    private Long parentCommentId;
    private String writer;
    private String content;

    public static Page<CommentResponse> commentList(Page<Comment> comments) {
        return new PageImpl<>(
            comments.getContent().stream().map(comment -> CommentResponse.builder()
                .parentCommentId(comment.getParentCommentId())
                .writer(comment.getUser().getNickname())
                .content(comment.getContent())
                .build()).collect(Collectors.toList()));
    }

}
