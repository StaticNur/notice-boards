package com.greenatom.noticeboards.controller;

import com.greenatom.noticeboards.model.dto.NewTopic;
import com.greenatom.noticeboards.model.dto.Topic;
import com.greenatom.noticeboards.model.entity.TopicWithMessages;
import com.greenatom.noticeboards.service.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class TopicController {

    private TopicService topicService;

    @Autowired
    public TopicController(TopicService topicService) {
        this.topicService = topicService;
    }

    @PostMapping("/topic")
    public ResponseEntity<TopicWithMessages> createTopic(@RequestBody NewTopic newTopic) {
        /*
        responses:
        '200':
          description: Successful operation
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/TopicWithMessages'
        '400':
          description: Invalid input
        '422':
          description: Validation exception
        * */
        return ResponseEntity.ok(topicService.createTopic(newTopic));
    }

    @PutMapping("/topic")
    public ResponseEntity<TopicWithMessages> updateTopic(@RequestBody Topic topic) {
        /*
        * responses:
        '200':
          description: Successful operation
          content:
            application/json:
              schema:
                type: array
                items:
                    $ref: '#/components/schemas/TopicWithMessages'
        '400':
          description: Invalid ID supplied
        '404':
          description: Topic not found
        '422':
          description: Validation exception
        * */
        return ResponseEntity.ok(topicService.updateTopic(topic));
    }

    @GetMapping("/topic")
    public ResponseEntity<List<Topic>> listAllTopics() {
        /*
        * responses:
        '200':
          description: successful operation
          content:
            application/json:
              schema:
                type: array
                items:
                    $ref: '#/components/schemas/Topic'
        * */
        return ResponseEntity.ok(topicService.getAllTopics());
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/topic/{topicId}")
    public void deleteTopic(@PathVariable("topicId") String topicId) {
        topicService.deleteTopicById(topicId);
    }

    @GetMapping("/topic/{topicId}")
    public ResponseEntity<TopicWithMessages> getTopicWithMessages(@PathVariable("topicId") String topicId) {
        /*
           * responses:
           '200':
             description: successful operation
             content:
               application/json:
                 schema:
                   type: array
                   items:
                       $ref: '#/components/schemas/Topic'
           * */
        return ResponseEntity.ok(topicService.getTopicById(topicId));
    }


}

