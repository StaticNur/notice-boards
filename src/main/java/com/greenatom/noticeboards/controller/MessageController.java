package com.greenatom.noticeboards.controller;

import com.greenatom.noticeboards.exceptions.AccessDeniedException;
import com.greenatom.noticeboards.model.dto.CustomFieldError;
import com.greenatom.noticeboards.model.dto.ExceptionResponse;
import com.greenatom.noticeboards.model.entity.Message;
import com.greenatom.noticeboards.model.dto.MessageDto;
import com.greenatom.noticeboards.model.entity.TopicWithMessages;
import com.greenatom.noticeboards.service.MessageService;
import com.greenatom.noticeboards.util.GeneratorResponseMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Message Controller", description = "Message API")
public class MessageController {
    private final GeneratorResponseMessage generatorResponseMessage;
    private final MessageService messageService;

    @Autowired
    public MessageController(GeneratorResponseMessage generatorResponseMessage,
                             MessageService messageService) {
        this.generatorResponseMessage = generatorResponseMessage;
        this.messageService = messageService;
    }

    @DeleteMapping("/message/{messageId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete an existing message by Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Успешное выполнение запроса."),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера. Подробности об ошибке содержатся в теле ответа.", content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
    })
    public void deleteMessage(@PathVariable("messageId") String messageId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            if (authentication.getAuthorities()
                    .stream().allMatch(authority -> "ADMIN".equals(authority.getAuthority()))) {
                messageService.deleteMessageById(messageId);
            } else {
                String username = authentication.getName();
                Message message = messageService.findById(messageId);
                if (message.getAuthor().equals(username)) {
                    messageService.deleteMessageById(messageId);
                } else throw new AccessDeniedException("Access denied. You can only delete your own messages");
            }
        }
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
                                           @RequestBody @Valid MessageDto messageDto,
                                           BindingResult bindingResult) {
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
    public ResponseEntity<?> updateMessage(@PathVariable String topicId,
                                           @RequestBody Message message,
                                           BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<CustomFieldError> customFieldErrors = generatorResponseMessage.generateErrorMessage(bindingResult);
            return ResponseEntity.badRequest().body(customFieldErrors);
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            if (authentication.getAuthorities()
                    .stream().allMatch(authority -> "ADMIN".equals(authority.getAuthority()))) {
                return ResponseEntity.ok(messageService.updateMessage(topicId, message));
            } else {
                String username = authentication.getName();
                if (message.getAuthor().equals(username)) {
                    return ResponseEntity.ok(messageService.updateMessage(topicId, message));
                } else throw new AccessDeniedException("Access denied. You can only edit your own messages");
            }
        }
        return ResponseEntity.internalServerError().body("Unknown error. Try logging out and logging in again");
    }

}
