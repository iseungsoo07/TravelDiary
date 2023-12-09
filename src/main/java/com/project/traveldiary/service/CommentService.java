package com.project.traveldiary.service;

import com.project.traveldiary.dto.CommentRequest;
import com.project.traveldiary.dto.CommentResponse;

public interface CommentService {

    CommentResponse updateComment(Long id, CommentRequest commentRequest, String userId);

    void deleteComment(Long id, String userId);
}
