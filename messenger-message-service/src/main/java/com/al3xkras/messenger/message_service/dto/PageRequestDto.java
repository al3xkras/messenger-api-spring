package com.al3xkras.messenger.message_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageRequestDto implements Serializable {
    private int page;
    private int size;
}
