package com.al3xkras.messengermessageservice.dto;

import com.al3xkras.messengermessageservice.model.ChatUserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
