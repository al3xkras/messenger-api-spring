package com.al3xkras.messengermessageservice.entity;

import com.al3xkras.messengermessageservice.model.ChatUserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatUser {
    private Long chatId;
    private Long userId;
    private String title;
    private ChatUserRole chatUserRole;
    private Chat chat;
    private MessengerUser messengerUser;
}
