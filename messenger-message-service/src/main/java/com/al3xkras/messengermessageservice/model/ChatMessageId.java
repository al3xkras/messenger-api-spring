package com.al3xkras.messengermessageservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ChatMessageId implements Serializable {
    private Long chatId;
    private Long userId;
    private LocalDateTime submissionDate;
}
