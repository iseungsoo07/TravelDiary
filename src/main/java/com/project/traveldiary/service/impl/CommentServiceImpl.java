package com.project.traveldiary.service.impl;

import static com.project.traveldiary.type.ErrorCode.CAN_DELETE_OWN_COMMENT;
import static com.project.traveldiary.type.ErrorCode.CAN_UPDATE_OWN_COMMENT;
import static com.project.traveldiary.type.ErrorCode.NOT_FOUND_COMMENT;
import static com.project.traveldiary.type.ErrorCode.NOT_FOUND_DIARY;

import com.project.traveldiary.aop.DistributedLock;
import com.project.traveldiary.dto.CommentRequest;
import com.project.traveldiary.dto.CommentResponse;
import com.project.traveldiary.entity.Comment;
import com.project.traveldiary.entity.Diary;
import com.project.traveldiary.exception.CommentException;
import com.project.traveldiary.exception.DiaryException;
import com.project.traveldiary.repository.CommentRepository;
import com.project.traveldiary.repository.DiaryRepository;
import com.project.traveldiary.service.CommentService;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final DiaryRepository diaryRepository;

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
    @Transactional
    @DistributedLock(prefix = "comment_diary")
    public void deleteComment(Long id, String userId) {
        Comment comment = commentRepository.findById(id)
            .orElseThrow(() -> new CommentException(NOT_FOUND_COMMENT));

        if (!Objects.equals(comment.getUser().getUserId(), userId)) {
            throw new CommentException(CAN_DELETE_OWN_COMMENT);
        }

        Diary diary = diaryRepository.findById(comment.getDiary().getId())
            .orElseThrow(() -> new DiaryException(NOT_FOUND_DIARY));

        long childCount = commentRepository.countByParentCommentId(comment.getId());

        commentRepository.deleteByParentCommentId(comment.getId());
        commentRepository.delete(comment);

        diary.decreaseCommentCount(childCount + 1);

        diaryRepository.save(diary);
    }
}
