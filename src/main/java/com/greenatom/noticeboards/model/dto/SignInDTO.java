package com.greenatom.noticeboards.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SignInDTO(
        @NotBlank(message = "username не должен быть пустым!") String username,
        @NotBlank(message = "password не должен быть пустым!") String password) {
}
