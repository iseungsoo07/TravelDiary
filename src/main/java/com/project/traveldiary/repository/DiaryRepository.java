package com.project.traveldiary.repository;

import com.project.traveldiary.entity.Diary;
import com.project.traveldiary.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiaryRepository extends JpaRepository<Diary, Long> {
    Page<Diary> findByUser(User user, Pageable pageable);
}
