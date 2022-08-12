package com.al3xkras.messenger.chat_service.repository;

import com.al3xkras.messenger.chat_service.entity.Chat;
import com.al3xkras.messenger.chat_service.entity.MessengerUser;
import com.al3xkras.messenger.chat_service.model.ChatUserRole;
import com.al3xkras.messenger.chat_service.model.MessengerUserType;
import com.al3xkras.messenger.chat_service.entity.ChatUser;
import com.al3xkras.messenger.chat_service.model.ChatUserId;
import org.hibernate.TransientPropertyValueException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;


@DataJpaTest
class ChatUserRepositoryTest {

    @Autowired
    private ChatUserRepository chatUserRepository;

    @Autowired
    private TestEntityManager testEntityManager;

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
            .chatName("first_chat")
            .chatDisplayName("First Chat!")
            .build();

    static Chat secondChat = Chat.builder()
            .chatName("second_chat")
            .chatDisplayName("Second Chat!")
            .build();

    static ChatUser chatUser11 = ChatUser.builder()
            .chat(firstChat)
            .messengerUser(firstUser)
            .title("Admin of chat 1")
            .chatUserRole(ChatUserRole.ADMIN)
            .build();

    static ChatUser chatUser12 = ChatUser.builder()
            .chat(firstChat)
            .messengerUser(secondUser)
            .chatUserRole(ChatUserRole.USER)
            .build();

    static ChatUser chatUser21 = ChatUser.builder()
            .chat(secondChat)
            .messengerUser(thirdUser)
            .title("Admin of chat 2")
            .chatUserRole(ChatUserRole.ADMIN)
            .build();

    static ChatUser chatUser22 = ChatUser.builder()
            .chat(secondChat)
            .messengerUser(firstUser)
            .chatUserRole(ChatUserRole.USER)
            .build();

    static ChatUser chatUser23 = ChatUser.builder()
            .chat(secondChat)
            .messengerUser(secondUser)
            .chatUserRole(ChatUserRole.USER)
            .build();

    @BeforeEach
    void beforeEach(){
        testEntityManager.persist(firstUser);
        testEntityManager.persist(secondUser);
        testEntityManager.persist(thirdUser);

        testEntityManager.persist(firstChat);
        testEntityManager.persist(secondChat);

        testEntityManager.persist(chatUser11);
        testEntityManager.persist(chatUser12);
        testEntityManager.persist(chatUser21);
        testEntityManager.persist(chatUser22);
    }

    @Test
    void whenSaveNewChatUser_thenReturnNewUser(){
        assertEquals(chatUser23,chatUserRepository.saveAndFlush(chatUser23));
    }

    @Test
    void whenSaveExistingChatUser_thenUpdate(){

        assertEquals(chatUser22,chatUserRepository.findById(new ChatUserId(secondChat.getChatId(),firstUser.getMessengerUserId())).orElseThrow(RuntimeException::new));
        ChatUser chatUser22Updated = ChatUser.builder()
                .chatId(secondChat.getChatId())
                .userId(firstUser.getMessengerUserId())
                .title("The title is updated")
                .chatUserRole(ChatUserRole.USER)
                .build();
        chatUserRepository.saveAndFlush(chatUser22Updated);
        assertEquals(chatUser22Updated,chatUserRepository.findById(new ChatUserId(secondChat.getChatId(),firstUser.getMessengerUserId())).orElseThrow(RuntimeException::new));
    }

    @Test
    void whenFindAllByChatId_thenReturn(){
        Pageable firstPageOfSize1 = PageRequest.of(0,1);
        Pageable firstPageOfSize2 = PageRequest.of(0,2);
        Pageable firstPageOfSize200 = PageRequest.of(0,200);

        List<ChatUser> chatUsersOfFirstChat = chatUserRepository.findAllByChatId(firstChat.getChatId(),
                firstPageOfSize1).toList();
        assertEquals(1,chatUsersOfFirstChat.size());
        assertEquals(chatUser11,chatUsersOfFirstChat.get(0));
        assertEquals(firstUser,chatUsersOfFirstChat.get(0).getMessengerUser());

        chatUsersOfFirstChat = chatUserRepository.findAllByChatId(firstChat.getChatId(),
                PageRequest.of(1,1)).toList();
        assertEquals(chatUsersOfFirstChat.size(),1);
        assertEquals(chatUsersOfFirstChat.get(0),chatUser12);
        assertEquals(secondUser,chatUsersOfFirstChat.get(0).getMessengerUser());

        chatUsersOfFirstChat = chatUserRepository.findAllByChatId(firstChat.getChatId(),
                firstPageOfSize2).toList();
        assertEquals(chatUsersOfFirstChat.size(),2);
        assertEquals(chatUsersOfFirstChat.get(0),chatUser11);
        assertEquals(chatUsersOfFirstChat.get(1),chatUser12);

        chatUsersOfFirstChat = chatUserRepository.findAllByChatId(firstChat.getChatId(),
                firstPageOfSize200).toList();
        assertEquals(chatUsersOfFirstChat.size(),2);
        assertEquals(chatUsersOfFirstChat.get(0),chatUser11);
        assertEquals(chatUsersOfFirstChat.get(1),chatUser12);
    }

    @Test
    void whenMessengerUserIsModified_thenChatUserReturnsModifiedMessengerUser(){
        Pageable firstPageOfSize1 = PageRequest.of(0,1);

        List<ChatUser> chatUsersOfFirstChat = chatUserRepository.findAllByChatId(firstChat.getChatId(),
                firstPageOfSize1).toList();
        assertEquals(1,chatUsersOfFirstChat.size());
        assertEquals(chatUser11,chatUsersOfFirstChat.get(0));
        assertEquals(firstUser,chatUsersOfFirstChat.get(0).getMessengerUser());

        MessengerUser firstUserModified = MessengerUser.builder()
                .messengerUserId(1L)
                .username("user1")
                .name("Modified")
                .emailAddress("mod@gmail.com")
                .phoneNumber("213-22-33")
                .messengerUserType(MessengerUserType.ADMIN)
                .build();

        testEntityManager.remove(firstUser);
        testEntityManager.persist(firstUserModified);

        chatUsersOfFirstChat = chatUserRepository.findAllByChatId(firstChat.getChatId(),
                firstPageOfSize1).toList();
        assertEquals(1,chatUsersOfFirstChat.size());
        assertEquals(chatUser11,chatUsersOfFirstChat.get(0));
        assertEquals(firstUserModified,chatUsersOfFirstChat.get(0).getMessengerUser());

    }

    @Test
    void whenSaveChatUserWithInvalidMessengerUser_thenThrowInvalidDataAccessApiUsageException(){
        MessengerUser notPersisted = MessengerUser.builder()
                .messengerUserId(4L)
                .username("user4")
                .name("Mike")
                .emailAddress("mike@gmail.com")
                .phoneNumber("456-75-645")
                .messengerUserType(MessengerUserType.USER)
                .build();
        ChatUser chatUser = ChatUser.builder()
                .messengerUser(notPersisted)
                .chat(firstChat)
                .chatUserRole(ChatUserRole.USER)
                .build();
        try {
            chatUserRepository.saveAndFlush(chatUser);
            throw new RuntimeException();
        } catch (InvalidDataAccessApiUsageException e){
            assertInstanceOf(TransientPropertyValueException.class,e.getCause().getCause());
        }
    }

    @Test
    void whenDeleteChatUserThatDoesNotExist_thenThrowEmptyResultDataAccessException(){
        Assertions.assertThrows(EmptyResultDataAccessException.class,()->{
            chatUserRepository.deleteById(new ChatUserId(chatUser23.getChatId(),chatUser23.getUserId()));
        });
    }

    @Test
    void whenDeleteChatUserByInvalidId_thenThrowEmptyResultDataAccessException(){
        Assertions.assertThrows(EmptyResultDataAccessException.class,()->{
            chatUserRepository.deleteById(new ChatUserId(null,chatUser23.getUserId()));
        });
        Assertions.assertThrows(EmptyResultDataAccessException.class,()->{
            chatUserRepository.deleteById(new ChatUserId(chatUser23.getChatId(),null));
        });
        Assertions.assertThrows(EmptyResultDataAccessException.class,()->{
            chatUserRepository.deleteById(new ChatUserId(null,null));
        });
    }

}