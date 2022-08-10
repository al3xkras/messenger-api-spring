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
@EqualsAndHashCode
public class Chat {

    @Id
    @SequenceGenerator(name = "chat_seq",sequenceName = "chat_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "chat_seq")
    @Column(name = "chat_id", nullable = false)
    private Long chatId;
    @Column(name = "chat_name", columnDefinition = "varchar(30)", nullable = false)
    private String chatName;
    @Column(name = "chat_display_name", columnDefinition = "nvarchar(50)", nullable = false)
    private String chatDisplayName;

    @PrePersist
    void beforePersist(){
        chatId = null;
    }
}
