package com.al3xkras.messenger.entity;

import com.al3xkras.messenger.model.ChatUserId;
import com.al3xkras.messenger.model.ChatUserRole;

import javax.persistence.*;

@Entity
@Table(name = "chat_user", uniqueConstraints = @UniqueConstraint(name = "chat_user_un", columnNames = {"chat_id", "user_id"}))
@IdClass(ChatUserId.class)
public class ChatUser {
    @Id
    @Column(name = "chat_id", nullable = false)
    private Long chatId;
    @Id
    @Column(name = "user_id", nullable = false)
    private Long userId;
    @Column(name = "title", columnDefinition = "nvarchar(20)")
    private String title;
    @Enumerated(EnumType.STRING)
    @Column(name = "chat_user_role", nullable = false)
    private ChatUserRole chatUserRole;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", referencedColumnName = "messenger_user_id", insertable = false, updatable = false)
    private MessengerUser messengerUser;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "chat_id", referencedColumnName = "chat_id", insertable = false, updatable = false)
    private Chat chat;

    @PrePersist
    private void beforePersist() {
        if (chatId == null && chat != null && chat.getChatId() != null) {
            chatId = chat.getChatId();
        }
        if (userId == null && messengerUser != null && messengerUser.getMessengerUserId() != null) {
            userId = messengerUser.getMessengerUserId();
        }
    }

    public Long getChatId() {
        if (chatId == null) if (chat != null && chat.getChatId() != null) return chat.getChatId();
        return chatId;
    }

    public Long getUserId() {
        if (userId == null) if (messengerUser != null && messengerUser.getMessengerUserId() != null) return messengerUser.getMessengerUserId();
        return userId;
    }


    //<editor-fold defaultstate="collapsed" desc="delombok">
    @SuppressWarnings("all")
    public static class ChatUserBuilder {
        @SuppressWarnings("all")
        private Long chatId;
        @SuppressWarnings("all")
        private Long userId;
        @SuppressWarnings("all")
        private String title;
        @SuppressWarnings("all")
        private ChatUserRole chatUserRole;
        @SuppressWarnings("all")
        private MessengerUser messengerUser;
        @SuppressWarnings("all")
        private Chat chat;

        @SuppressWarnings("all")
        ChatUserBuilder() {
        }

        @SuppressWarnings("all")
        public ChatUser.ChatUserBuilder chatId(final Long chatId) {
            this.chatId = chatId;
            return this;
        }

        @SuppressWarnings("all")
        public ChatUser.ChatUserBuilder userId(final Long userId) {
            this.userId = userId;
            return this;
        }

        @SuppressWarnings("all")
        public ChatUser.ChatUserBuilder title(final String title) {
            this.title = title;
            return this;
        }

        @SuppressWarnings("all")
        public ChatUser.ChatUserBuilder chatUserRole(final ChatUserRole chatUserRole) {
            this.chatUserRole = chatUserRole;
            return this;
        }

        @SuppressWarnings("all")
        public ChatUser.ChatUserBuilder messengerUser(final MessengerUser messengerUser) {
            this.messengerUser = messengerUser;
            return this;
        }

        @SuppressWarnings("all")
        public ChatUser.ChatUserBuilder chat(final Chat chat) {
            this.chat = chat;
            return this;
        }

        @SuppressWarnings("all")
        public ChatUser build() {
            return new ChatUser(this.chatId, this.userId, this.title, this.chatUserRole, this.messengerUser, this.chat);
        }

        @Override
        @SuppressWarnings("all")
        public String toString() {
            return "ChatUser.ChatUserBuilder(chatId=" + this.chatId + ", userId=" + this.userId + ", title=" + this.title + ", chatUserRole=" + this.chatUserRole + ", messengerUser=" + this.messengerUser + ", chat=" + this.chat + ")";
        }
    }

    @SuppressWarnings("all")
    public static ChatUser.ChatUserBuilder builder() {
        return new ChatUser.ChatUserBuilder();
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
    public MessengerUser getMessengerUser() {
        return this.messengerUser;
    }

    @SuppressWarnings("all")
    public Chat getChat() {
        return this.chat;
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

    @SuppressWarnings("all")
    public void setMessengerUser(final MessengerUser messengerUser) {
        this.messengerUser = messengerUser;
    }

    @SuppressWarnings("all")
    public void setChat(final Chat chat) {
        this.chat = chat;
    }

    @Override
    @SuppressWarnings("all")
    public String toString() {
        return "ChatUser(chatId=" + this.getChatId() + ", userId=" + this.getUserId() + ", title=" + this.getTitle() + ", chatUserRole=" + this.getChatUserRole() + ", messengerUser=" + this.getMessengerUser() + ", chat=" + this.getChat() + ")";
    }

    @SuppressWarnings("all")
    public ChatUser() {
    }

    @SuppressWarnings("all")
    public ChatUser(final Long chatId, final Long userId, final String title, final ChatUserRole chatUserRole, final MessengerUser messengerUser, final Chat chat) {
        this.chatId = chatId;
        this.userId = userId;
        this.title = title;
        this.chatUserRole = chatUserRole;
        this.messengerUser = messengerUser;
        this.chat = chat;
    }

    @Override
    @SuppressWarnings("all")
    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof ChatUser)) return false;
        final ChatUser other = (ChatUser) o;
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
        final Object this$messengerUser = this.getMessengerUser();
        final Object other$messengerUser = other.getMessengerUser();
        if (this$messengerUser == null ? other$messengerUser != null : !this$messengerUser.equals(other$messengerUser)) return false;
        final Object this$chat = this.getChat();
        final Object other$chat = other.getChat();
        if (this$chat == null ? other$chat != null : !this$chat.equals(other$chat)) return false;
        return true;
    }

    @SuppressWarnings("all")
    protected boolean canEqual(final Object other) {
        return other instanceof ChatUser;
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
        final Object $messengerUser = this.getMessengerUser();
        result = result * PRIME + ($messengerUser == null ? 43 : $messengerUser.hashCode());
        final Object $chat = this.getChat();
        result = result * PRIME + ($chat == null ? 43 : $chat.hashCode());
        return result;
    }
    //</editor-fold>
}
