package com.greenatom.noticeboards.controller;

import com.greenatom.noticeboards.exceptions.*;
import com.greenatom.noticeboards.model.dto.ExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class GlobalExceptionHandlerController {
    @ExceptionHandler({
            MissingServletRequestParameterException.class,
            InvalidInputException.class,
            InvalidTokenException.class,
            NotFoundException.class,
            AccessDeniedException.class,
            AuthorizeException.class
    })
    public ResponseEntity<ExceptionResponse> handleBadRequestException(Exception e) {
        return ResponseEntity.badRequest().body(new ExceptionResponse(e.getMessage()));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ExceptionResponse> handleNoResourceFoundException(NoResourceFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ExceptionResponse(e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleException(Exception e) {
        return ResponseEntity.internalServerError().body(new ExceptionResponse(e.getMessage()));
    }
}
