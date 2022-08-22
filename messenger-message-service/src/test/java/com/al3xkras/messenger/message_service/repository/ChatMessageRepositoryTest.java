package com.al3xkras.messenger.message_service.repository;

import com.al3xkras.messenger.entity.Chat;
import com.al3xkras.messenger.entity.ChatMessage;
import com.al3xkras.messenger.entity.ChatUser;
import com.al3xkras.messenger.entity.MessengerUser;
import com.al3xkras.messenger.model.ChatUserRole;
import com.al3xkras.messenger.model.MessengerUserType;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@DataJpaTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")
class ChatMessageRepositoryTest {

    @Autowired
    private ChatMessageRepository chatMessageRepository;
    @Autowired
    private TestEntityManager entityManager;

    Chat chat1 = Chat.builder()
            .chatName("first_chat")
            .chatDisplayName("First chat")
            .build();
    Chat chat2 = Chat.builder()
            .chatName("second_chat")
            .chatDisplayName("Second chat")
            .build();

    static MessengerUser firstUser = MessengerUser.FIRST_ADMIN;
    static MessengerUser secondUser = MessengerUser.builder()
            .username("user2")
            .password("Password123.")
            .name("Andrew")
            .emailAddress("andrew@gmail.com")
            .phoneNumber("+31234567")
            .messengerUserType(MessengerUserType.USER)
            .build();
    static MessengerUser thirdUser = MessengerUser.builder()
            .username("user3")
            .password("Password123.")
            .name("Mike")
            .emailAddress("mike@gmail.com")
            .phoneNumber("+4 564-7564")
            .messengerUserType(MessengerUserType.USER)
            .build();
    static MessengerUser user4 = MessengerUser.builder()
            .username("user4")
            .password("Password123.")
            .name("Mike")
            .emailAddress("mike@gmail.com")
            .phoneNumber("+4 564-7564")
            .messengerUserType(MessengerUserType.USER)
            .build();
    static MessengerUser user5 = MessengerUser.builder()
            .username("user5")
            .password("Password123.")
            .name("Mike")
            .emailAddress("mike@gmail.com")
            .phoneNumber("+4 564-7564")
            .messengerUserType(MessengerUserType.USER)
            .build();
    ChatMessage message1;
    ChatMessage message2;
    ChatMessage message3;
    ChatMessage message4;
    ChatMessage message5;
    ChatMessage message6;
    ChatMessage message7;
    ChatMessage message8;
    ChatMessage message9;

    ChatUser chatUser11;
    ChatUser chatUser12;
    ChatUser chatUser21;
    ChatUser chatUser22;
    ChatUser chatUser13;
    ChatUser chatUser24;
    ChatUser chatUser25;

    void initChatUsers(){
        chatUser11 = ChatUser.builder()
                .chat(chat1)
                .messengerUser(firstUser)
                .title("Admin")
                .chatUserRole(ChatUserRole.ADMIN)
                .build();

        chatUser12 = ChatUser.builder()
                .chat(chat1)
                .messengerUser(secondUser)
                .chatUserRole(ChatUserRole.USER)
                .build();

        chatUser21 = ChatUser.builder()
                .chat(chat2)
                .messengerUser(secondUser)
                .title("Admin")
                .chatUserRole(ChatUserRole.ADMIN)
                .build();

        chatUser22 = ChatUser.builder()
                .chat(chat2)
                .messengerUser(firstUser)
                .chatUserRole(ChatUserRole.USER)
                .build();

        chatUser13 = ChatUser.builder()
                .chat(chat1)
                .messengerUser(thirdUser)
                .chatUserRole(ChatUserRole.USER)
                .build();

        chatUser24 = ChatUser.builder()
                .chat(chat2)
                .messengerUser(user4)
                .chatUserRole(ChatUserRole.USER)
                .build();

        chatUser25 = ChatUser.builder()
                .chat(chat2)
                .messengerUser(user5)
                .chatUserRole(ChatUserRole.USER)
                .build();
    }

    void init(){
        message1 = ChatMessage.builder()
                .chatId(chat1.getChatId())
                .userId(firstUser.getMessengerUserId())
                .submissionDate(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .message("hello")
                .build();
        message2 = ChatMessage.builder()
                .chatId(chat1.getChatId())
                .userId(firstUser.getMessengerUserId())
                .submissionDate(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusSeconds(1))
                .message("hello again")
                .build();
        message3 = ChatMessage.builder()
                .chatId(chat1.getChatId())
                .userId(secondUser.getMessengerUserId())
                .submissionDate(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusSeconds(10))
                .message("Hi")
                .build();
        message4 = ChatMessage.builder()
                .chatId(chat1.getChatId())
                .userId(secondUser.getMessengerUserId())
                .submissionDate(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusSeconds(15))
                .message("How are you?")
                .build();
        message5 = ChatMessage.builder()
                .chatId(chat1.getChatId())
                .userId(firstUser.getMessengerUserId())
                .submissionDate(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusMinutes(20))
                .message("Fine...")
                .build();
        message6 = ChatMessage.builder()
                .chatId(chat1.getChatId())
                .userId(thirdUser.getMessengerUserId())
                .submissionDate(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusDays(1))
                .message("Bored")
                .build();

        message7 = ChatMessage.builder()
                .chatId(chat2.getChatId())
                .userId(firstUser.getMessengerUserId())
                .submissionDate(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusSeconds(1))
                .message("test")
                .build();
        message8 = ChatMessage.builder()
                .chatId(chat2.getChatId())
                .userId(user4.getMessengerUserId())
                .submissionDate(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusSeconds(10))
                .message("test")
                .build();
        message9 = ChatMessage.builder()
                .chatId(chat2.getChatId())
                .userId(user5.getMessengerUserId())
                .submissionDate(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusSeconds(11))
                .message("test?")
                .build();
    }

    @BeforeEach
    void persistEntities(){
        firstUser.setMessengerUserId(null);
        secondUser.setMessengerUserId(null);
        thirdUser.setMessengerUserId(null);
        user4.setMessengerUserId(null);
        user5.setMessengerUserId(null);
        entityManager.persist(firstUser);
        entityManager.persist(secondUser);
        entityManager.persist(thirdUser);
        entityManager.persist(user4);
        entityManager.persist(user5);
        chat1.setChatId(null);
        chat2.setChatId(null);
        entityManager.persist(chat1);
        entityManager.persist(chat2);
        init();
        initChatUsers();
        entityManager.persist(chatUser11);
        entityManager.persist(chatUser12);
        entityManager.persist(chatUser21);
        entityManager.persist(chatUser22);
        entityManager.persist(chatUser13);
        entityManager.persist(chatUser24);
        entityManager.persist(chatUser25);

        entityManager.persist(message1);
        entityManager.persist(message2);
        entityManager.persist(message3);
        entityManager.persist(message4);
        entityManager.persist(message5);
        entityManager.persist(message6);
        entityManager.persist(message7);
        entityManager.persist(message8);
        entityManager.persist(message9);
    }

    @Test
    @Order(2)
    void testFindAllByChatId(){

        Pageable firstPageOfSize4 = PageRequest.of(0,4);
        Pageable secondPageOfSize4 = PageRequest.of(1,4);

        Set<ChatMessage> expected1 = new HashSet<>(Arrays.asList(
                message1,message2,message3,message4
        ));

        Set<ChatMessage> expected2 = new HashSet<>(Arrays.asList(
                message5,message6
        ));

        Set<ChatMessage> expected3 = new HashSet<>(Arrays.asList(
                message7,message8,message9
        ));

        Set<ChatMessage> expected4 = new HashSet<>();

        assertEquals(expected1,chatMessageRepository.findAllByChatIdOrderBySubmissionDate(chat1.getChatId(),firstPageOfSize4).toSet());
        assertEquals(expected2,chatMessageRepository.findAllByChatIdOrderBySubmissionDate(chat1.getChatId(),secondPageOfSize4).toSet());
        assertEquals(expected3,chatMessageRepository.findAllByChatIdOrderBySubmissionDate(chat2.getChatId(),firstPageOfSize4).toSet());
        assertEquals(expected4,chatMessageRepository.findAllByChatIdOrderBySubmissionDate(chat2.getChatId(),secondPageOfSize4).toSet());
        assertEquals(expected4,chatMessageRepository.findAllByChatIdOrderBySubmissionDate(20L,secondPageOfSize4).toSet());
    }

    @Test
    @Order(3)
    void testFindAllByChatName(){
        Pageable firstPageOfSize4 = PageRequest.of(0,4);
        Pageable secondPageOfSize4 = PageRequest.of(1,4);

        Set<ChatMessage> expected1 = new HashSet<>(Arrays.asList(
                message1,message2,message3,message4
        ));

        Set<ChatMessage> expected2 = new HashSet<>(Arrays.asList(
                message5,message6
        ));

        Set<ChatMessage> expected3 = new HashSet<>(Arrays.asList(
                message7,message8,message9
        ));

        Set<ChatMessage> expected4 = new HashSet<>();

        assertEquals(expected1,chatMessageRepository.findAllByChatNameOrderBySubmissionDate(chat1.getChatName(),firstPageOfSize4).toSet());
        assertEquals(expected2,chatMessageRepository.findAllByChatNameOrderBySubmissionDate(chat1.getChatName(),secondPageOfSize4).toSet());
        assertEquals(expected3,chatMessageRepository.findAllByChatNameOrderBySubmissionDate(chat2.getChatName(),firstPageOfSize4).toSet());
        assertEquals(expected4,chatMessageRepository.findAllByChatNameOrderBySubmissionDate(chat2.getChatName(),secondPageOfSize4).toSet());
        assertEquals(expected4,chatMessageRepository.findAllByChatNameOrderBySubmissionDate("someChat",firstPageOfSize4).toSet());
    }

}