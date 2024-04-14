package com.greenatom.noticeboards.service.impl;

import com.greenatom.noticeboards.exceptions.InvalidInputException;
import com.greenatom.noticeboards.exceptions.NotFoundException;
import com.greenatom.noticeboards.model.dto.NewTopic;
import com.greenatom.noticeboards.model.dto.Topic;
import com.greenatom.noticeboards.model.entity.Message;
import com.greenatom.noticeboards.model.entity.TopicWithMessages;
import com.greenatom.noticeboards.repository.TopicRepository;
import com.greenatom.noticeboards.service.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TopicServiceImlp implements TopicService {

    private final TopicRepository topicRepository;

    @Autowired
    public TopicServiceImlp(TopicRepository topicRepository) {
        this.topicRepository = topicRepository;
    }

    @Override
    @Transactional
    public TopicWithMessages createTopic(NewTopic newTopic) {
        TopicWithMessages topicWithMessages = new TopicWithMessages();
        topicWithMessages.setId(UUID.randomUUID());
        topicWithMessages.setName(newTopic.getTopicName());
        topicWithMessages.setCreated(ZonedDateTime.now());

        Message message = new Message();
        message.setId(UUID.randomUUID());
        message.setText(newTopic.getMessage().getText());
        message.setAuthor(newTopic.getMessage().getAuthor());
        message.setCreated(ZonedDateTime.now());
        message.setOwner(topicWithMessages);

        List<Message> messages = new ArrayList<>();
        messages.add(message);
        topicWithMessages.setMessages(messages);

        topicRepository.save(topicWithMessages);

        return topicWithMessages;
    }

    @Override
    public List<Topic> getAllTopics(Pageable pageable) {
        return topicRepository.findAll(pageable)
                .stream()
                .map(t -> Topic.builder()
                        .id(t.getId())
                        .name(t.getName())
                        .created(t.getCreated())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public TopicWithMessages getTopicMessagesById(String topicId, int page, int size) {
        TopicWithMessages topicWithMessages = topicRepository.findById(convertToUUID(topicId))
                .orElseThrow(() -> new NotFoundException("Топик с указанным ID не найден"));

        int startItem = page * size;

        List<Message> messages = topicWithMessages.getMessages();

        List<Message> pageMessages;

        if (messages.size() < startItem) {
            pageMessages = Collections.emptyList();
        } else {
            int toIndex = Math.min(startItem + size, messages.size());
            pageMessages = messages.subList(startItem, toIndex);
        }

        topicWithMessages.setMessages(pageMessages);

        return topicWithMessages;
    }

    @Override
    public Optional<TopicWithMessages> findById(UUID uuid) {
        return topicRepository.findById(uuid);
    }

    @Override
    @Transactional
    public void save(TopicWithMessages topic) {
        topicRepository.save(topic);
    }

    @Override
    @Transactional
    public TopicWithMessages updateTopic(Topic newTopic) {
        TopicWithMessages topic = topicRepository.findById(newTopic.getId())
                .orElseThrow(() -> new NotFoundException("Топик с указанным ID не найден"));

        topic.setName(newTopic.getName());
        topic.setCreated(newTopic.getCreated());

        topic = topicRepository.save(topic);

        return topic;
    }

    @Override
    @Transactional
    public void deleteTopicById(String topicId) {
        TopicWithMessages topic = topicRepository.findById(convertToUUID(topicId))
                .orElseThrow(() -> new NotFoundException("Топик с таким ID не существует"));
        topicRepository.delete(topic);
    }

    private UUID convertToUUID(String id) {
        try {
            return UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new InvalidInputException(e.getMessage());
        }
    }
}

