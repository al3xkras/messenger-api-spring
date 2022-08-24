package com.al3xkras.messenger.chat_service.controller;

import com.al3xkras.messenger.chat_service.model.JwtAccessTokens;
import com.al3xkras.messenger.chat_service.service.ChatService;
import com.al3xkras.messenger.dto.ChatDTO;
import com.al3xkras.messenger.dto.ChatUserDTO;
import com.al3xkras.messenger.dto.PageRequestDto;
import com.al3xkras.messenger.entity.Chat;
import com.al3xkras.messenger.entity.ChatUser;
import com.al3xkras.messenger.entity.MessengerUser;
import com.al3xkras.messenger.model.ChatUserRole;
import com.al3xkras.messenger.model.MessengerUtils;
import com.al3xkras.messenger.model.security.ChatUserAuthenticationToken;
import com.al3xkras.messenger.model.security.JwtTokenAuth;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Collections;
import java.util.Optional;

import static com.al3xkras.messenger.model.MessengerUtils.Messages.EXCEPTION_ARGUMENT_ISNULL;
import static com.al3xkras.messenger.model.MessengerUtils.Messages.EXCEPTION_REQUIRED_PARAMETERS_ARE_NULL;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChatController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChatService chatService;
    @MockBean
    private RestTemplate restTemplate;
    @MockBean
    private JwtAccessTokens accessTokens;
    @MockBean
    private SecurityContext securityContext;

    static ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp(){
        ChatUserAuthenticationToken authenticationToken = new ChatUserAuthenticationToken("user1",1L,"name",
                ChatUserRole.SUPER_ADMIN.authorities());
        authenticationToken.setChatId(1L);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    @Test
    void testGetChatsByUser() throws Exception {
        Pageable pageable = PageRequest.of(0,1);
        Chat chat = Chat.builder().chatId(1L)
                .chatDisplayName("Chat 1")
                .chatName("chat1")
                .build();

        Page<Chat> chats = new PageImpl<>(Collections.singletonList(chat));
        Mockito.when(chatService.findAllByMessengerUserId(1L, pageable))
                .thenReturn(chats);
        Mockito.when(chatService.findAllByMessengerUserUsername("user1", pageable))
                .thenReturn(chats);

        PageRequestDto pageRequestDto = new PageRequestDto(pageable.getPageNumber(),pageable.getPageSize());

        mockMvc.perform(get("/chats")
                        .param("user-id","1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pageRequestDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(chats)));

        mockMvc.perform(get("/chats")
                        .param("username","user1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pageRequestDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(chats)));

        mockMvc.perform(get("/chats").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pageRequestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(String.format(EXCEPTION_REQUIRED_PARAMETERS_ARE_NULL.value(),
                        String.join(", ",JwtTokenAuth.Param.USER_ID.value(),JwtTokenAuth.Param.USERNAME.value()))));

        mockMvc.perform(get("/chats"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateChat() throws Exception {
        Chat chatToCreate = Chat.builder()
                .chatName("chat1")
                .chatDisplayName("Chat 1")
                .build();
        Chat chatCreated = Chat.builder()
                .chatId(1L)
                .chatName("chat1")
                .chatDisplayName("Chat 1")
                .build();

        MessengerUser creator = MessengerUser.builder()
                .messengerUserId(1L)
                .username("user1")
                .build();

        String accessToken = "someToken ______";
        String userServicePrefix = MessengerUtils.Property.USER_SERVICE_URI_PREFIX.value();
        URI uri = URI.create("http://localhost:10001"+userServicePrefix+"/user/"+1L);
        RequestEntity<?> requestEntity = RequestEntity.get(uri)
                .header(HttpHeaders.AUTHORIZATION, JwtTokenAuth.PREFIX_WITH_WHITESPACE+accessToken)
                .build();
        Mockito.when(accessTokens.getUserServiceAccessToken())
                .thenReturn(accessToken);
        Mockito.when(restTemplate.exchange(requestEntity,MessengerUser.class))
                .thenReturn(ResponseEntity.of(Optional.of(creator)));
        Mockito.when(chatService.saveChat(chatToCreate,creator))
                .thenReturn(chatCreated);

        ChatDTO chatDTO = ChatDTO.builder()
                .chatName(chatToCreate.getChatName())
                .displayName(chatToCreate.getChatDisplayName())
                .ownerId(creator.getMessengerUserId())
                .build();

        mockMvc.perform(post("/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(chatDTO)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(chatCreated)));
    }

    @Test
    void testGetChatInfo() throws Exception{
        Chat chat = Chat.builder()
                .chatId(1L)
                .chatName("chat1")
                .chatDisplayName("Chat 1")
                .build();

        Mockito.when(chatService.findChatById(1L))
                .thenReturn(chat);
        Mockito.when(chatService.findChatByChatName(chat.getChatName()))
                .thenReturn(chat);

        mockMvc.perform(get("/chat")
                .param("chat-id","1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(chat)));

        mockMvc.perform(get("/chat")
                .param("chat-name","chat1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(chat)));

        mockMvc.perform(get("/chat"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(String.format(EXCEPTION_REQUIRED_PARAMETERS_ARE_NULL.value(),
                        String.join(", ",JwtTokenAuth.Param.USER_ID.value(),JwtTokenAuth.Param.USERNAME.value()))));
    }

    @Test
    void testEditChatInfo() throws Exception {
        ChatDTO chatDTO = ChatDTO.builder()
                .chatId(1L)
                .chatName("name")
                .displayName("Name")
                .build();

        ChatDTO chatDTOInvalid = ChatDTO.builder()
                .chatName("name")
                .displayName("Name")
                .ownerId(1L)
                .build();

        mockMvc.perform(put("/chat").contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(chatDTOInvalid)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(String.format(EXCEPTION_ARGUMENT_ISNULL.value(),JwtTokenAuth.Param.CHAT_ID.value(),ChatDTO.class.getSimpleName())));

        Chat chat = Chat.builder()
                .chatId(chatDTO.getChatId())
                .chatName(chatDTO.getChatName())
                .chatDisplayName(chatDTO.getDisplayName())
                .build();
        Mockito.when(chatService.updateChat(chat))
                .thenReturn(chat);

        mockMvc.perform(put("/chat").contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(chatDTO)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(chat)));
    }

    @Test
    void testModifyChatUser() throws Exception {

        ChatUserDTO chatUserDTO = ChatUserDTO.builder()
                .chatId(1L).userId(1L)
                .title("I am modified!").chatUserRole(ChatUserRole.USER)
                .build();

        ChatUserDTO chatUserDTOInvalid = ChatUserDTO.builder()
                .chatId(1L)
                .title("I am modified!").chatUserRole(ChatUserRole.USER)
                .build();

        ChatUserDTO chatUserDTOInvalid2 = ChatUserDTO.builder()
                .chatId(1L).userId(1L)
                .title("I am modified!")
                .build();

        ChatUser chatUserModified = ChatUser.builder()
                .chatId(1L).userId(1L)
                .title("I am modified!").chatUserRole(ChatUserRole.USER)
                .build();

        Mockito.when(chatService.updateChatUser(chatUserModified))
                .thenReturn(chatUserModified);

        ChatUserAuthenticationToken authenticationToken = new ChatUserAuthenticationToken("user1",1L,"name",ChatUserRole.SUPER_ADMIN.authorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        mockMvc.perform(put("/chat/users")
                        .param("chat-id",chatUserDTO.getChatId().toString())
                        .param("user-id",chatUserDTO.getUserId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(chatUserDTO)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(chatUserModified)));

        mockMvc.perform(put("/chat/users").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(chatUserDTOInvalid)))
                .andExpect(status().isBadRequest());

        mockMvc.perform(put("/chat/users").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(chatUserDTOInvalid2)))
                .andExpect(status().isBadRequest());

    }
}