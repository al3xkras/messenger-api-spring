package com.al3xkras.messenger.user_service.entity;

import com.al3xkras.messenger.user_service.model.ChatUserId;
import com.al3xkras.messenger.user_service.model.ChatUserRole;
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

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id",
            referencedColumnName = "messenger_user_id", insertable = false, updatable = false)
    @ToString.Exclude
    private MessengerUser messengerUser;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "chat_id",
            referencedColumnName = "chat_id", insertable = false, updatable = false)
    @ToString.Exclude
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


}
