package com.al3xkras.messengermessageservice.entity;

import com.al3xkras.messengermessageservice.model.ChatMessageId;
import lombok.*;

import javax.persistence.*;
import java.util.Date;


@Getter
@Setter
@ToString
@NoArgsConstructor
@Builder
@Entity
@Table(name = "message")
@IdClass(ChatMessageId.class)
@EqualsAndHashCode
public class ChatMessage {
    @Id
    private Long chatId;
    @Id
    private Long userId;
    @Id
    @Column(name = "submission_date", columnDefinition = "DATE ")
    private Date submissionDate;

    private String message;

}
