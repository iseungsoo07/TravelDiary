package com.project.traveldiary.controller;

import com.project.traveldiary.dto.CommentDeleteResponse;
import com.project.traveldiary.dto.CommentRequest;
import com.project.traveldiary.dto.CommentResponse;
import com.project.traveldiary.security.TokenProvider;
import com.project.traveldiary.service.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CommentController extends BaseController {

    private final CommentService commentService;

    public CommentController(TokenProvider tokenProvider,
        CommentService commentService) {
        super(tokenProvider);
        this.commentService = commentService;
    }

    @PutMapping("/comment/{id}")
    public ResponseEntity<CommentResponse> updateComment(@PathVariable Long id, @RequestBody
    CommentRequest commentRequest, @RequestHeader("X-AUTH-TOKEN") String token) {
        String userId = getCurrentUserId(token);

        return ResponseEntity.ok(commentService.updateComment(id, commentRequest, userId));
    }

    @DeleteMapping("/comment/{id}")
    public ResponseEntity<CommentDeleteResponse> deleteComment(@PathVariable Long id,
        @RequestHeader("X-AUTH-TOKEN") String token) {
        String userId = getCurrentUserId(token);

        commentService.deleteComment(id, userId);

        return ResponseEntity.ok(CommentDeleteResponse.builder()
            .message("댓글이 삭제되었습니다.")
            .build());
    }
}
