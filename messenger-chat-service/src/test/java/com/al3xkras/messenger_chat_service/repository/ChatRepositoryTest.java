package com.al3xkras.messenger_chat_service.repository;

import com.al3xkras.messenger_chat_service.entity.Chat;
import com.al3xkras.messenger_chat_service.entity.ChatUser;
import com.al3xkras.messenger_chat_service.entity.MessengerUser;
import com.al3xkras.messenger_chat_service.model.ChatUserRole;
import com.al3xkras.messenger_chat_service.model.MessengerUserType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ChatRepositoryTest {

    @Autowired
    private ChatRepository chatRepository;

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
        firstUser = testEntityManager.persist(firstUser);
        secondUser = testEntityManager.persist(secondUser);
        thirdUser = testEntityManager.persist(thirdUser);

        firstChat = testEntityManager.persist(firstChat);
        secondChat = testEntityManager.persist(secondChat);

        chatUser11 = testEntityManager.persist(chatUser11);
        chatUser12 = testEntityManager.persist(chatUser12);
        chatUser21 = testEntityManager.persist(chatUser21);
        chatUser22 = testEntityManager.persist(chatUser22);
        chatUser23 = testEntityManager.persist(chatUser23);
    }


    @Test
    void whenFindAllByUserId_thenReturn(){
        Pageable firstPageOfSize1 = PageRequest.of(0,1);
        Pageable firstPageOfSize2 = PageRequest.of(0,2);
        Pageable firstPageOfSize200 = PageRequest.of(0,200);

        List<Chat> chatsOfFirstUser = chatRepository.findAllByUserId(firstUser.getMessengerUserId(),
                firstPageOfSize1).toList();
        assertEquals(1,chatsOfFirstUser.size());
        assertEquals(firstChat,chatsOfFirstUser.get(0));

        chatsOfFirstUser = chatRepository.findAllByUserId(firstUser.getMessengerUserId(),
                firstPageOfSize2).toList();
        assertEquals(2,chatsOfFirstUser.size());
        assertEquals(firstChat,chatsOfFirstUser.get(0));
        assertEquals(secondChat,chatsOfFirstUser.get(1));

        chatsOfFirstUser = chatRepository.findAllByUserId(firstUser.getMessengerUserId(),
                firstPageOfSize200).toList();
        assertEquals(2,chatsOfFirstUser.size());

        chatsOfFirstUser = chatRepository.findAllByUserId(100L,
                firstPageOfSize200).toList();
        assertEquals(0,chatsOfFirstUser.size());
    }

    @Test
    void whenFindAllByUsername_thenReturn(){
        Pageable firstPageOfSize1 = PageRequest.of(0,1);
        Pageable firstPageOfSize2 = PageRequest.of(0,2);
        Pageable firstPageOfSize200 = PageRequest.of(0,200);

        List<Chat> chatsOfFirstUser = chatRepository.findAllByUsername(firstUser.getUsername(),
                firstPageOfSize1).toList();
        assertEquals(1,chatsOfFirstUser.size());
        assertEquals(firstChat,chatsOfFirstUser.get(0));

        chatsOfFirstUser = chatRepository.findAllByUsername(firstUser.getUsername(),
                firstPageOfSize2).toList();
        assertEquals(2,chatsOfFirstUser.size());
        assertEquals(firstChat,chatsOfFirstUser.get(0));
        assertEquals(secondChat,chatsOfFirstUser.get(1));

        chatsOfFirstUser = chatRepository.findAllByUsername(firstUser.getUsername(),
                firstPageOfSize200).toList();
        assertEquals(2,chatsOfFirstUser.size());

        chatsOfFirstUser = chatRepository.findAllByUsername("user120",
                firstPageOfSize200).toList();
        assertEquals(0,chatsOfFirstUser.size());
    }

    @Test
    void whenFindByExistingChatName_thenReturnChat(){
        Optional<Chat> chat = chatRepository.findByChatName(firstChat.getChatName());
        assertEquals(firstChat,chat.orElse(null));
        assertEquals(Optional.empty(),chatRepository.findByChatName("chat12345"));
    }

    @Test
    void whenSaveChatWithExistingChatName_thenThrowConstraintViolation(){
        Chat firstChat2 = Chat.builder()
                .chatName("first_chat")
                .chatDisplayName("First Chat but not first!")
                .build();

        assertThrows(DataIntegrityViolationException.class,()->{
            chatRepository.saveAndFlush(firstChat2);
        });
    }
}