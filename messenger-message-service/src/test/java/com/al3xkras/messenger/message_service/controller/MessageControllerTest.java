package com.al3xkras.messenger.message_service.controller;

import com.al3xkras.messenger.message_service.service.MessageService;
import com.al3xkras.messenger.dto.MessageDTO;
import com.al3xkras.messenger.entity.ChatMessage;
import com.al3xkras.messenger.model.ChatMessageId;
import com.al3xkras.messenger.model.ChatUserRole;
import com.al3xkras.messenger.model.security.ChatUserAuthenticationToken;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MessageControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MessageService messageService;

    static ChatUserAuthenticationToken token;
    @BeforeEach
    void setUp(){
        token = new ChatUserAuthenticationToken(
                "user1",1L,"chat1",
                ChatUserRole.SUPER_ADMIN.authorities());
        token.setChatId(1L);
        SecurityContextHolder.getContext().setAuthentication(token);
    }

    @Test
    void testSendMessage() throws Exception {

        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        MessageDTO valid1 = MessageDTO.builder()
                .chatId(1L)
                .userId(1L)
                .submissionDate(now)
                .message("message")
                .build();

        ChatMessage message1 = ChatMessage.builder()
                .chatId(valid1.getChatId())
                .userId(valid1.getUserId())
                .submissionDate(valid1.getSubmissionDate())
                .message(valid1.getMessage())
                .build();

        Mockito.when(messageService.saveMessage(message1))
                .thenReturn(message1);

        mockMvc.perform(post("/message")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(valid1)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(message1)));

        MessageDTO forbiddenChatMessage1 = MessageDTO.builder()
                .chatId(2L)
                .userId(1L)
                .submissionDate(now)
                .message("message")
                .build();
        MessageDTO forbiddenChatMessage2 = MessageDTO.builder()
                .chatId(1L)
                .userId(2L)
                .submissionDate(now)
                .message("message")
                .build();
        mockMvc.perform(post("/message")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(forbiddenChatMessage1)))
                .andExpect(status().isForbidden());
        mockMvc.perform(post("/message")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(forbiddenChatMessage2)))
                .andExpect(status().isForbidden());

    }

    @Test
    void testFindMessageById() throws Exception {

        LocalDateTime now = LocalDateTime.now();

        ChatMessage valid1 = ChatMessage.builder()
                .chatId(token.getChatId())
                .userId(token.getUserId())
                .submissionDate(now)
                .message("message")
                .build();

        ChatMessage forbidden = ChatMessage.builder()
                .chatId(token.getChatId()+1)
                .userId(token.getUserId())
                .submissionDate(now)
                .message("message")
                .build();

        ChatMessage valid2 = ChatMessage.builder()
                .chatId(token.getChatId())
                .userId(token.getUserId()+1)
                .submissionDate(now)
                .message("message")
                .build();

        Mockito.when(messageService.findById(new ChatMessageId(valid1.getChatId(),valid1.getUserId(),valid1.getSubmissionDate())))
                .thenReturn(valid1);
        Mockito.when(messageService.findById(new ChatMessageId(valid2.getChatId(),valid2.getUserId(),valid2.getSubmissionDate())))
                .thenReturn(valid2);

        mockMvc.perform(get("/message")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new ChatMessageId(valid1.getChatId(),valid1.getUserId(),valid1.getSubmissionDate()))))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(valid1)));

        mockMvc.perform(get("/message")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new ChatMessageId(forbidden.getChatId(),forbidden.getUserId(),forbidden.getSubmissionDate()))))
                .andExpect(status().isForbidden());

        token.setChatId(100L);
        SecurityContextHolder.getContext().setAuthentication(token);

        mockMvc.perform(get("/message")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ChatMessageId(valid1.getChatId(),valid1.getUserId(),valid1.getSubmissionDate()))))
                .andExpect(status().isForbidden());

    }

}