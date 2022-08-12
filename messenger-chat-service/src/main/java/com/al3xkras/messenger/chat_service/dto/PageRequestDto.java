package com.al3xkras.messenger.chat_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PageRequestDto {
    private int page;
    private int size;
}
