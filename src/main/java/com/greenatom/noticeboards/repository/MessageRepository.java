package com.greenatom.noticeboards.repository;

import com.greenatom.noticeboards.model.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<Message, String> {
    Optional<Message> findById(UUID messageId);

    void deleteById(UUID messageId);
}

