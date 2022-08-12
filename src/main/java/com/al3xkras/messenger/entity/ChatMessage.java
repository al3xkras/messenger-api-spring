package com.al3xkras.messenger.entity;

import com.al3xkras.messenger.model.ChatMessageId;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "message")
@IdClass(ChatMessageId.class)
public class ChatMessage {
    @Id
    @Column(name = "chat_id", nullable = false)
    private Long chatId;
    @Id
    @Column(name = "user_id", nullable = false)
    private Long userId;
    @Id
    @Column(name = "submission_date", columnDefinition = "DATETIME", nullable = false)
    private LocalDateTime submissionDate;
    @Column(name = "message_str", columnDefinition = "nvarchar(255)", nullable = false)
    private String message;


    //<editor-fold defaultstate="collapsed" desc="delombok">
    @SuppressWarnings("all")
    public static class ChatMessageBuilder {
        @SuppressWarnings("all")
        private Long chatId;
        @SuppressWarnings("all")
        private Long userId;
        @SuppressWarnings("all")
        private LocalDateTime submissionDate;
        @SuppressWarnings("all")
        private String message;

        @SuppressWarnings("all")
        ChatMessageBuilder() {
        }

        @SuppressWarnings("all")
        public ChatMessage.ChatMessageBuilder chatId(final Long chatId) {
            this.chatId = chatId;
            return this;
        }

        @SuppressWarnings("all")
        public ChatMessage.ChatMessageBuilder userId(final Long userId) {
            this.userId = userId;
            return this;
        }

        @SuppressWarnings("all")
        public ChatMessage.ChatMessageBuilder submissionDate(final LocalDateTime submissionDate) {
            this.submissionDate = submissionDate;
            return this;
        }

        @SuppressWarnings("all")
        public ChatMessage.ChatMessageBuilder message(final String message) {
            this.message = message;
            return this;
        }

        @SuppressWarnings("all")
        public ChatMessage build() {
            return new ChatMessage(this.chatId, this.userId, this.submissionDate, this.message);
        }

        @Override
        @SuppressWarnings("all")
        public String toString() {
            return "ChatMessage.ChatMessageBuilder(chatId=" + this.chatId + ", userId=" + this.userId + ", submissionDate=" + this.submissionDate + ", message=" + this.message + ")";
        }
    }

    @SuppressWarnings("all")
    public static ChatMessage.ChatMessageBuilder builder() {
        return new ChatMessage.ChatMessageBuilder();
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
    public String getMessage() {
        return this.message;
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

    @SuppressWarnings("all")
    public void setMessage(final String message) {
        this.message = message;
    }

    @Override
    @SuppressWarnings("all")
    public String toString() {
        return "ChatMessage(chatId=" + this.getChatId() + ", userId=" + this.getUserId() + ", submissionDate=" + this.getSubmissionDate() + ", message=" + this.getMessage() + ")";
    }

    @SuppressWarnings("all")
    public ChatMessage() {
    }

    @SuppressWarnings("all")
    public ChatMessage(final Long chatId, final Long userId, final LocalDateTime submissionDate, final String message) {
        this.chatId = chatId;
        this.userId = userId;
        this.submissionDate = submissionDate;
        this.message = message;
    }

    @Override
    @SuppressWarnings("all")
    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof ChatMessage)) return false;
        final ChatMessage other = (ChatMessage) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$chatId = this.getChatId();
        final Object other$chatId = other.getChatId();
        if (this$chatId == null ? other$chatId != null : !this$chatId.equals(other$chatId)) return false;
        final Object this$userId = this.getUserId();
        final Object other$userId = other.getUserId();
        if (this$userId == null ? other$userId != null : !this$userId.equals(other$userId)) return false;
        final Object this$submissionDate = this.getSubmissionDate();
        final Object other$submissionDate = other.getSubmissionDate();
        if (this$submissionDate == null ? other$submissionDate != null : !this$submissionDate.equals(other$submissionDate)) return false;
        final Object this$message = this.getMessage();
        final Object other$message = other.getMessage();
        if (this$message == null ? other$message != null : !this$message.equals(other$message)) return false;
        return true;
    }

    @SuppressWarnings("all")
    protected boolean canEqual(final Object other) {
        return other instanceof ChatMessage;
    }

    @Override
    @SuppressWarnings("all")
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $chatId = this.getChatId();
        result = result * PRIME + ($chatId == null ? 43 : $chatId.hashCode());
        final Object $userId = this.getUserId();
        result = result * PRIME + ($userId == null ? 43 : $userId.hashCode());
        final Object $submissionDate = this.getSubmissionDate();
        result = result * PRIME + ($submissionDate == null ? 43 : $submissionDate.hashCode());
        final Object $message = this.getMessage();
        result = result * PRIME + ($message == null ? 43 : $message.hashCode());
        return result;
    }
    //</editor-fold>
}
