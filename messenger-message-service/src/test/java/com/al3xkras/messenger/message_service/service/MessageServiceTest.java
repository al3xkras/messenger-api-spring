package com.al3xkras.messenger.message_service.service;

import com.al3xkras.messenger.message_service.entity.ChatMessage;
import com.al3xkras.messenger.message_service.exception.ChatMessageNotFoundException;
import com.al3xkras.messenger.message_service.model.ChatMessageId;
import com.al3xkras.messenger.message_service.repository.ChatMessageRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MessageServiceTest {

    @Autowired
    private MessageService messageService;

    @MockBean
    private ChatMessageRepository chatMessageRepository;

    @Test
    void testUpdateMessage() {
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        LocalDateTime then = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusSeconds(10);
        ChatMessage chatMessage = ChatMessage.builder()
                .chatId(1L)
                .userId(1L)
                .submissionDate(now)
                .message("some message")
                .build();
        ChatMessage chatMessage1 = ChatMessage.builder()
                .chatId(1L)
                .userId(1L)
                .submissionDate(then)
                .message("")
                .build();
        ChatMessage chatMessage2 = ChatMessage.builder()
                .chatId(2L)
                .userId(1L)
                .submissionDate(now)
                .message("")
                .build();
        Mockito.when(chatMessageRepository.findById(new ChatMessageId(1L,1L,now)))
                .thenReturn(Optional.of(chatMessage));
        Mockito.when(chatMessageRepository.findById(new ChatMessageId(1L,1L,then)))
                .thenReturn(Optional.empty());
        Mockito.when(chatMessageRepository.findById(new ChatMessageId(2L,1L,now)))
                .thenReturn(Optional.empty());

        ChatMessage updated = ChatMessage.builder()
                .chatId(chatMessage.getChatId())
                .userId(chatMessage.getUserId())
                .submissionDate(chatMessage.getSubmissionDate())
                .message(chatMessage.getMessage())
                .build();

        Mockito.when(chatMessageRepository.saveAndFlush(updated))
                .thenReturn(updated);

        assertEquals(updated,messageService.updateMessage(chatMessage));
        assertThrows(ChatMessageNotFoundException.class,()->{
            messageService.updateMessage(chatMessage1);
        });
        assertThrows(ChatMessageNotFoundException.class,()->{
            messageService.updateMessage(chatMessage2);
        });
    }
}