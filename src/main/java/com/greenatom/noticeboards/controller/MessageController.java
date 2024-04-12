package com.greenatom.noticeboards.controller;

import com.greenatom.noticeboards.model.entity.Message;
import com.greenatom.noticeboards.model.dto.MessageDto;
import com.greenatom.noticeboards.model.entity.TopicWithMessages;
import com.greenatom.noticeboards.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class MessageController {
    private final MessageService messageService;

    @Autowired
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/message/{messageId}")
    public void deleteMessage(@PathVariable("messageId") String messageId) {
        //** responses:'204':description: Successful operation**//
        messageService.deleteMessageById(messageId);
    }

    @PostMapping("/topic/{topicId}/message")
    public ResponseEntity<TopicWithMessages> createMessage(@PathVariable("topicId") String topicId, @RequestBody MessageDto messageDto) {
        /*responses:
        '200':
          description: Successful operation
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/TopicWithMessages'
        '400':
          description: Invalid input
        '422':
          description: Validation exception*/
        return ResponseEntity.ok(messageService.createMessage(topicId, messageDto));
    }

    @PutMapping("/topic/{topicId}/message")
    public ResponseEntity<TopicWithMessages> updateMessage(@PathVariable String topicId, @RequestBody Message message) {
        /*
        * responses:
        '200':
          description: Successful operation
          content:
            application/json:
              schema:
                items:
                    $ref: '#/components/schemas/TopicWithMessages'
        '400':
          description: Invalid ID supplied
        '404':
          description: Topic not found
        '422':
          description: Validation exception
        * */
        return ResponseEntity.ok(messageService.updateMessage(topicId, message));
    }

}
