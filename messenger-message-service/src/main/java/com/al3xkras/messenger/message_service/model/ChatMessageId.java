package com.al3xkras.messenger.message_service.model;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class ChatMessageId implements Serializable {
    private Long chatId;
    private Long userId;
    private LocalDateTime submissionDate;
}
