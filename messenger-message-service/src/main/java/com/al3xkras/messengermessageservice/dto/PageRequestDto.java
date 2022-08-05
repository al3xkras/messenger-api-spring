package com.al3xkras.messengermessageservice.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class PageRequestDto implements Serializable {
    private int page;
    private int size;
}
