package com.al3xkras.messenger.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class ChatMessageId implements Serializable {
    private Long chatId;
    private Long userId;
    private LocalDateTime submissionDate;

    //<editor-fold defaultstate="collapsed" desc="delombok">
    @SuppressWarnings("all")
    public ChatMessageId(final Long chatId, final Long userId, final LocalDateTime submissionDate) {
        this.chatId = chatId;
        this.userId = userId;
        this.submissionDate = submissionDate;
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
    public LocalDateTime getSubmissionDate() {
        return this.submissionDate;
    }

    @SuppressWarnings("all")
    public void setChatId(final Long chatId) {
        this.chatId = chatId;
    }

    @SuppressWarnings("all")
    public void setUserId(final Long userId) {
        this.userId = userId;
    }

    @SuppressWarnings("all")
    public void setSubmissionDate(final LocalDateTime submissionDate) {
        this.submissionDate = submissionDate;
    }
    //</editor-fold>
}
