package com.al3xkras.messenger_chat_service.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;


@AllArgsConstructor
@Getter
@Setter
public class ChatMessageId implements Serializable {
    private Long chatId;
    private Long userId;
    private Date submissionDate;
}
