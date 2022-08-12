package com.al3xkras.messenger.message_service.controller;

import com.al3xkras.messenger.message_service.service.MessageService;
import com.al3xkras.messenger.dto.MessageDTO;
import com.al3xkras.messenger.entity.ChatMessage;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@WebMvcTest
@ActiveProfiles("test")
class MessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MessageService messageService;



    @Test
    void sendMessage() {

        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        MessageDTO valid1 = MessageDTO.builder()
                .chatId(1L)
                .userId(1L)
                .submissionDate(now)
                .message("message")
                .build();

        ChatMessage message = ChatMessage.builder()
                .chatId(valid1.getChatId())
                .userId(valid1.getUserId())
                .submissionDate(valid1.getSubmissionDate())
                .message(valid1.getMessage())
                .build();

        Mockito.when(messageService.saveMessage(message))
                .thenReturn(message);

    }

    @Test
    void findAllMessages() {
    }

    @Test
    void editMessage() {
    }

    @Test
    void deleteMessage() {
    }
}