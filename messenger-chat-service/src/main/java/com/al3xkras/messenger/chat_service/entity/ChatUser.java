package com.al3xkras.messenger.chat_service.entity;

import com.al3xkras.messenger.chat_service.model.ChatUserRole;
import com.al3xkras.messenger.chat_service.model.ChatUserId;
import lombok.*;

import javax.persistence.*;


@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@Entity
@Table(name = "chat_user",
        uniqueConstraints = @UniqueConstraint(name = "chat_user_un",
                columnNames = {"chat_id", "user_id"})
)
@IdClass(ChatUserId.class)
public class ChatUser {
    @Id
    @Column(name = "chat_id",nullable = false)
    private Long chatId;
    @Id
    @Column(name = "user_id",nullable = false)
    private Long userId;

    @Column(name = "title", columnDefinition = "nvarchar(20)")
    private String title;
    @Column(name = "chat_user_role", nullable = false)
    private ChatUserRole chatUserRole;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id",
            referencedColumnName = "messenger_user_id", insertable = false, updatable = false)
    private MessengerUser messengerUser;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "chat_id",
            referencedColumnName = "chat_id", insertable = false, updatable = false)
    private Chat chat;

    @PrePersist
    private void beforePersist(){
        if (chatId==null && chat!=null && chat.getChatId()!=null){
            chatId = chat.getChatId();
        }
        if (userId==null && messengerUser!=null && messengerUser.getMessengerUserId()!=null){
            userId = messengerUser.getMessengerUserId();
        }
    }

    public Long getChatId() {
        if (chatId==null)
            if (chat!=null && chat.getChatId()!=null)
                return chat.getChatId();
        return chatId;
    }

    public Long getUserId() {
        if (userId==null)
            if (messengerUser!=null && messengerUser.getMessengerUserId()!=null)
                return messengerUser.getMessengerUserId();
        return userId;
    }
}
