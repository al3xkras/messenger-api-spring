package com.al3xkras.messenger.chat_service.model;

import com.al3xkras.messenger.model.ChatUserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatUserDetails {
    private Long chatId;
    private Long userId;
    private ChatUserRole role;
}
