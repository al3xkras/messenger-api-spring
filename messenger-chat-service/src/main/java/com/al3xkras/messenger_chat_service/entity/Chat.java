package com.al3xkras.messenger_chat_service.entity;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;


@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "chat", uniqueConstraints =
@UniqueConstraint(name = "chat_name_un",columnNames = "chat_name")
)
public class Chat {

    @Id
    @Column(name = "chat_id")
    private Long chatId;
    @Column(name = "chat_name")
    private String chatName;
    private String chatDisplayName;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Chat chat = (Chat) o;
        return chatId != null && Objects.equals(chatId, chat.chatId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
