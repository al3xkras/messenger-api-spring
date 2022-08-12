package com.al3xkras.messenger.user_service.service;

import com.al3xkras.messenger.user_service.entity.MessengerUser;
import com.al3xkras.messenger.user_service.exception.MessengerUserAlreadyExistsException;
import com.al3xkras.messenger.user_service.exception.MessengerUserNotFoundException;
import com.al3xkras.messenger.user_service.model.MessengerUserType;
import com.al3xkras.messenger.user_service.repository.MessengerUserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;


@SpringBootTest
class MessengerUserServiceTest {

    @Autowired
    private MessengerUserService messengerUserService;

    @MockBean
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

    static MessengerUser userWithExistingUsername = MessengerUser.builder()
            .username("user1").build();

    @BeforeEach
    void beforeEach(){
        Mockito.when(messengerUserRepository.findByUsername("user1"))
                .thenReturn(Optional.of(firstUser));
        Mockito.when(messengerUserRepository.findById(1L))
                .thenReturn(Optional.of(firstUser));

        Mockito.when(messengerUserRepository.findByUsername("user2"))
                .thenReturn(Optional.of(secondUser));
        Mockito.when(messengerUserRepository.findById(2L))
                .thenReturn(Optional.of(secondUser));

        Mockito.when(messengerUserRepository.save(updatedFirstUser)).
                thenAnswer(invocation->{
                    Mockito.when(messengerUserRepository.findByUsername("user1"))
                            .thenReturn(Optional.of(updatedFirstUser));
                    Mockito.when(messengerUserRepository.findById(1L))
                            .thenReturn(Optional.of(updatedFirstUser));
                    return updatedFirstUser;
                });

        Mockito.when(messengerUserRepository.findById(3L)).thenReturn(Optional.empty());
        Mockito.when(messengerUserRepository.findByUsername("user3")).thenReturn(Optional.empty());

        Mockito.when(messengerUserRepository.save(userToSave))
                .thenAnswer(invocationOnMock -> {
                    Mockito.when(messengerUserRepository.findByUsername("user3"))
                            .thenReturn(Optional.of(userToSaveWithId));
                    Mockito.when(messengerUserRepository.findById(3L))
                            .thenReturn(Optional.of(userToSaveWithId));
                    return userToSaveWithId;
                });

        DataIntegrityViolationException sqlIntegrity = new DataIntegrityViolationException("username");
        Mockito.when(messengerUserRepository.save(userWithExistingUsername))
                .thenThrow(sqlIntegrity);
    }

    @Test
    void whenUserExists_thenFind(){
        MessengerUser messengerUser1 = messengerUserService.findMessengerUserById(1L);
        Assertions.assertEquals(firstUser,messengerUser1);

        MessengerUser messengerUser2 = messengerUserService.findMessengerUserById(2L);
        Assertions.assertEquals(secondUser,messengerUser2);

        MessengerUser messengerUser3 = messengerUserService.findMessengerUserByUsername("user1");
        Assertions.assertEquals(firstUser,messengerUser3);

        MessengerUser messengerUser4 = messengerUserService.findMessengerUserByUsername("user2");
        Assertions.assertEquals(secondUser,messengerUser4);
    }

    @Test
    void whenUserNotExists_thenThrowException(){
        Assertions.assertThrows(MessengerUserNotFoundException.class,()->{
            MessengerUser messengerUser = messengerUserService.findMessengerUserById(3L);
        });
        Assertions.assertThrows(MessengerUserNotFoundException.class,()->{
            MessengerUser messengerUser = messengerUserService.findMessengerUserByUsername("user3");
        });
    }

    @Test
    void whenUpdateUserById_thenReturnUpdated(){
        Assertions.assertEquals(firstUser,messengerUserService.findMessengerUserById(1L));
        messengerUserService.updateUserById(updatedFirstUser);
        Assertions.assertEquals(updatedFirstUser,messengerUserService.findMessengerUserById(1L));
        Assertions.assertEquals(updatedFirstUser,messengerUserService.findMessengerUserByUsername("user1"));
    }

    @Test
    void whenUpdateUserByName_thenReturnUpdated(){
        Assertions.assertEquals(firstUser,messengerUserService.findMessengerUserById(1L));
        messengerUserService.updateUserByUsername(updatedFirstUser);
        Assertions.assertEquals(updatedFirstUser,messengerUserService.findMessengerUserById(1L));
        Assertions.assertEquals(updatedFirstUser,messengerUserService.findMessengerUserByUsername("user1"));
    }

    @Test
    void whenUpdateUserThatDoesNotExist_thenThrowException(){
        Assertions.assertThrows(MessengerUserNotFoundException.class,()->{
            MessengerUser user3 = MessengerUser.builder().messengerUserId(3L).username("user3").build();
            messengerUserService.updateUserById(user3);
        });
        Assertions.assertThrows(MessengerUserNotFoundException.class,()->{
            MessengerUser user3 = MessengerUser.builder().messengerUserId(3L).username("user3").build();
            messengerUserService.updateUserByUsername(user3);
        });
    }

    @Test
    void whenSaveNewUser_thenReturnNewUserWithId(){
        Assertions.assertThrows(MessengerUserNotFoundException.class,()->{
           messengerUserService.findMessengerUserById(3L);
        });
        messengerUserService.saveUser(userToSave);

        Assertions.assertEquals(userToSaveWithId,messengerUserService.findMessengerUserById(3L));
        Assertions.assertEquals(userToSaveWithId,messengerUserService.findMessengerUserByUsername("user3"));
    }

    @Test
    void whenSaveExistingUser_thenExpectException(){
        Assertions.assertThrows(MessengerUserAlreadyExistsException.class,()->{
           messengerUserService.saveUser(userWithExistingUsername);
        });
    }

}