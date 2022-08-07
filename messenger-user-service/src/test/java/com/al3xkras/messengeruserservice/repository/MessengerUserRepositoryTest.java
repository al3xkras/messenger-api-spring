package com.al3xkras.messengeruserservice.repository;

import com.al3xkras.messengeruserservice.entity.MessengerUser;
import com.al3xkras.messengeruserservice.model.MessengerUserType;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;


@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestEntityManager
@Slf4j
class MessengerUserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private MessengerUserRepository messengerUserRepository;

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
            .name("Den")
            .emailAddress("d3en@gmail.com")
            .phoneNumber("111-22-73")
            .messengerUserType(MessengerUserType.USER)
            .build();
    static MessengerUser updatedFirstUser = MessengerUser.builder()
            .messengerUserId(1L)
            .username("user1")
            .name("Maxim")
            .emailAddress("maxim@gmail.com")
            .phoneNumber("111-22-33")
            .messengerUserType(MessengerUserType.ADMIN)
            .build();

    static MessengerUser userToSave = MessengerUser.builder()
            .username("user3")
            .name("Jake")
            .emailAddress("jak3@gmail.com")
            .phoneNumber("111-45376-73")
            .messengerUserType(MessengerUserType.USER)
            .build();
    static MessengerUser userToSaveWithId = MessengerUser.builder()
            .messengerUserId(3L)
            .username("user3")
            .name("Jake")
            .emailAddress("jak3@gmail.com")
            .phoneNumber("111-45376-73")
            .messengerUserType(MessengerUserType.USER)
            .build();


    @BeforeEach
    void setUp() {
        messengerUserRepository.save(firstUser);
        messengerUserRepository.save(secondUser);
        log.info("setup");
    }

    @Test
    void whenFindExistingUser_thenReturn(){
        Assertions.assertEquals(firstUser,messengerUserRepository.findById(1L).orElse(null));
        Assertions.assertEquals(firstUser,messengerUserRepository.findByUsername("user1").orElse(null));
        Assertions.assertEquals(secondUser,messengerUserRepository.findById(2L).orElse(null));
        Assertions.assertEquals(secondUser,messengerUserRepository.findByUsername("user2").orElse(null));
    }

    @Test
    void whenFindUserThatDoesNotExist_thenReturnOptionalEmpty(){
        Assertions.assertEquals(Optional.empty(),messengerUserRepository.findById(3L));
        Assertions.assertEquals(Optional.empty(), messengerUserRepository.findByUsername("user3"));
    }

    @Test
    void whenSaveNewUser_thenReturnNewUserWithId(){
        Assertions.assertEquals(Optional.empty(),messengerUserRepository.findById(3L));
        Assertions.assertEquals(userToSaveWithId,messengerUserRepository.save(userToSave));
        Assertions.assertEquals(userToSaveWithId,messengerUserRepository.findById(3L).orElse(null));
        Assertions.assertEquals(userToSaveWithId,messengerUserRepository.findByUsername("user3").orElse(null));
    }

    @Test
    void whenSaveExistingUser_thenReturnUpdatedUser(){
        Assertions.assertEquals(firstUser,messengerUserRepository.findById(1L).orElse(null));
        messengerUserRepository.save(updatedFirstUser);
        Assertions.assertEquals(updatedFirstUser,messengerUserRepository.findById(1L).orElse(null));
    }

    @Test
    void whenSaveUserWithExistingUsername_thenThrowSqlIntegrity(){
        Assertions.assertEquals(firstUser,messengerUserRepository.findByUsername("user1").orElse(null));

        Assertions.assertThrows(DataIntegrityViolationException.class,()->{
            messengerUserRepository.saveAndFlush(MessengerUser.builder().username("user1").messengerUserType(MessengerUserType.USER).build());
        });
    }

    @Test
    void whenDeleteExisting_thenDelete(){
        Assertions.assertEquals(firstUser,messengerUserRepository.findById(1L).orElse(null));
        Assertions.assertEquals(firstUser,messengerUserRepository.findByUsername("user1").orElse(null));
        messengerUserRepository.deleteById(1L);
        Assertions.assertEquals(Optional.empty(),messengerUserRepository.findById(1L));
        Assertions.assertEquals(Optional.empty(),messengerUserRepository.findByUsername("user1"));
    }

    @Test
    void whenDeleteUserThatDoesNotExist_thenExpectException(){
        Assertions.assertEquals(Optional.empty(),messengerUserRepository.findById(3L));
        Assertions.assertEquals(Optional.empty(),messengerUserRepository.findByUsername("user3"));
        Assertions.assertThrows(EmptyResultDataAccessException.class,()->{
            messengerUserRepository.deleteById(3L);
        });
    }

}