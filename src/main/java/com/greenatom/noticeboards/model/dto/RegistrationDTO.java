package com.greenatom.noticeboards.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegistrationDTO(
        @NotBlank(message = "userName не должен быть пустым!") String userName,
        @NotBlank(message = "password не должен быть пустым!") String password,
        @NotBlank(message = "role не должен быть пустым! Пример admin/user") String role) {
}
