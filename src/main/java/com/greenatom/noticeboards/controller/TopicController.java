package com.greenatom.noticeboards.controller;

import com.greenatom.noticeboards.model.dto.CustomFieldError;
import com.greenatom.noticeboards.model.dto.ExceptionResponse;
import com.greenatom.noticeboards.model.dto.NewTopic;
import com.greenatom.noticeboards.model.dto.Topic;
import com.greenatom.noticeboards.model.entity.TopicWithMessages;
import com.greenatom.noticeboards.service.MessageService;
import com.greenatom.noticeboards.service.TopicService;
import com.greenatom.noticeboards.util.GeneratorResponseMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Topic Controller", description = "Topic API")
public class TopicController {

    private final MessageService messageService;
    private final TopicService topicService;
    private final GeneratorResponseMessage generatorResponseMessage;

    @Autowired
    public TopicController(MessageService messageService,
                           TopicService topicService,
                           GeneratorResponseMessage generatorResponseMessage) {
        this.messageService = messageService;
        this.topicService = topicService;
        this.generatorResponseMessage = generatorResponseMessage;
    }

    @PostMapping("/topic")
    @Operation(summary = "Create a new topic to the store")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное выполнение запроса. Возвращенные данные в теле ответа.", content = @Content(schema = @Schema(implementation = TopicWithMessages.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "422", description = "Ошибка валидации. Подробности об ошибках содержатся в теле ответа.", content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера. Подробности об ошибке содержатся в теле ответа.", content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
    })
    public ResponseEntity<?> createTopic(@RequestBody @Valid NewTopic newTopic,
                                                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<CustomFieldError> customFieldErrors = generatorResponseMessage.generateErrorMessage(bindingResult);
            return ResponseEntity.badRequest().body(customFieldErrors);
        }
        return ResponseEntity.ok(topicService.createTopic(newTopic));
    }

    @PutMapping("/topic")
    @Operation(summary = "Update an existing message by Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное выполнение запроса. Возвращенные данные в теле ответа.", content = @Content(schema = @Schema(implementation = TopicWithMessages.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "404", description = "Topic not found", content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "422", description = "Ошибка валидации. Подробности об ошибках содержатся в теле ответа.", content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера. Подробности об ошибке содержатся в теле ответа.", content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
    })
    public ResponseEntity<?> updateTopic(@RequestBody @Valid Topic topic,
                                                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<CustomFieldError> customFieldErrors = generatorResponseMessage.generateErrorMessage(bindingResult);
            return ResponseEntity.badRequest().body(customFieldErrors);
        }
        return ResponseEntity.ok(topicService.updateTopic(topic));
    }

    @GetMapping("/topic")
    @Operation(summary = "View all topics")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное выполнение запроса. Возвращенные данные в теле ответа.",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Topic.class)))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера. Подробности об ошибке содержатся в теле ответа.",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
    })
    public ResponseEntity<List<Topic>> listAllTopics() {
        return ResponseEntity.ok(topicService.getAllTopics());
    }

    @DeleteMapping("/topic/{topicId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "delete the topic and all its messages")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Успешное выполнение запроса."),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера. Подробности об ошибке содержатся в теле ответа.",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
    })
    public void deleteTopic(@PathVariable("topicId") String topicId) {
        messageService.deleteAll(topicId);
        topicService.deleteTopicById(topicId);
    }

    @GetMapping("/topic/{topicId}")
    @Operation(summary = "Shows all messages in topic")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное выполнение запроса. Возвращенные данные в теле ответа.", content = @Content(schema = @Schema(implementation = TopicWithMessages.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера. Подробности об ошибке содержатся в теле ответа.", content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
    })
    public ResponseEntity<TopicWithMessages> getTopicWithMessages(@PathVariable("topicId") String topicId) {
        return ResponseEntity.ok(topicService.getTopicById(topicId));
    }


}

