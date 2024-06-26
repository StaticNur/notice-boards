package com.greenatom.noticeboards.service;

import com.greenatom.noticeboards.model.entity.Message;
import com.greenatom.noticeboards.model.dto.MessageDto;
import com.greenatom.noticeboards.model.entity.TopicWithMessages;

public interface MessageService {
    Message findById(String messageId);

    void deleteMessageById(String messageId);

    TopicWithMessages createMessage(String topicId, MessageDto messageDto);

    TopicWithMessages updateMessage(String topicId, Message message);

    void deleteAll(String topicId);
}
