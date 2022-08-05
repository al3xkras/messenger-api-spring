package com.al3xkras.messengermessageservice.entity;

import com.al3xkras.messengermessageservice.model.ChatUserId;
import com.al3xkras.messengermessageservice.model.ChatUserRole;
import lombok.*;

import javax.persistence.*;

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
@EqualsAndHashCode
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
    @JoinColumn(name = "chat_id", insertable = false, updatable = false)
    @ToString.Exclude
    private Chat chat;

}
