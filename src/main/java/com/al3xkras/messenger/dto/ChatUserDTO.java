package com.al3xkras.messenger.dto;

import com.al3xkras.messenger.model.ChatUserRole;

import javax.validation.constraints.NotNull;

public class ChatUserDTO {
    @NotNull
    private Long chatId;
    @NotNull
    private Long userId;
    private String title;
    @NotNull
    private ChatUserRole chatUserRole;

    public ChatUserDTO(){}
    //<editor-fold defaultstate="collapsed" desc="delombok">
    @SuppressWarnings("all")
    ChatUserDTO(final Long chatId, final Long userId, final String title, final ChatUserRole chatUserRole) {
        this.chatId = chatId;
        this.userId = userId;
        this.title = title;
        this.chatUserRole = chatUserRole;
    }


    @SuppressWarnings("all")
    public static class ChatUserDTOBuilder {
        @SuppressWarnings("all")
        private Long chatId;
        @SuppressWarnings("all")
        private Long userId;
        @SuppressWarnings("all")
        private String title;
        @SuppressWarnings("all")
        private ChatUserRole chatUserRole;

        @SuppressWarnings("all")
        ChatUserDTOBuilder() {
        }

        @SuppressWarnings("all")
        public ChatUserDTO.ChatUserDTOBuilder chatId(final Long chatId) {
            this.chatId = chatId;
            return this;
        }

        @SuppressWarnings("all")
        public ChatUserDTO.ChatUserDTOBuilder userId(final Long userId) {
            this.userId = userId;
            return this;
        }

        @SuppressWarnings("all")
        public ChatUserDTO.ChatUserDTOBuilder title(final String title) {
            this.title = title;
            return this;
        }

        @SuppressWarnings("all")
        public ChatUserDTO.ChatUserDTOBuilder chatUserRole(final ChatUserRole chatUserRole) {
            this.chatUserRole = chatUserRole;
            return this;
        }

        @SuppressWarnings("all")
        public ChatUserDTO build() {
            return new ChatUserDTO(this.chatId, this.userId, this.title, this.chatUserRole);
        }

        @Override
        @SuppressWarnings("all")
        public String toString() {
            return "ChatUserDTO.ChatUserDTOBuilder(chatId=" + this.chatId + ", userId=" + this.userId + ", title=" + this.title + ", chatUserRole=" + this.chatUserRole + ")";
        }
    }

    @SuppressWarnings("all")
    public static ChatUserDTO.ChatUserDTOBuilder builder() {
        return new ChatUserDTO.ChatUserDTOBuilder();
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
    public String getTitle() {
        return this.title;
    }

    @SuppressWarnings("all")
    public ChatUserRole getChatUserRole() {
        return this.chatUserRole;
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
    public void setTitle(final String title) {
        this.title = title;
    }

    @SuppressWarnings("all")
    public void setChatUserRole(final ChatUserRole chatUserRole) {
        this.chatUserRole = chatUserRole;
    }

    @Override
    @SuppressWarnings("all")
    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof ChatUserDTO)) return false;
        final ChatUserDTO other = (ChatUserDTO) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$chatId = this.getChatId();
        final Object other$chatId = other.getChatId();
        if (this$chatId == null ? other$chatId != null : !this$chatId.equals(other$chatId)) return false;
        final Object this$userId = this.getUserId();
        final Object other$userId = other.getUserId();
        if (this$userId == null ? other$userId != null : !this$userId.equals(other$userId)) return false;
        final Object this$title = this.getTitle();
        final Object other$title = other.getTitle();
        if (this$title == null ? other$title != null : !this$title.equals(other$title)) return false;
        final Object this$chatUserRole = this.getChatUserRole();
        final Object other$chatUserRole = other.getChatUserRole();
        if (this$chatUserRole == null ? other$chatUserRole != null : !this$chatUserRole.equals(other$chatUserRole)) return false;
        return true;
    }

    @SuppressWarnings("all")
    protected boolean canEqual(final Object other) {
        return other instanceof ChatUserDTO;
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
        final Object $title = this.getTitle();
        result = result * PRIME + ($title == null ? 43 : $title.hashCode());
        final Object $chatUserRole = this.getChatUserRole();
        result = result * PRIME + ($chatUserRole == null ? 43 : $chatUserRole.hashCode());
        return result;
    }

    @Override
    @SuppressWarnings("all")
    public String toString() {
        return "ChatUserDTO(chatId=" + this.getChatId() + ", userId=" + this.getUserId() + ", title=" + this.getTitle() + ", chatUserRole=" + this.getChatUserRole() + ")";
    }
    //</editor-fold>
}
