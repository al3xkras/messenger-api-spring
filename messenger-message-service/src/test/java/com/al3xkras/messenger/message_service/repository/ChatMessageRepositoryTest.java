package com.al3xkras.messenger.message_service.repository;

import com.al3xkras.messenger.message_service.entity.Chat;
import com.al3xkras.messenger.message_service.entity.ChatMessage;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@DataJpaTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
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

    ChatMessage message1;
    ChatMessage message2;
    ChatMessage message3;
    ChatMessage message4;
    ChatMessage message5;
    ChatMessage message6;
    ChatMessage message7;
    ChatMessage message8;
    ChatMessage message9;

    void init(){
        message1 = ChatMessage.builder()
                .chatId(chat1.getChatId())
                .userId(1L)
                .submissionDate(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .message("hello")
                .build();
        message2 = ChatMessage.builder()
                .chatId(chat1.getChatId())
                .userId(1L)
                .submissionDate(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusSeconds(1))
                .message("hello again")
                .build();
        message3 = ChatMessage.builder()
                .chatId(chat1.getChatId())
                .userId(2L)
                .submissionDate(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusSeconds(10))
                .message("Hi")
                .build();
        message4 = ChatMessage.builder()
                .chatId(chat1.getChatId())
                .userId(2L)
                .submissionDate(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusSeconds(15))
                .message("How are you?")
                .build();
        message5 = ChatMessage.builder()
                .chatId(chat1.getChatId())
                .userId(1L)
                .submissionDate(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusMinutes(20))
                .message("Fine...")
                .build();
        message6 = ChatMessage.builder()
                .chatId(chat1.getChatId())
                .userId(3L)
                .submissionDate(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusDays(1))
                .message("Bored")
                .build();

        message7 = ChatMessage.builder()
                .chatId(chat2.getChatId())
                .userId(1L)
                .submissionDate(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusSeconds(1))
                .message("test")
                .build();
        message8 = ChatMessage.builder()
                .chatId(chat2.getChatId())
                .userId(20L)
                .submissionDate(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusSeconds(10))
                .message("test")
                .build();
        message9 = ChatMessage.builder()
                .chatId(chat2.getChatId())
                .userId(210L)
                .submissionDate(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusSeconds(11))
                .message("test?")
                .build();
    }

    @BeforeEach
    void beforeEach(){
        chat1.setChatId(null);
        chat2.setChatId(null);
        entityManager.persist(chat1);
        entityManager.persist(chat2);
        init();
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

        assertEquals(expected1,chatMessageRepository.findAllByChatId(chat1.getChatId(),firstPageOfSize4).toSet());
        assertEquals(expected2,chatMessageRepository.findAllByChatId(chat1.getChatId(),secondPageOfSize4).toSet());
        assertEquals(expected3,chatMessageRepository.findAllByChatId(chat2.getChatId(),firstPageOfSize4).toSet());
        assertEquals(expected4,chatMessageRepository.findAllByChatId(chat2.getChatId(),secondPageOfSize4).toSet());
        assertEquals(expected4,chatMessageRepository.findAllByChatId(20L,secondPageOfSize4).toSet());
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

        assertEquals(expected1,chatMessageRepository.findAllByChatName(chat1.getChatName(),firstPageOfSize4).toSet());
        assertEquals(expected2,chatMessageRepository.findAllByChatName(chat1.getChatName(),secondPageOfSize4).toSet());
        assertEquals(expected3,chatMessageRepository.findAllByChatName(chat2.getChatName(),firstPageOfSize4).toSet());
        assertEquals(expected4,chatMessageRepository.findAllByChatName(chat2.getChatName(),secondPageOfSize4).toSet());
        assertEquals(expected4,chatMessageRepository.findAllByChatName("someChat",firstPageOfSize4).toSet());
    }

}