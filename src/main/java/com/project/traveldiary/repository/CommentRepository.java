package com.project.traveldiary.repository;

import com.project.traveldiary.entity.Comment;
import com.project.traveldiary.entity.Diary;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByParentCommentId(Long parentCommentId);

    Page<Comment> findByDiary(Diary diary, Pageable pageable);
}
