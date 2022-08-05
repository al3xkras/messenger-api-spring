package com.al3xkras.messengermessageservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public class ChatUserId implements Serializable {
    private Long chatId;
    private Long userId;
}
