package com.al3xkras.messenger_chat_service.dto;

import lombok.Data;

import javax.validation.constraints.Size;

@Data
public class ChatDTO {
    private Long chatId;
    private String chatName;
    @Size(min = 1, max = 20)
    private String displayName;
}
