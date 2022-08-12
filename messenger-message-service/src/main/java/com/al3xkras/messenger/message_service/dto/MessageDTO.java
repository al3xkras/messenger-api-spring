package com.al3xkras.messenger.message_service.dto;


import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@Builder
public class MessageDTO {
    @NotNull
    private Long chatId;
    @NotNull
    private Long userId;
    @NotNull
    private LocalDateTime submissionDate;



    @Size(min = 1, max = 128)
    private String message;
}
