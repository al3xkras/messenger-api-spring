package com.al3xkras.messenger_chat_service.service;

import com.al3xkras.messenger_chat_service.entity.Chat;
import com.al3xkras.messenger_chat_service.entity.ChatUser;
import com.al3xkras.messenger_chat_service.entity.MessengerUser;
import com.al3xkras.messenger_chat_service.model.ChatUserRole;
import com.al3xkras.messenger_chat_service.model.MessengerUserType;
import com.al3xkras.messenger_chat_service.repository.ChatRepository;
import com.al3xkras.messenger_chat_service.repository.ChatUserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;

@SpringBootTest
class ChatServiceTest {

    @Autowired
    private ChatService chatService;

    @MockBean
    private ChatRepository chatRepository;
    @MockBean
    private ChatUserRepository chatUserRepository;

    static MessengerUser firstUser = MessengerUser.builder()
            .messengerUserId(1L)
            .username("user1")
            .name("Max")
            .emailAddress("max@gmail.com")
            .phoneNumber("111-22-33")
            .messengerUserType(MessengerUserType.ADMIN)
            .build();
    static MessengerUser secondUser = MessengerUser.builder()
            .messengerUserId(2L)
            .username("user2")
            .name("Andrew")
            .emailAddress("andrew@gmail.com")
            .phoneNumber("116-22-345")
            .messengerUserType(MessengerUserType.USER)
            .build();
    static MessengerUser thirdUser = MessengerUser.builder()
            .messengerUserId(3L)
            .username("user3")
            .name("Mike")
            .emailAddress("mike@gmail.com")
            .phoneNumber("456-75-645")
            .messengerUserType(MessengerUserType.USER)
            .build();

    static Chat firstChat = Chat.builder()
            .chatId(1L)
            .chatName("first_chat")
            .chatDisplayName("First Chat!")
            .build();

    static Chat secondChat = Chat.builder()
            .chatId(2L)
            .chatName("second_chat")
            .chatDisplayName("Second Chat!")
            .build();

    static ChatUser firstChatUserOfFirstChat = ChatUser.builder()
            .chatId(firstChat.getChatId())
            .userId(firstUser.getMessengerUserId())
            .title("Admin")
            .chatUserRole(ChatUserRole.ADMIN)

            .build();

    @Test
    void whenCreateChatThatDoesNotExist_thenChatIsCreated(){

        Pageable firstPage = PageRequest.of(0,1);
        Pageable secondPage = PageRequest.of(1,1);
        Pageable pageOfSize2 = PageRequest.of(0,2);

        Mockito.when(chatRepository.findAllByUserId(firstUser.getMessengerUserId(),firstPage))
                .thenReturn(new PageImpl<>(Arrays.asList(firstChat)));
        Mockito.when(chatRepository.findAllByUserId(firstUser.getMessengerUserId(),secondPage))
                .thenReturn(new PageImpl<>(Arrays.asList(secondChat)));
        Mockito.when(chatRepository.findAllByUserId(firstUser.getMessengerUserId(),pageOfSize2))
                .thenReturn(new PageImpl<>(Arrays.asList(firstChat,secondChat)));


    }
}