package com.greenatom.noticeboards.service;

import com.greenatom.noticeboards.model.dto.NewTopic;
import com.greenatom.noticeboards.model.dto.Topic;
import com.greenatom.noticeboards.model.entity.TopicWithMessages;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TopicService {
    TopicWithMessages createTopic(NewTopic newTopic);

    TopicWithMessages updateTopic(Topic topic);

    List<Topic> getAllTopics(Pageable pageable);

    void deleteTopicById(String topicId);

    TopicWithMessages getTopicMessagesById(String topicId, int page, int size);

    Optional<TopicWithMessages> findById(UUID uuid);

    void save(TopicWithMessages topic);
}
