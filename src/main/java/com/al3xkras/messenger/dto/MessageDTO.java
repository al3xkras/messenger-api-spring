package com.al3xkras.messenger.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

public class MessageDTO {
    @NotNull
    private Long chatId;
    @NotNull
    private Long userId;
    @NotNull
    private LocalDateTime submissionDate;
    @Size(min = 1, max = 128)
    private String message;

    //<editor-fold defaultstate="collapsed" desc="delombok">
    @SuppressWarnings("all")
    MessageDTO(final Long chatId, final Long userId, final LocalDateTime submissionDate, final String message) {
        this.chatId = chatId;
        this.userId = userId;
        this.submissionDate = submissionDate;
        this.message = message;
    }

    public MessageDTO(){}
    @SuppressWarnings("all")
    public static class MessageDTOBuilder {
        @SuppressWarnings("all")
        private Long chatId;
        @SuppressWarnings("all")
        private Long userId;
        @SuppressWarnings("all")
        private LocalDateTime submissionDate;
        @SuppressWarnings("all")
        private String message;

        @SuppressWarnings("all")
        MessageDTOBuilder() {
        }

        @SuppressWarnings("all")
        public MessageDTO.MessageDTOBuilder chatId(final Long chatId) {
            this.chatId = chatId;
            return this;
        }

        @SuppressWarnings("all")
        public MessageDTO.MessageDTOBuilder userId(final Long userId) {
            this.userId = userId;
            return this;
        }

        @SuppressWarnings("all")
        public MessageDTO.MessageDTOBuilder submissionDate(final LocalDateTime submissionDate) {
            this.submissionDate = submissionDate;
            return this;
        }

        @SuppressWarnings("all")
        public MessageDTO.MessageDTOBuilder message(final String message) {
            this.message = message;
            return this;
        }

        @SuppressWarnings("all")
        public MessageDTO build() {
            return new MessageDTO(this.chatId, this.userId, this.submissionDate, this.message);
        }

        @Override
        @SuppressWarnings("all")
        public String toString() {
            return "MessageDTO.MessageDTOBuilder(chatId=" + this.chatId + ", userId=" + this.userId + ", submissionDate=" + this.submissionDate + ", message=" + this.message + ")";
        }
    }

    @SuppressWarnings("all")
    public static MessageDTO.MessageDTOBuilder builder() {
        return new MessageDTO.MessageDTOBuilder();
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
    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof MessageDTO)) return false;
        final MessageDTO other = (MessageDTO) o;
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
        return other instanceof MessageDTO;
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

    @Override
    @SuppressWarnings("all")
    public String toString() {
        return "MessageDTO(chatId=" + this.getChatId() + ", userId=" + this.getUserId() + ", submissionDate=" + this.getSubmissionDate() + ", message=" + this.getMessage() + ")";
    }
    //</editor-fold>
}
