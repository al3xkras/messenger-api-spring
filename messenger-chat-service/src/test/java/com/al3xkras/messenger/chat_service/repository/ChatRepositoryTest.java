package com.al3xkras.messenger.chat_service.repository;

import com.al3xkras.messenger.entity.MessengerUser;
import com.al3xkras.messenger.model.ChatUserRole;
import com.al3xkras.messenger.model.MessengerUserType;
import com.al3xkras.messenger.entity.Chat;
import com.al3xkras.messenger.entity.ChatUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@ActiveProfiles("test")
class ChatRepositoryTest {

    @Autowired
    private ChatRepository chatRepository;

    @PersistenceContext
    private EntityManager entityManager;


    static MessengerUser firstUser = MessengerUser.builder()
            .username("user1")
            .password("Password123.")
            .name("Max")
            .emailAddress("max@gmail.com")
            .phoneNumber("111-22-33")
            .messengerUserType(MessengerUserType.ADMIN)
            .build();
    static MessengerUser secondUser = MessengerUser.builder()
            .username("user2")
            .password("Password123.")
            .name("Andrew")
            .emailAddress("andrew@gmail.com")
            .phoneNumber("116-22-345")
            .messengerUserType(MessengerUserType.USER)
            .build();
    static MessengerUser thirdUser = MessengerUser.builder()
            .username("user3")
            .password("Password123.")
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

    void init(){
        firstChat = Chat.builder()
                .chatName("first_chat")
                .chatDisplayName("First Chat!")
                .build();
        secondChat = Chat.builder()
                .chatName("second_chat")
                .chatDisplayName("Second Chat!")
                .build();

         chatUser11 = ChatUser.builder()
                .chat(firstChat)
                .messengerUser(firstUser)
                .title("Admin of chat 1")
                .chatUserRole(ChatUserRole.ADMIN)
                .build();
         chatUser12 = ChatUser.builder()
                .chat(firstChat)
                .messengerUser(secondUser)
                .chatUserRole(ChatUserRole.USER)
                .build();
         chatUser21 = ChatUser.builder()
                .chat(secondChat)
                .messengerUser(thirdUser)
                .title("Admin of chat 2")
                .chatUserRole(ChatUserRole.ADMIN)
                .build();
         chatUser22 = ChatUser.builder()
                .chat(secondChat)
                .messengerUser(firstUser)
                .chatUserRole(ChatUserRole.USER)
                .build();
         chatUser23 = ChatUser.builder()
                .chat(secondChat)
                .messengerUser(secondUser)
                .chatUserRole(ChatUserRole.USER)
                .build();
    }

    @BeforeEach
    void beforeEach(){
        firstUser.setMessengerUserId(null);
        secondUser.setMessengerUserId(null);
        thirdUser.setMessengerUserId(null);
        entityManager.persist(firstUser);
        entityManager.persist(secondUser);
        entityManager.persist(thirdUser);

        init();

        entityManager.persist(firstChat);
        entityManager.persist(secondChat);

        entityManager.persist(chatUser11);
        entityManager.persist(chatUser12);
        entityManager.persist(chatUser21);
        entityManager.persist(chatUser22);
        entityManager.persist(chatUser23);
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