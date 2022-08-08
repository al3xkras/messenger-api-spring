package com.al3xkras.messengermessageservice.entity;



import com.al3xkras.messengermessageservice.model.ChatMessageId;
import lombok.*;

import javax.persistence.*;
import java.util.Date;


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
    private Date submissionDate;

    @Column(name = "message_str",columnDefinition = "nvarchar(255)",nullable = false)
    private String message;

}
