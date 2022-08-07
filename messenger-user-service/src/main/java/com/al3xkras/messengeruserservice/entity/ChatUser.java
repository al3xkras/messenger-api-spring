package com.al3xkras.messengeruserservice.entity;

import com.al3xkras.messengeruserservice.model.ChatUserId;
import com.al3xkras.messengeruserservice.model.ChatUserRole;
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

    private String title;
    private ChatUserRole chatUserRole;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id",
            referencedColumnName = "messenger_user_id", insertable = false, updatable = false)
    @ToString.Exclude
    private MessengerUser messengerUser;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "chat_id",
            referencedColumnName = "chat_id", insertable = false, updatable = false)
    @ToString.Exclude
    private Chat chat;


}
