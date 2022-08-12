package com.al3xkras.messenger.message_service.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public class ChatUserId implements Serializable {
    private Long chatId;
    private Long userId;
}
