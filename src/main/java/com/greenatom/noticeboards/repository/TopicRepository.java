package com.greenatom.noticeboards.repository;

import com.greenatom.noticeboards.model.entity.TopicWithMessages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TopicRepository extends JpaRepository<TopicWithMessages, String> {
    Optional<TopicWithMessages> findById(UUID topicId);
}

