package com.greenatom.noticeboards.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "FORUM_ENGINE_DATA.MESSAGE")
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "text")
    private String text;

    @Column(name = "author")
    private String author;

    @Column(name = "created")
    private ZonedDateTime created;

    @ManyToOne
    @JoinColumn(name = "topic_id", referencedColumnName = "id")
    @JsonBackReference
    private TopicWithMessages owner;
}
