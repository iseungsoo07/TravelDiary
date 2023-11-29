package com.project.traveldiary.repository;

import com.project.traveldiary.entity.Diary;
import com.project.traveldiary.entity.Likes;
import com.project.traveldiary.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikesRepository extends JpaRepository<Likes, Long> {
    long countByDiary(Diary diary);

    boolean existsByUserAndDiary(User user, Diary diary);

    Optional<Likes> findByUserAndDiary(User user, Diary diary);
}
