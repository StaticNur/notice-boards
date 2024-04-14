package com.greenatom.noticeboards.model.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewTopic {

    @NotBlank(message = "topicName не должен быть пустым!")
    private String topicName;

    @NotNull(message = "message не должен быть пустым!")
    @Valid
    private MessageDto message;
}


