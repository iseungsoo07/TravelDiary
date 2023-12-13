package com.project.traveldiary.repository;

import com.project.traveldiary.entity.ChatRoom;
import com.project.traveldiary.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    Page<ChatRoom> findByUser1OrUser2(User user1, User user2, Pageable pageable);

    boolean existsByUser1AndUser2(User user1, User user2);
}
