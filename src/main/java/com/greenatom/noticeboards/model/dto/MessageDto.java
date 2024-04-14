package com.greenatom.noticeboards.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageDto {

    @NotBlank(message = "text не должен быть пустым!")
    private String text;

    @Pattern(regexp = "^(?!\\d+$).+", message = "author не должен содержать только цифры!")
    @NotBlank(message = "author не должен быть пустым!")
    private String author;
}
