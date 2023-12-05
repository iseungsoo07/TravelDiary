package com.project.traveldiary.service;

import static com.project.traveldiary.type.ErrorCode.CAN_DELETE_OWN_COMMENT;
import static com.project.traveldiary.type.ErrorCode.CAN_UPDATE_OWN_COMMENT;
import static com.project.traveldiary.type.ErrorCode.NOT_FOUND_COMMENT;

import com.project.traveldiary.dto.CommentRequest;
import com.project.traveldiary.dto.CommentResponse;
import com.project.traveldiary.entity.Comment;
import com.project.traveldiary.exception.CommentException;
import com.project.traveldiary.repository.CommentRepository;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    @Override
    public CommentResponse updateComment(Long id, CommentRequest commentRequest, String userId) {
        Comment comment = commentRepository.findById(id)
            .orElseThrow(() -> new CommentException(NOT_FOUND_COMMENT));

        if (!Objects.equals(comment.getUser().getUserId(), userId)) {
            throw new CommentException(CAN_UPDATE_OWN_COMMENT);
        }

        comment.updateContent(commentRequest.getContent());

        Comment savedComment = commentRepository.save(comment);

        return CommentResponse.builder()
            .parentCommentId(savedComment.getParentCommentId())
            .writer(savedComment.getUser().getNickname())
            .content(savedComment.getContent())
            .build();
    }

    @Override
    public void deleteComment(Long id, String userId) {
        Comment comment = commentRepository.findById(id)
            .orElseThrow(() -> new CommentException(NOT_FOUND_COMMENT));

        if (!Objects.equals(comment.getUser().getUserId(), userId)) {
            throw new CommentException(CAN_DELETE_OWN_COMMENT);
        }

        List<Comment> childComments = commentRepository.findByParentCommentId(id);

        commentRepository.deleteAll(childComments);
        commentRepository.delete(comment);
    }
}
