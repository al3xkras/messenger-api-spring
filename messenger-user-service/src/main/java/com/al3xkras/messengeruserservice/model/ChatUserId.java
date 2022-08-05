package com.al3xkras.messengeruserservice.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class ChatUserId implements Serializable {
    private Long chatId;
    private Long userId;
}
