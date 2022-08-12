package com.al3xkras.messenger.entity;

import javax.persistence.*;

@Entity
@Table(name = "chat", uniqueConstraints = @UniqueConstraint(name = "chat_name_un", columnNames = "chat_name"))
public class Chat {
    @Id
    @SequenceGenerator(name = "chat_seq", sequenceName = "chat_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "chat_seq")
    @Column(name = "chat_id", nullable = false)
    private Long chatId;
    @Column(name = "chat_name", columnDefinition = "varchar(30)", nullable = false)
    private String chatName;
    @Column(name = "chat_display_name", columnDefinition = "nvarchar(50)", nullable = false)
    private String chatDisplayName;

    @PrePersist
    void beforePersist() {
        chatId = null;
    }


    //<editor-fold defaultstate="collapsed" desc="delombok">
    @SuppressWarnings("all")
    public static class ChatBuilder {
        @SuppressWarnings("all")
        private Long chatId;
        @SuppressWarnings("all")
        private String chatName;
        @SuppressWarnings("all")
        private String chatDisplayName;

        @SuppressWarnings("all")
        ChatBuilder() {
        }

        @SuppressWarnings("all")
        public Chat.ChatBuilder chatId(final Long chatId) {
            this.chatId = chatId;
            return this;
        }

        @SuppressWarnings("all")
        public Chat.ChatBuilder chatName(final String chatName) {
            this.chatName = chatName;
            return this;
        }

        @SuppressWarnings("all")
        public Chat.ChatBuilder chatDisplayName(final String chatDisplayName) {
            this.chatDisplayName = chatDisplayName;
            return this;
        }

        @SuppressWarnings("all")
        public Chat build() {
            return new Chat(this.chatId, this.chatName, this.chatDisplayName);
        }

        @Override
        @SuppressWarnings("all")
        public String toString() {
            return "Chat.ChatBuilder(chatId=" + this.chatId + ", chatName=" + this.chatName + ", chatDisplayName=" + this.chatDisplayName + ")";
        }
    }

    @SuppressWarnings("all")
    public static Chat.ChatBuilder builder() {
        return new Chat.ChatBuilder();
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
    public String getChatDisplayName() {
        return this.chatDisplayName;
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
    public void setChatDisplayName(final String chatDisplayName) {
        this.chatDisplayName = chatDisplayName;
    }

    @Override
    @SuppressWarnings("all")
    public String toString() {
        return "Chat(chatId=" + this.getChatId() + ", chatName=" + this.getChatName() + ", chatDisplayName=" + this.getChatDisplayName() + ")";
    }

    @SuppressWarnings("all")
    public Chat() {
    }

    @SuppressWarnings("all")
    public Chat(final Long chatId, final String chatName, final String chatDisplayName) {
        this.chatId = chatId;
        this.chatName = chatName;
        this.chatDisplayName = chatDisplayName;
    }

    @Override
    @SuppressWarnings("all")
    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof Chat)) return false;
        final Chat other = (Chat) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$chatId = this.getChatId();
        final Object other$chatId = other.getChatId();
        if (this$chatId == null ? other$chatId != null : !this$chatId.equals(other$chatId)) return false;
        final Object this$chatName = this.getChatName();
        final Object other$chatName = other.getChatName();
        if (this$chatName == null ? other$chatName != null : !this$chatName.equals(other$chatName)) return false;
        final Object this$chatDisplayName = this.getChatDisplayName();
        final Object other$chatDisplayName = other.getChatDisplayName();
        if (this$chatDisplayName == null ? other$chatDisplayName != null : !this$chatDisplayName.equals(other$chatDisplayName)) return false;
        return true;
    }

    @SuppressWarnings("all")
    protected boolean canEqual(final Object other) {
        return other instanceof Chat;
    }

    @Override
    @SuppressWarnings("all")
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $chatId = this.getChatId();
        result = result * PRIME + ($chatId == null ? 43 : $chatId.hashCode());
        final Object $chatName = this.getChatName();
        result = result * PRIME + ($chatName == null ? 43 : $chatName.hashCode());
        final Object $chatDisplayName = this.getChatDisplayName();
        result = result * PRIME + ($chatDisplayName == null ? 43 : $chatDisplayName.hashCode());
        return result;
    }
    //</editor-fold>
}
