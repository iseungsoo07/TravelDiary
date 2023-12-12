package com.project.traveldiary.repository;

import com.project.traveldiary.entity.Chat;
import com.project.traveldiary.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {

    Page<Chat> findByUser1OrUser2(User user1, User user2, Pageable pageable);

    boolean existsByUser1AndUser2(User user1, User user2);
}
