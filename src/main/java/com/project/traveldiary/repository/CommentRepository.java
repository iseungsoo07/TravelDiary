package com.project.traveldiary.repository;

import com.project.traveldiary.entity.Comment;
import com.project.traveldiary.entity.Diary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    Page<Comment> findByDiaryAndParentCommentIdIsNullOrderByCreatedAtAsc(Diary diary,
        Pageable pageable);

    long countByParentCommentId(Long parentCommentId);

    void deleteByParentCommentId(Long parentCommentId);

    Page<Comment> findByParentCommentIdOrderByCreatedAtAsc(Long parentCommentId, Pageable pageable);

}
