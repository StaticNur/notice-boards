package com.greenatom.noticeboards.model.dto;

import jakarta.validation.constraints.NotBlank;

public record SignInDTO(
        @NotBlank(message = "username не должен быть пустым!") String username,
        @NotBlank(message = "password не должен быть пустым!") String password) {
}
