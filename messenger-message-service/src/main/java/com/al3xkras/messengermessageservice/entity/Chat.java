package com.al3xkras.messengermessageservice.entity;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.Objects;

@Entity
@Table(name = "chat", uniqueConstraints =
    @UniqueConstraint(name = "chat_name_un",columnNames = "chat_name")
)
@Getter
@Setter
@ToString
@NoArgsConstructor
@Builder
public class Chat {

    @Id
    private Long chatId;
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
