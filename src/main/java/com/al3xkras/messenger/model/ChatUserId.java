package com.al3xkras.messenger.model;

import java.io.Serializable;

public class ChatUserId implements Serializable {
    private Long chatId;
    private Long userId;

    //<editor-fold defaultstate="collapsed" desc="delombok">
    @SuppressWarnings("all")
    public ChatUserId(final Long chatId, final Long userId) {
        this.chatId = chatId;
        this.userId = userId;
    }

    @SuppressWarnings("all")
    public ChatUserId() {
    }

    @SuppressWarnings("all")
    public Long getChatId() {
        return this.chatId;
    }

    @SuppressWarnings("all")
    public Long getUserId() {
        return this.userId;
    }

    @SuppressWarnings("all")
    public void setChatId(final Long chatId) {
        this.chatId = chatId;
    }

    @SuppressWarnings("all")
    public void setUserId(final Long userId) {
        this.userId = userId;
    }
    //</editor-fold>
}
