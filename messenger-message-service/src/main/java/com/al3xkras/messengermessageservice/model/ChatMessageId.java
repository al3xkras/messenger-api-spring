package com.al3xkras.messengermessageservice.model;

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
