package com.al3xkras.messenger.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@Builder
public class ChatDTO {
    private Long chatId;
    @Size(min = 1, max = 30)
    @Pattern(regexp = "^[a-zA-Z0-9_.]{1,30}$")
    private String chatName;
    @Size(min = 1, max = 50)
    private String displayName;
    @NotNull
    private Long ownerId;
}
