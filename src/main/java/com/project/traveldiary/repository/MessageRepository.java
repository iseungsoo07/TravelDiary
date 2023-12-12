package com.project.traveldiary.repository;

import com.project.traveldiary.entity.Chat;
import com.project.traveldiary.entity.Message;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByChatOrderByCreatedAtDesc(Chat chat);
}
