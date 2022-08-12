package com.al3xkras.messenger.dto;

import com.al3xkras.messenger.model.ChatUserRole;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Builder
public class ChatUserDTO {
    @NotNull
    private Long chatId;
    @NotNull
    private Long userId;
    private String title;
    @NotNull
    private ChatUserRole chatUserRole;
}
