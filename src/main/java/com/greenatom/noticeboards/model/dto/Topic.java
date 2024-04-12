package com.greenatom.noticeboards.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@Builder
public class Topic {
    private UUID id;
    private String name;
    private ZonedDateTime created;
}
