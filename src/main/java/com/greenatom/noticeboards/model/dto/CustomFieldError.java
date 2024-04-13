package com.greenatom.noticeboards.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomFieldError {

    @JsonProperty("defaultMessage")
    private String defaultMessage;

    @JsonProperty("field")
    private String field;
}
