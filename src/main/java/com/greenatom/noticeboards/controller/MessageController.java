package com.greenatom.noticeboards.controller;

import com.greenatom.noticeboards.model.dto.CustomFieldError;
import com.greenatom.noticeboards.model.dto.ExceptionResponse;
import com.greenatom.noticeboards.model.entity.Message;
import com.greenatom.noticeboards.model.dto.MessageDto;
import com.greenatom.noticeboards.model.entity.TopicWithMessages;
import com.greenatom.noticeboards.service.MessageService;
import com.greenatom.noticeboards.util.GeneratorResponseMessage;
import com.greenatom.noticeboards.util.ValidateFormat;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Message Controller", description = "Message API")
public class MessageController {
    private final ValidateFormat validateFormat;
    private final GeneratorResponseMessage generatorResponseMessage;
    private final MessageService messageService;

    @Autowired
    public MessageController(ValidateFormat validateFormat,
                             GeneratorResponseMessage generatorResponseMessage,
                             MessageService messageService) {
        this.validateFormat = validateFormat;
        this.generatorResponseMessage = generatorResponseMessage;
        this.messageService = messageService;
    }

    @DeleteMapping("/message/{messageId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete an existing message by Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Успешное выполнение запроса."),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера. Подробности об ошибке содержатся в теле ответа.", content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
    })
    public void deleteMessage(@PathVariable("messageId") String messageId) {
        messageService.deleteMessageById(messageId);
    }

    @PostMapping("/topic/{topicId}/message")
    @Operation(summary = "Create a new message in topic")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное выполнение запроса. Возвращенные данные в теле ответа.", content = @Content(schema = @Schema(implementation = TopicWithMessages.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "422", description = "Ошибка валидации. Подробности об ошибках содержатся в теле ответа.", content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера. Подробности об ошибке содержатся в теле ответа.", content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
    })
    public ResponseEntity<?> createMessage(@PathVariable("topicId") String topicId,
                                                           @RequestBody MessageDto messageDto,
                                                           BindingResult bindingResult) {
        validateFormat.validate(messageDto, bindingResult);
        if (bindingResult.hasErrors()) {
            List<CustomFieldError> customFieldErrors = generatorResponseMessage.generateErrorMessage(bindingResult);
            return ResponseEntity.badRequest().body(customFieldErrors);
        }
        return ResponseEntity.ok(messageService.createMessage(topicId, messageDto));
    }

    @PutMapping("/topic/{topicId}/message")
    @Operation(summary = "Update an existing message by Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное выполнение запроса. Возвращенные данные в теле ответа.", content = @Content(schema = @Schema(implementation = TopicWithMessages.class))),
            @ApiResponse(responseCode = "400", description = "Invalid ID supplied", content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "404", description = "Topic not found", content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "422", description = "Ошибка валидации. Подробности об ошибках содержатся в теле ответа.", content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера. Подробности об ошибке содержатся в теле ответа.", content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
    })
    public ResponseEntity<TopicWithMessages> updateMessage(@PathVariable String topicId, @RequestBody Message message) {
        return ResponseEntity.ok(messageService.updateMessage(topicId, message));
    }

}
