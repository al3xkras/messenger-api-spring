package com.al3xkras.messenger_chat_service.service;

import com.al3xkras.messenger_chat_service.entity.Chat;
import com.al3xkras.messenger_chat_service.entity.ChatUser;
import com.al3xkras.messenger_chat_service.entity.MessengerUser;
import com.al3xkras.messenger_chat_service.exception.ChatNameAlreadyExistsException;
import com.al3xkras.messenger_chat_service.exception.InvalidMessengerUserException;
import com.al3xkras.messenger_chat_service.model.ChatUserRole;
import com.al3xkras.messenger_chat_service.model.MessengerUserType;
import com.al3xkras.messenger_chat_service.repository.ChatRepository;
import com.al3xkras.messenger_chat_service.repository.ChatUserRepository;
import org.hibernate.TransientPropertyValueException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Collections;

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

    @BeforeEach
    void beforeEach(){
        Pageable firstPage = PageRequest.of(0,1);
        Pageable secondPage = PageRequest.of(1,1);
        Pageable pageOfSize2 = PageRequest.of(0,2);

        Mockito.when(chatRepository.findAllByUserId(firstUser.getMessengerUserId(),firstPage))
                .thenReturn(new PageImpl<>(Collections.singletonList(firstChat)));
        Mockito.when(chatRepository.findAllByUserId(firstUser.getMessengerUserId(),secondPage))
                .thenReturn(new PageImpl<>(Collections.singletonList(secondChat)));
        Mockito.when(chatRepository.findAllByUserId(firstUser.getMessengerUserId(),pageOfSize2))
                .thenReturn(new PageImpl<>(Arrays.asList(firstChat,secondChat)));
    }

    @Test
    void whenSaveChatThatDoesNotExist_thenReturnCreatedChat(){

        Chat newChat = Chat.builder()
                .chatName("chat3")
                .chatDisplayName("Chat 3")
                .build();
        Chat newChatAfterPersist = Chat.builder()
                .chatId(3L)
                .chatName("chat3")
                .chatDisplayName("Chat 3")
                .build();

        MessengerUser creator = thirdUser;

        ChatUser chatOwner = ChatUser.builder()
                .chat(newChat)
                .messengerUser(creator)
                .title("Admin")
                .chatUserRole(ChatUserRole.ADMIN)
                .build();
        ChatUser chatOwnerAfterPersist = ChatUser.builder()
                .chatId(newChat.getChatId())
                .userId(creator.getMessengerUserId())
                .chat(newChat)
                .messengerUser(creator)
                .title("Admin")
                .chatUserRole(ChatUserRole.ADMIN)
                .build();



        Mockito.when(chatRepository.save(newChat))
                .thenReturn(newChatAfterPersist);
        Mockito.when(chatUserRepository.saveAndFlush(chatOwner))
                .thenReturn(chatOwnerAfterPersist);

        Assertions.assertEquals(newChatAfterPersist,chatService.saveChat(newChat, creator));
    }

    @Test
    void whenSaveChatWithChatNameThatExists_thenThrowChatNameAlreadyExistsException(){
        Chat newChat = Chat.builder()
                .chatName("chat3")
                .chatDisplayName("Chat 3")
                .build();
        Chat newChatAfterPersist = Chat.builder()
                .chatId(3L)
                .chatName("chat3")
                .chatDisplayName("Chat 3")
                .build();

        MessengerUser creator = thirdUser;

        ChatUser chatOwner = ChatUser.builder()
                .chat(newChat)
                .messengerUser(creator)
                .title("Admin")
                .chatUserRole(ChatUserRole.ADMIN)
                .build();


        Mockito.when(chatRepository.save(newChat))
                .thenReturn(newChatAfterPersist);
        Mockito.when(chatUserRepository.saveAndFlush(chatOwner))
                .thenThrow(DataIntegrityViolationException.class);

        Assertions.assertThrows(ChatNameAlreadyExistsException.class,()->{
            chatService.saveChat(newChat, creator);
        });
    }

    @Test
    void whenSaveChatWithInvalidCreatorMessengerUser_thenThrowInvalidMessengerUserException(){
        MessengerUser messengerUserWithoutId = MessengerUser.builder().username("user1")
                .name("Max")
                .emailAddress("max@gmail.com")
                .phoneNumber("111-22-33")
                .messengerUserType(MessengerUserType.ADMIN)
                .build();

        ChatUser chatOwner1 = ChatUser.builder()
                .chat(firstChat)
                .messengerUser(messengerUserWithoutId)
                .title("Admin")
                .chatUserRole(ChatUserRole.ADMIN)
                .build();

        MessengerUser messengerUserNotPersisted = MessengerUser.builder()
                .messengerUserId(4L)
                .username("user4")
                .name("Mike")
                .emailAddress("mike@gmail.com")
                .phoneNumber("456-75-645")
                .messengerUserType(MessengerUserType.USER)
                .build();

        ChatUser chatOwner2 = ChatUser.builder()
                .chat(secondChat)
                .messengerUser(messengerUserNotPersisted)
                .title("Admin")
                .chatUserRole(ChatUserRole.ADMIN)
                .build();

        Mockito.when(chatRepository.save(firstChat))
                .thenReturn(firstChat);
        Mockito.when(chatUserRepository.saveAndFlush(chatOwner1))
                .thenThrow(new InvalidDataAccessApiUsageException("",new IllegalStateException(new TransientPropertyValueException("","","",""))));

        Mockito.when(chatRepository.save(secondChat))
                .thenReturn(secondChat);
        Mockito.when(chatUserRepository.saveAndFlush(chatOwner2))
                .thenThrow(new InvalidDataAccessApiUsageException("",new IllegalStateException(new TransientPropertyValueException("","","",""))));

        Assertions.assertThrows(InvalidMessengerUserException.class,()->{
            chatService.saveChat(firstChat,messengerUserWithoutId);
        });

        Assertions.assertThrows(InvalidMessengerUserException.class,()->{
            chatService.saveChat(secondChat,messengerUserNotPersisted);
        });
    }
}