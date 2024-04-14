package com.greenatom.noticeboards.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@Builder
public class Topic {

    @NotNull(message = "id не должен быть пустым!")
    private UUID id;

    @NotBlank(message = "name не должен быть пустым!")
    private String name;

    @PastOrPresent(message = "created не должен быть больше текущей даты!")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @NotNull(message = "created не должен быть пустым!")
    private ZonedDateTime created;
}
