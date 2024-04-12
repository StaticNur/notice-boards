package com.greenatom.noticeboards.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewTopic {
    private String topicName;
    private MessageDto message;
}


