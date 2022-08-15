package com.al3xkras.messenger.user_service.repository;

import com.al3xkras.messenger.entity.MessengerUser;
import com.al3xkras.messenger.model.MessengerUserType;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@Slf4j
@DataJpaTest
@ActiveProfiles("test")
class MessengerUserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private MessengerUserRepository messengerUserRepository;

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
            .name("Den")
            .emailAddress("d3en@gmail.com")
            .phoneNumber("111-22-73")
            .messengerUserType(MessengerUserType.USER)
            .build();
    static MessengerUser updatedFirstUser;

    static MessengerUser userToSave = MessengerUser.builder()
            .username("user3")
            .password("Password123.")
            .name("Jake")
            .emailAddress("jak3@gmail.com")
            .phoneNumber("111-45376-73")
            .messengerUserType(MessengerUserType.USER)
            .build();
    static MessengerUser userToSaveWithId = MessengerUser.builder()
            .messengerUserId(3L)
            .username("user3")
            .password("Password123.")
            .name("Jake")
            .emailAddress("jak3@gmail.com")
            .phoneNumber("111-45376-73")
            .messengerUserType(MessengerUserType.USER)
            .build();

    void init(){
        updatedFirstUser = MessengerUser.builder()
                .messengerUserId(firstUser.getMessengerUserId())
                .username("user1")
                .password("Password123.")
                .name("Maxim")
                .emailAddress("maxim@gmail.com")
                .phoneNumber("111-22-33")
                .messengerUserType(MessengerUserType.ADMIN)
                .build();
    }

    @BeforeEach
    void setUp() {
        firstUser.setMessengerUserId(null);
        secondUser.setMessengerUserId(null);
        entityManager.persist(firstUser);
        entityManager.persist(secondUser);
        init();
    }

    @Test
    void whenFindExistingUser_thenReturn(){
        assertEquals(firstUser,messengerUserRepository.findById(firstUser.getMessengerUserId()).orElse(null));
        assertEquals(firstUser,messengerUserRepository.findByUsername(firstUser.getUsername()).orElse(null));
        assertEquals(secondUser,messengerUserRepository.findById(secondUser.getMessengerUserId()).orElse(null));
        assertEquals(secondUser,messengerUserRepository.findByUsername(secondUser.getUsername()).orElse(null));
    }

    @Test
    void whenFindUserThatDoesNotExist_thenReturnOptionalEmpty(){
        assertEquals(Optional.empty(),messengerUserRepository.findById(3L));
        assertEquals(Optional.empty(), messengerUserRepository.findByUsername("user3"));
    }

    @Test
    void whenSaveNewUser_thenReturnNewUserWithId(){
        userToSaveWithId = messengerUserRepository.save(userToSave);
        Long id = userToSaveWithId.getMessengerUserId();
        userToSaveWithId.setMessengerUserId(null);
        assertEquals(userToSaveWithId,userToSave);
        assertNotNull(id);
        userToSaveWithId.setMessengerUserId(id);
        assertEquals(userToSaveWithId,messengerUserRepository.findById(userToSave.getMessengerUserId()).orElse(null));
        assertEquals(userToSaveWithId,messengerUserRepository.findByUsername("user3").orElse(null));
    }

    @Test
    void whenSaveExistingUser_thenReturnUpdatedUser(){
        assertEquals(firstUser,messengerUserRepository.findById(firstUser.getMessengerUserId()).orElse(null));
        messengerUserRepository.save(updatedFirstUser);
        assertEquals(updatedFirstUser,messengerUserRepository.findById(firstUser.getMessengerUserId()).orElse(null));
    }

    @Test
    void whenSaveUserWithExistingUsername_thenThrowSqlIntegrity(){
        assertEquals(firstUser,messengerUserRepository.findByUsername("user1").orElse(null));

        assertThrows(DataIntegrityViolationException.class,()->{
            messengerUserRepository.saveAndFlush(MessengerUser.builder().username("user1").messengerUserType(MessengerUserType.USER).build());
        });
    }

    @Test
    void whenDeleteExisting_thenDelete(){
        assertEquals(firstUser,messengerUserRepository.findById(firstUser.getMessengerUserId()).orElse(null));
        assertEquals(firstUser,messengerUserRepository.findByUsername(firstUser.getUsername()).orElse(null));
        messengerUserRepository.deleteById(firstUser.getMessengerUserId());
        assertEquals(Optional.empty(),messengerUserRepository.findById(firstUser.getMessengerUserId()));
        assertEquals(Optional.empty(),messengerUserRepository.findByUsername(firstUser.getUsername()));
    }

    @Test
    void whenDeleteUserThatDoesNotExist_thenExpectException(){
        assertEquals(Optional.empty(),messengerUserRepository.findById(365L));
        assertEquals(Optional.empty(),messengerUserRepository.findByUsername("user3"));
        assertThrows(EmptyResultDataAccessException.class,()->{
            messengerUserRepository.deleteById(365L);
        });
    }

}