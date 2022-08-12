package com.al3xkras.messenger.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class ChatDTO {
    private Long chatId;
    @Size(min = 1, max = 30)
    @Pattern(regexp = "^[a-zA-Z0-9_.]{1,30}$")
    private String chatName;
    @Size(min = 1, max = 50)
    private String displayName;
    @NotNull
    private Long ownerId;

    //<editor-fold defaultstate="collapsed" desc="delombok">
    @SuppressWarnings("all")
    ChatDTO(final Long chatId, final String chatName, final String displayName, final Long ownerId) {
        this.chatId = chatId;
        this.chatName = chatName;
        this.displayName = displayName;
        this.ownerId = ownerId;
    }


    @SuppressWarnings("all")
    public static class ChatDTOBuilder {
        @SuppressWarnings("all")
        private Long chatId;
        @SuppressWarnings("all")
        private String chatName;
        @SuppressWarnings("all")
        private String displayName;
        @SuppressWarnings("all")
        private Long ownerId;

        @SuppressWarnings("all")
        ChatDTOBuilder() {
        }

        @SuppressWarnings("all")
        public ChatDTO.ChatDTOBuilder chatId(final Long chatId) {
            this.chatId = chatId;
            return this;
        }

        @SuppressWarnings("all")
        public ChatDTO.ChatDTOBuilder chatName(final String chatName) {
            this.chatName = chatName;
            return this;
        }

        @SuppressWarnings("all")
        public ChatDTO.ChatDTOBuilder displayName(final String displayName) {
            this.displayName = displayName;
            return this;
        }

        @SuppressWarnings("all")
        public ChatDTO.ChatDTOBuilder ownerId(final Long ownerId) {
            this.ownerId = ownerId;
            return this;
        }

        @SuppressWarnings("all")
        public ChatDTO build() {
            return new ChatDTO(this.chatId, this.chatName, this.displayName, this.ownerId);
        }

        @Override
        @SuppressWarnings("all")
        public String toString() {
            return "ChatDTO.ChatDTOBuilder(chatId=" + this.chatId + ", chatName=" + this.chatName + ", displayName=" + this.displayName + ", ownerId=" + this.ownerId + ")";
        }
    }

    @SuppressWarnings("all")
    public static ChatDTO.ChatDTOBuilder builder() {
        return new ChatDTO.ChatDTOBuilder();
    }

    @SuppressWarnings("all")
    public Long getChatId() {
        return this.chatId;
    }

    @SuppressWarnings("all")
    public String getChatName() {
        return this.chatName;
    }

    @SuppressWarnings("all")
    public String getDisplayName() {
        return this.displayName;
    }

    @SuppressWarnings("all")
    public Long getOwnerId() {
        return this.ownerId;
    }

    @SuppressWarnings("all")
    public void setChatId(final Long chatId) {
        this.chatId = chatId;
    }

    @SuppressWarnings("all")
    public void setChatName(final String chatName) {
        this.chatName = chatName;
    }

    @SuppressWarnings("all")
    public void setDisplayName(final String displayName) {
        this.displayName = displayName;
    }

    @SuppressWarnings("all")
    public void setOwnerId(final Long ownerId) {
        this.ownerId = ownerId;
    }

    @Override
    @SuppressWarnings("all")
    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof ChatDTO)) return false;
        final ChatDTO other = (ChatDTO) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$chatId = this.getChatId();
        final Object other$chatId = other.getChatId();
        if (this$chatId == null ? other$chatId != null : !this$chatId.equals(other$chatId)) return false;
        final Object this$ownerId = this.getOwnerId();
        final Object other$ownerId = other.getOwnerId();
        if (this$ownerId == null ? other$ownerId != null : !this$ownerId.equals(other$ownerId)) return false;
        final Object this$chatName = this.getChatName();
        final Object other$chatName = other.getChatName();
        if (this$chatName == null ? other$chatName != null : !this$chatName.equals(other$chatName)) return false;
        final Object this$displayName = this.getDisplayName();
        final Object other$displayName = other.getDisplayName();
        if (this$displayName == null ? other$displayName != null : !this$displayName.equals(other$displayName)) return false;
        return true;
    }

    @SuppressWarnings("all")
    protected boolean canEqual(final Object other) {
        return other instanceof ChatDTO;
    }

    @Override
    @SuppressWarnings("all")
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $chatId = this.getChatId();
        result = result * PRIME + ($chatId == null ? 43 : $chatId.hashCode());
        final Object $ownerId = this.getOwnerId();
        result = result * PRIME + ($ownerId == null ? 43 : $ownerId.hashCode());
        final Object $chatName = this.getChatName();
        result = result * PRIME + ($chatName == null ? 43 : $chatName.hashCode());
        final Object $displayName = this.getDisplayName();
        result = result * PRIME + ($displayName == null ? 43 : $displayName.hashCode());
        return result;
    }

    @Override
    @SuppressWarnings("all")
    public String toString() {
        return "ChatDTO(chatId=" + this.getChatId() + ", chatName=" + this.getChatName() + ", displayName=" + this.getDisplayName() + ", ownerId=" + this.getOwnerId() + ")";
    }
    //</editor-fold>
}
