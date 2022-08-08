package com.al3xkras.messenger_chat_service.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
public class ChatDTO {
    private Long chatId;
    @Size(min = 1, max = 15)
    private String chatName;
    @Size(min = 1, max = 50)
    private String displayName;
    @NotNull
    private Long ownerId;
}
