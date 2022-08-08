package com.al3xkras.messengermessageservice.entity;

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
    @SequenceGenerator(name = "chat_seq",sequenceName = "chat_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "chat_seq")
    @Column(name = "chat_id", nullable = false)
    private Long chatId;
    @Column(name = "chat_name", columnDefinition = "varchar(15)", nullable = false)
    private String chatName;
    @Column(name = "chat_display_name", columnDefinition = "nvarchar(50)", nullable = false)
    private String chatDisplayName;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Chat chat = (Chat) o;
        return chatId != null && Objects.equals(chatId, chat.chatId);
    }

    @PrePersist
    void beforePersist(){
        chatId = null;
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
