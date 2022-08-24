package com.al3xkras.messenger.message_service;

import com.al3xkras.messenger.dto.*;
import com.al3xkras.messenger.entity.Chat;
import com.al3xkras.messenger.entity.ChatMessage;
import com.al3xkras.messenger.entity.ChatUser;
import com.al3xkras.messenger.entity.MessengerUser;
import com.al3xkras.messenger.model.ChatMessageId;
import com.al3xkras.messenger.model.ChatUserRole;
import com.al3xkras.messenger.model.MessengerUserType;
import com.al3xkras.messenger.model.RestResponsePage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MessengerMessageServiceApplicationTest {

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    static MessengerUser firstUser = MessengerUser.FIRST_ADMIN;

    static MessengerUser secondUser = MessengerUser.builder()
            .username("user2")
            .name("Andrew")
            .emailAddress("andrew@gmail.com")
            .phoneNumber("+43 116-22-45")
            .messengerUserType(MessengerUserType.USER)
            .build();
    static MessengerUser thirdUser = MessengerUser.builder()
            .username("user3")
            .name("Mike")
            .emailAddress("mike@gmail.com")
            .phoneNumber("+44 456-75-45")
            .messengerUserType(MessengerUserType.USER)
            .build();

    static Chat firstChat = Chat.builder()
            .chatName("first_chat")
            .chatDisplayName("First Chat!")
            .build();

    static Chat secondChat = Chat.builder()
            .chatName("second_chat")
            .chatDisplayName("Second Chat!")
            .build();

    static  ChatUser chatUser11;
    static ChatUser chatUser12;

    static ChatUser chatUser21;
    static ChatUser chatUser22;
    static ChatUser chatUser23;


    static LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
    static LocalDateTime then = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusSeconds(2);

    static ChatMessage message1;
    static ChatMessage message2;
    static ChatMessage message3;
    static ChatMessage message4;
    static ChatMessage message5;

    static void updateChatUsers(){
        chatUser11 = ChatUser.builder()
                .chat(firstChat)
                .messengerUser(firstUser)
                .title("Admin")
                .chatUserRole(ChatUserRole.ADMIN)
                .build();

        chatUser12 = ChatUser.builder()
                .chat(firstChat)
                .messengerUser(secondUser)
                .chatUserRole(ChatUserRole.USER)
                .build();

        chatUser21 = ChatUser.builder()
                .chat(secondChat)
                .messengerUser(secondUser)
                .title("Admin")
                .chatUserRole(ChatUserRole.ADMIN)
                .build();

        chatUser22 = ChatUser.builder()
                .chat(secondChat)
                .messengerUser(firstUser)
                .chatUserRole(ChatUserRole.USER)
                .build();

        chatUser23 = ChatUser.builder()
                .chat(secondChat)
                .messengerUser(thirdUser)
                .chatUserRole(ChatUserRole.USER)
                .build();


        message1 = ChatMessage.builder()
                .chatId(chatUser11.getChatId())
                .userId(chatUser11.getUserId())
                .submissionDate(now)
                .message("Hello world!")
                .build();

        message2 = ChatMessage.builder()
                .chatId(chatUser12.getChatId())
                .userId(chatUser12.getUserId())
                .submissionDate(now)
                .message("Hi")
                .build();

        message3 = ChatMessage.builder()
                .chatId(chatUser12.getChatId())
                .userId(chatUser12.getUserId())
                .submissionDate(then)
                .message("mmm")
                .build();

        message4 = ChatMessage.builder()
                .chatId(chatUser21.getChatId())
                .userId(chatUser21.getUserId())
                .submissionDate(now)
                .message("message")
                .build();
        message5 = ChatMessage.builder()
                .chatId(chatUser23.getChatId())
                .userId(chatUser23.getUserId())
                .submissionDate(then)
                .message("wow")
                .build();
    }


    @Test
    @Order(1)
    void persistUsers() throws Exception{
        MessengerUserDTO validUserDto1 = MessengerUserDTO.builder()
                .username(firstUser.getUsername())
                .name(firstUser.getName())
                .password("1a83F_23.")
                .surname(firstUser.getSurname())
                .phoneNumber(firstUser.getPhoneNumber())
                .email(firstUser.getEmailAddress())
                .messengerUserType(firstUser.getMessengerUserType())
                .build();
        MessengerUserDTO validUserDto2 = MessengerUserDTO.builder()
                .username(secondUser.getUsername())
                .name(secondUser.getName())
                .password("1a83F_23.")
                .surname(secondUser.getSurname())
                .phoneNumber(secondUser.getPhoneNumber())
                .email(secondUser.getEmailAddress())
                .messengerUserType(secondUser.getMessengerUserType())
                .build();
        MessengerUserDTO validUserDto3 = MessengerUserDTO.builder()
                .username(thirdUser.getUsername())
                .name(thirdUser.getName())
                .password("1a83F_23.")
                .surname(thirdUser.getSurname())
                .phoneNumber(thirdUser.getPhoneNumber())
                .email(thirdUser.getEmailAddress())
                .messengerUserType(thirdUser.getMessengerUserType())
                .build();

        ResponseEntity<MessengerUser> response;
        try {
            response = restTemplate.exchange(RequestEntity.post("http://localhost:10001/user")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(objectMapper.writeValueAsString(validUserDto1)), MessengerUser.class);
            assertNotNull(response.getBody());
            firstUser = response.getBody();
            assertNotNull(firstUser.getMessengerUserId());

            response = restTemplate.exchange(RequestEntity.post("http://localhost:10001/user")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(objectMapper.writeValueAsString(validUserDto2)), MessengerUser.class);
            assertNotNull(response.getBody());
            secondUser = response.getBody();
            assertNotNull(secondUser.getMessengerUserId());

            response = restTemplate.exchange(RequestEntity.post("http://localhost:10001/user")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(objectMapper.writeValueAsString(validUserDto3)), MessengerUser.class);
            assertNotNull(response.getBody());
            thirdUser = response.getBody();
            assertNotNull(thirdUser.getMessengerUserId());

        } catch (ResourceAccessException r){
            log.error("messenger user service is down. please enable it");
            fail();
        }
    }

    @Test
    @Order(2)
    void persistChats() {
        @Valid
        ChatDTO validDto1 = ChatDTO.builder()
                .chatName(firstChat.getChatName())
                .displayName(firstChat.getChatDisplayName())
                .ownerId(firstUser.getMessengerUserId())
                .build();
        @Valid
        ChatDTO validDto2 = ChatDTO.builder()
                .chatName(secondChat.getChatName())
                .displayName(secondChat.getChatDisplayName())
                .ownerId(secondUser.getMessengerUserId())
                .build();

        try {
            URI createChat = URI.create("http://localhost:10002/chat");

            RequestEntity<String> requestEntity = RequestEntity.post(createChat)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(objectMapper.writeValueAsString(validDto1));
            ResponseEntity<Chat> response = restTemplate.exchange(requestEntity,Chat.class);
            assertNotNull(response.getBody());
            firstChat=response.getBody();

            requestEntity = RequestEntity.post(createChat)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(objectMapper.writeValueAsString(validDto2));
            response = restTemplate.exchange(requestEntity,Chat.class);
            assertNotNull(response.getBody());
            secondChat = response.getBody();
        } catch (ResourceAccessException | JsonProcessingException r){
            log.error("chat service is down. please enable it");
            fail();
        }
    }

    @Test
    @Order(3)
    void persistChatUsers(){

        updateChatUsers();
        ChatUserDTO chatUserDTO12 = ChatUserDTO.builder()
                .chatId(chatUser12.getChatId())
                .userId(chatUser12.getUserId())
                .title(chatUser12.getTitle())
                .chatUserRole(chatUser12.getChatUserRole())
                .build();
        ChatUserDTO chatUserDTO22 = ChatUserDTO.builder()
                .chatId(chatUser22.getChatId())
                .userId(chatUser22.getUserId())
                .title(chatUser22.getTitle())
                .chatUserRole(chatUser22.getChatUserRole())
                .build();
        ChatUserDTO chatUserDTO23 = ChatUserDTO.builder()
                .chatId(chatUser23.getChatId())
                .userId(chatUser23.getUserId())
                .title(chatUser23.getTitle())
                .chatUserRole(chatUser23.getChatUserRole())
                .build();

        try {
            URI addChatUser = URI.create("http://localhost:10002/chat/users");

            RequestEntity<String> requestEntity = RequestEntity.post(addChatUser)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(objectMapper.writeValueAsString(chatUserDTO12));
            ResponseEntity<ChatUser> response = restTemplate.exchange(requestEntity,ChatUser.class);
            assertNotNull(response.getBody());
            chatUser12=response.getBody();

            requestEntity = RequestEntity.post(addChatUser)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(objectMapper.writeValueAsString(chatUserDTO22));
            response = restTemplate.exchange(requestEntity,ChatUser.class);
            assertNotNull(response.getBody());
            chatUser22=response.getBody();

            requestEntity = RequestEntity.post(addChatUser)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(objectMapper.writeValueAsString(chatUserDTO23));
            response = restTemplate.exchange(requestEntity,ChatUser.class);
            assertNotNull(response.getBody());
            chatUser23=response.getBody();

        } catch (ResourceAccessException | JsonProcessingException r){
            log.error("chat service is down. please enable it");
            fail();
        }
    }

    @Test
    @Order(4)
    void testSendMessage() throws Exception {
        updateChatUsers();
        MessageDTO validMessage1 = MessageDTO.builder()
                .chatId(message1.getChatId())
                .userId(message1.getUserId())
                .submissionDate(message1.getSubmissionDate())
                .message(message1.getMessage())
                .build();
        MessageDTO validMessage2 = MessageDTO.builder()
                .chatId(message2.getChatId())
                .userId(message2.getUserId())
                .submissionDate(message2.getSubmissionDate())
                .message(message2.getMessage())
                .build();
        MessageDTO validMessage3 = MessageDTO.builder()
                .chatId(message3.getChatId())
                .userId(message3.getUserId())
                .submissionDate(message3.getSubmissionDate())
                .message(message3.getMessage())
                .build();
        MessageDTO validMessage4 = MessageDTO.builder()
                .chatId(message4.getChatId())
                .userId(message4.getUserId())
                .submissionDate(message4.getSubmissionDate())
                .message(message4.getMessage())
                .build();
        MessageDTO validMessage5 = MessageDTO.builder()
                .chatId(message5.getChatId())
                .userId(message5.getUserId())
                .submissionDate(message5.getSubmissionDate())
                .message(message5.getMessage())
                .build();

        MessageDTO invalidIdMessageDto1 = MessageDTO.builder()
                .chatId(chatUser11.getChatId())
                .userId(chatUser11.getUserId())
                .message("m")
                .build();
        MessageDTO invalidIdMessageDto2 = MessageDTO.builder()
                .chatId(chatUser11.getChatId())
                .submissionDate(then)
                .message("m")
                .build();
        MessageDTO invalidIdMessageDto3 = MessageDTO.builder()
                .chatId(2050L)
                .userId(firstUser.getMessengerUserId())
                .submissionDate(then)
                .message("m")
                .build();
        MessageDTO invalidIdMessageDto4 = MessageDTO.builder()
                .chatId(firstChat.getChatId())
                .userId(999L)
                .submissionDate(now)
                .message("m")
                .build();

        MessageDTO invalidMessageDto = MessageDTO.builder()
                .chatId(chatUser12.getChatId())
                .userId(chatUser12.getUserId())
                .submissionDate(now)
                .build();

        MessageDTO existingIdMessageDto = MessageDTO.builder()
                .chatId(chatUser12.getChatId())
                .userId(chatUser12.getUserId())
                .submissionDate(then)
                .message(":(")
                .build();


        mockMvc.perform(post("/message")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validMessage1)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(message1)));

        mockMvc.perform(post("/message")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validMessage2)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(message2)));

        mockMvc.perform(post("/message")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validMessage3)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(message3)));

        mockMvc.perform(post("/message")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validMessage4)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(message4)));

        mockMvc.perform(post("/message")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validMessage5)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(message5)));


        mockMvc.perform(post("/message")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidIdMessageDto1)))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post("/message")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidIdMessageDto2)))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post("/message")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidIdMessageDto3)))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post("/message")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidIdMessageDto4)))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post("/message")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidMessageDto)))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post("/message")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(existingIdMessageDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(5)
    void testEditMessage() throws Exception {
        MessageDTO valid1 = MessageDTO.builder()
                .chatId(chatUser11.getChatId())
                .userId(chatUser11.getUserId())
                .submissionDate(now)
                .message("Hello")
                .build();

        MessageDTO valid4 = MessageDTO.builder()
                .chatId(chatUser21.getChatId())
                .userId(chatUser21.getUserId())
                .submissionDate(now)
                .message("New message")
                .build();

        ChatMessage message1afterUpdate = ChatMessage.builder()
                .chatId(chatUser11.getChatId())
                .userId(chatUser11.getUserId())
                .submissionDate(now)
                .message("Hello")
                .build();

        ChatMessage message4afterUpdate = ChatMessage.builder()
                .chatId(chatUser21.getChatId())
                .userId(chatUser21.getUserId())
                .submissionDate(now)
                .message("New message")
                .build();

        mockMvc.perform(put("/message")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(valid1)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(message1afterUpdate)));
        message1=message1afterUpdate;

        mockMvc.perform(put("/message")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(valid4)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(message4afterUpdate)));
        message4=message4afterUpdate;


        MessageDTO invalidIdDto1 = MessageDTO.builder()
                .chatId(890L)
                .userId(firstUser.getMessengerUserId())
                .submissionDate(now)
                .message("abc")
                .build();
        MessageDTO invalidIdDto2 = MessageDTO.builder()
                .chatId(firstChat.getChatId())
                .userId(thirdUser.getMessengerUserId())
                .submissionDate(now)
                .message("abc")
                .build();
        MessageDTO invalidIdDto3 = MessageDTO.builder()
                .chatId(firstChat.getChatId())
                .userId(firstUser.getMessengerUserId())
                .submissionDate(then)
                .message("abc")
                .build();

        mockMvc.perform(put("/message")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidIdDto1)))
                .andExpect(status().isBadRequest());

        mockMvc.perform(put("/message")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidIdDto2)))
                .andExpect(status().isBadRequest());

        mockMvc.perform(put("/message")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidIdDto3)))
                .andExpect(status().isBadRequest());

    }

    @Test
    @Order(6)
    void testFindById() throws Exception{

        ChatMessageId updated1 = new ChatMessageId();
        updated1.setChatId(message1.getChatId());
        updated1.setUserId(message1.getUserId());
        updated1.setSubmissionDate(message1.getSubmissionDate());

        ChatMessageId id3 = new ChatMessageId();
        id3.setChatId(message3.getChatId());
        id3.setUserId(message3.getUserId());
        id3.setSubmissionDate(message3.getSubmissionDate());

        ChatMessageId updated4 = new ChatMessageId();
        updated4.setChatId(message4.getChatId());
        updated4.setUserId(message4.getUserId());
        updated4.setSubmissionDate(message4.getSubmissionDate());

        ChatMessageId invalidId = new ChatMessageId();
        invalidId.setChatId(chatUser11.getChatId());
        invalidId.setUserId(chatUser11.getUserId());
        invalidId.setSubmissionDate(then);

        mockMvc.perform(get("/message")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updated1)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(message1)));

        mockMvc.perform(get("/message")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(id3)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(message3)));

        mockMvc.perform(get("/message")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated4)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(message4)));

        mockMvc.perform(get("/message")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidId)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(7)
    void testFindAllMessages() throws Exception {

        TypeReference<RestResponsePage<ChatMessage>> typeRef =
                new TypeReference<RestResponsePage<ChatMessage>>(){};

        PageRequestDto firstPageOfSize2 = new PageRequestDto(0,2);
        PageRequestDto secondPageOfSize2 = new PageRequestDto(1,2);
        PageRequestDto firstPageOfSize3 = new PageRequestDto(0,3);
        PageRequestDto secondPageOfSize3 = new PageRequestDto(1,3);

        Set<ChatMessage> expected1 = new HashSet<>(Arrays.asList(
                message1,message2
        ));
        Set<ChatMessage> expected2 = new HashSet<>(Arrays.asList(
                message3
        ));
        Set<ChatMessage> expected3 = new HashSet<>(Arrays.asList(
                message1,message2,message3
        ));
        Set<ChatMessage> expected4 = new HashSet<>(Arrays.asList(
                message4,message5
        ));
        Set<ChatMessage> expected5 = new HashSet<>();

        Set<ChatMessage> actual = new HashSet<>();
        mockMvc.perform(get("/messages")
                .param("chat-id",firstChat.getChatId().toString())
                        .param("chat-name","ThisNameWillBeIgnored")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(firstPageOfSize2)))
                .andExpect(status().isOk())
                .andDo(result -> {
                    RestResponsePage<ChatMessage> responseBody = objectMapper.readValue(result.getResponse().getContentAsString(),
                            typeRef);
                    assertNotNull(responseBody);
                    actual.addAll(responseBody.toList());
                });
        assertEquals(expected1,actual);

        actual.clear();
        mockMvc.perform(get("/messages")
                        .param("chat-name",firstChat.getChatName())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(secondPageOfSize2)))
                .andExpect(status().isOk())
                .andDo(result -> {
                    RestResponsePage<ChatMessage> responseBody = objectMapper.readValue(result.getResponse().getContentAsString(),
                            typeRef);
                    assertNotNull(responseBody);
                    actual.addAll(responseBody.toList());
                });
        assertEquals(expected2,actual);

        actual.clear();
        mockMvc.perform(get("/messages")
                        .param("chat-id",firstChat.getChatId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(firstPageOfSize3)))
                .andExpect(status().isOk())
                .andDo(result -> {
                    RestResponsePage<ChatMessage> responseBody = objectMapper.readValue(result.getResponse().getContentAsString(),
                            typeRef);
                    assertNotNull(responseBody);
                    actual.addAll(responseBody.toList());
                });
        assertEquals(expected3,actual);

        actual.clear();
        mockMvc.perform(get("/messages")
                        .param("chat-name",secondChat.getChatName())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(firstPageOfSize3)))
                .andExpect(status().isOk())
                .andDo(result -> {
                    RestResponsePage<ChatMessage> responseBody = objectMapper.readValue(result.getResponse().getContentAsString(),
                            typeRef);
                    assertNotNull(responseBody);
                    actual.addAll(responseBody.toList());
                });
        assertEquals(expected4,actual);

        actual.clear();
        mockMvc.perform(get("/messages")
                        .param("chat-name",secondChat.getChatName())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(secondPageOfSize3)))
                .andExpect(status().isOk())
                .andDo(result -> {
                    RestResponsePage<ChatMessage> responseBody = objectMapper.readValue(result.getResponse().getContentAsString(),
                            typeRef);
                    assertNotNull(responseBody);
                    actual.addAll(responseBody.toList());
                });
        assertEquals(expected5,actual);

        actual.clear();

        mockMvc.perform(get("/messages")
                .param("chat-name",secondChat.getChatName()))
                .andExpect(status().isBadRequest());
        mockMvc.perform(get("/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(secondPageOfSize3)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("please specify \"chat-id\" or \"chat-name\""));

        mockMvc.perform(get("/messages")
                .param("chat-name","noName")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(firstPageOfSize2)))
                .andExpect(status().isOk())
                .andDo(result -> {
                    RestResponsePage<ChatMessage> responseBody = objectMapper.readValue(result.getResponse().getContentAsString(),
                            typeRef);
                    assertNotNull(responseBody);
                    actual.addAll(responseBody.toList());
                });
        assertEquals(expected5,actual);
    }

    @Test
    @Order(8)
    void testDeleteMessage() throws Exception {

        ChatMessageId id4 = new ChatMessageId();
        id4.setChatId(message4.getChatId());
        id4.setUserId(message4.getUserId());
        id4.setSubmissionDate(message4.getSubmissionDate());

        ChatMessageId invalidId = new ChatMessageId();
        invalidId.setChatId(chatUser11.getChatId());
        invalidId.setUserId(chatUser11.getUserId());
        invalidId.setSubmissionDate(then);

        ChatMessageId deletedId = id4;

        mockMvc.perform(delete("/message")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(id4)))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/message")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidId)))
                .andExpect(status().isBadRequest());

        mockMvc.perform(delete("/message")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deletedId)))
                .andExpect(status().isBadRequest());
    }
}