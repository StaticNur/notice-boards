package com.greenatom.noticeboards.service.impl;

import com.greenatom.noticeboards.exceptions.InvalidInputException;
import com.greenatom.noticeboards.exceptions.NotFoundException;
import com.greenatom.noticeboards.model.entity.Message;
import com.greenatom.noticeboards.model.dto.MessageDto;
import com.greenatom.noticeboards.model.entity.TopicWithMessages;
import com.greenatom.noticeboards.repository.MessageRepository;
import com.greenatom.noticeboards.service.MessageService;
import com.greenatom.noticeboards.service.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class MessageServiceImpl implements MessageService {
    private final TopicService topicService;
    private final MessageRepository messageRepository;

    @Autowired
    public MessageServiceImpl(MessageRepository messageRepository, TopicService topicService) {
        this.messageRepository = messageRepository;
        this.topicService = topicService;
    }

    @Override
    @Transactional
    public TopicWithMessages updateMessage(String topicId, Message updatedMessage) {
        TopicWithMessages topic = topicService.findById(convertToUUID(topicId))
                .orElseThrow(() -> new NotFoundException("Топик с указанным ID не найден"));

        Optional<Message> optionalMessage = topic.getMessages().stream()
                .filter(message -> message.getId().equals(updatedMessage.getId()))
                .findFirst();

        if (optionalMessage.isPresent()) {
            Message messageToUpdate = optionalMessage.get();
            messageToUpdate.setText(updatedMessage.getText());
            messageToUpdate.setAuthor(updatedMessage.getAuthor());
            messageToUpdate.setCreated(updatedMessage.getCreated());
            messageRepository.save(messageToUpdate);
        } else {
            throw new NotFoundException("Сообщение с указанным ID не найдено в топике");
        }

        return topic;
    }

    @Override
    @Transactional
    public void deleteAll(String topicId) {
        TopicWithMessages topic = topicService.findById(convertToUUID(topicId))
                .orElseThrow(() -> new NotFoundException("Топик с таким ID не существует"));

        List<Message> messages = topic.getMessages();
        for (Message message : messages) {
            messageRepository.delete(message);
        }
    }

    @Override
    public TopicWithMessages createMessage(String topicId, MessageDto messageDto) {
        TopicWithMessages topic = topicService.findById(convertToUUID(topicId))
                .orElseThrow(() -> new NotFoundException("Топик с таким ID не существует"));

        Message message = buildMessageFromDto(messageDto);
        message.setOwner(topic);

        messageRepository.save(message);

        topic.getMessages().add(message);

        return topic;
    }

    @Override
    @Transactional
    public void deleteMessageById(String messageId) {
        Message message = messageRepository.findById(convertToUUID(messageId))
                .orElseThrow(() -> new NotFoundException("Сообщение с указанным ID не найдено"));

        if (message.getOwner() != null) {
            TopicWithMessages topic = message.getOwner();
            List<Message> messages = topic.getMessages();

            if (messages.size() > 1) {
                messages.remove(message);
                topicService.save(topic);
                messageRepository.deleteById(convertToUUID(messageId));
            }
        }
    }

    private Message buildMessageFromDto(MessageDto messageDto) {
        Message message = new Message();
        message.setId(UUID.randomUUID());
        message.setText(messageDto.getText());
        message.setAuthor(messageDto.getAuthor());
        message.setCreated(ZonedDateTime.now());
        return message;
    }

    private UUID convertToUUID(String id){
        try {
            return UUID.fromString(id);
        }catch (IllegalArgumentException e){
            throw new InvalidInputException(e.getMessage());
        }
    }
}
