package com.al3xkras.messenger.message_service.entity;


import com.al3xkras.messenger.message_service.model.ChatMessageId;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;


@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "message")
@IdClass(ChatMessageId.class)
@EqualsAndHashCode
public class ChatMessage {
    @Id
    @Column(name = "chat_id", nullable = false)
    private Long chatId;
    @Id
    @Column(name = "user_id", nullable = false)
    private Long userId;
    @Id
    @Column(name = "submission_date", columnDefinition = "DATETIME", nullable = false)
    private LocalDateTime submissionDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id", referencedColumnName = "chat_id", insertable = false, updatable = false)
    private Chat chat;

    @Column(name = "message_str",columnDefinition = "nvarchar(255)",nullable = false)
    private String message;

    @PrePersist
    void beforePersist(){
        submissionDate = submissionDate.truncatedTo(ChronoUnit.SECONDS);
    }

}
