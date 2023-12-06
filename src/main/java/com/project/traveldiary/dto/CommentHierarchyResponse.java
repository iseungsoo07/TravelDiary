package com.project.traveldiary.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentHierarchyResponse {

    private CommentResponse comment;
    private Page<CommentResponse> replies;

}
