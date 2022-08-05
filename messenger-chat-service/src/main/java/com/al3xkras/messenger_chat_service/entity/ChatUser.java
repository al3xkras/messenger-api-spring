package com.al3xkras.messenger_chat_service.entity;

import com.al3xkras.messenger_chat_service.model.ChatUserId;
import com.al3xkras.messenger_chat_service.model.ChatUserRole;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "chat_user",
        uniqueConstraints = @UniqueConstraint(name = "chat_user_un",
                columnNames = {"chat_id", "user_id"})
)
@Getter
@Setter
@ToString
@NoArgsConstructor
@Builder
@IdClass(ChatUserId.class)
public class ChatUser {
    @Id
    @JoinColumn(name = "chat_id",
            referencedColumnName = "chat_id",
            table = "chat",nullable = false)
    private Long chatId;
    @Id
    @JoinColumn(name = "user_id",
            referencedColumnName = "messenger_user_id",
            table = "messenger_user",nullable = false)
    private Long userId;
    private String title;
    private ChatUserRole chatUserRole;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    @ToString.Exclude
    private MessengerUser messengerUser;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "chat_id", insertable = false, updatable = false)
    @ToString.Exclude
    private Chat chat;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ChatUser chatUser = (ChatUser) o;
        return chatId != null && Objects.equals(chatId, chatUser.chatId)
                && userId != null && Objects.equals(userId, chatUser.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chatId, userId);
    }
}
