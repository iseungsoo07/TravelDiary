package com.project.traveldiary.repository;

import com.project.traveldiary.entity.Diary;
import com.project.traveldiary.entity.Likes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikesRepository extends JpaRepository<Likes, Long> {
    long countByDiary(Diary diary);
}
