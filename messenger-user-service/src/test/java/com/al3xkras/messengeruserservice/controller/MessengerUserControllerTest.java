package com.al3xkras.messengeruserservice.controller;

import com.al3xkras.messengeruserservice.dto.MessengerUserDTO;
import com.al3xkras.messengeruserservice.entity.MessengerUser;
import com.al3xkras.messengeruserservice.exception.MessengerUserNotFoundException;
import com.al3xkras.messengeruserservice.model.MessengerUserType;
import com.al3xkras.messengeruserservice.service.MessengerUserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(MessengerUserController.class)
class MessengerUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    static ObjectMapper objectMapper = new ObjectMapper();

    @MockBean
    private MessengerUserService messengerUserService;

    static MessengerUser firstUser = MessengerUser.builder()
            .messengerUserId(1L)
            .username("user1")
            .name("Max")
            .emailAddress("max@gmail.com")
            .phoneNumber("111-22-33")
            .messengerUserType(MessengerUserType.ADMIN)
            .build();

    String firstUserJSON = "{\"messengerUserId\":1,\"username\":\"user1\",\"name\":\"Max\",\"surname\":null,\"emailAddress\":\"max@gmail.com\",\"phoneNumber\":\"111-22-33\",\"messengerUserType\":\"ADMIN\"}";

    MessengerUserDTO updatedFirstUserDTO = MessengerUserDTO.builder()
            .username("user1")
            .email("maxim@gmail.com")
            .phoneNumber("111-22-33")
            .messengerUserType(MessengerUserType.ADMIN)
            .build();
    MessengerUser firstUserUpdate = MessengerUser.builder()
            .messengerUserId(1L)
            .username(updatedFirstUserDTO.getUsername())
            .name(updatedFirstUserDTO.getName())
            .surname(updatedFirstUserDTO.getSurname())
            .emailAddress(updatedFirstUserDTO.getEmail())
            .phoneNumber(updatedFirstUserDTO.getPhoneNumber())
            .messengerUserType(updatedFirstUserDTO.getMessengerUserType())
            .build();
    MessengerUser firstUserAfterUpdate = MessengerUser.builder()
            .messengerUserId(1L)
            .username(updatedFirstUserDTO.getUsername())
            .name(firstUser.getName())
            .surname(updatedFirstUserDTO.getSurname())
            .emailAddress(updatedFirstUserDTO.getEmail())
            .phoneNumber(updatedFirstUserDTO.getPhoneNumber())
            .messengerUserType(updatedFirstUserDTO.getMessengerUserType())
            .build();

    @BeforeEach
    void setUp() {

    }

    @Test
    void whenFindExistingById_thenReturn() throws Exception {
        Mockito.when(messengerUserService.findMessengerUserById(1L))
                .thenReturn(firstUser);

        mockMvc.perform(MockMvcRequestBuilders.get("/user/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(firstUserJSON));
    }

    @Test
    void whenFindUserThatDoesNotExist_thenExpectHttpStatusNotFound() throws Exception {
        Mockito.when(messengerUserService.findMessengerUserById(10L))
                        .thenThrow(MessengerUserNotFoundException.class);
        mockMvc.perform(MockMvcRequestBuilders.get("/user/10"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("user not found"));
    }

    @Test
    void whenFindExistingByUsername_thenReturn() throws Exception {
        Mockito.when(messengerUserService.findMessengerUserByUsername("user1"))
                .thenReturn(firstUser);
        mockMvc.perform(MockMvcRequestBuilders.get("/user").param("username","user1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(firstUserJSON));
    }

    @Test
    void whenFindUserWithoutUsernameParam_thenReturnHttpStatusBadRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/user"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void whenFindUserByUsernameThatDoesNotExist_thenReturnHttpStatusNotFound() throws Exception {
        Mockito.when(messengerUserService.findMessengerUserByUsername("user3"))
                .thenThrow(MessengerUserNotFoundException.class);
        mockMvc.perform(MockMvcRequestBuilders.get("/user").param("username","user3"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("user not found"));
    }

    @Test
    void whenSaveNewUser_thenReturnNewUserWithId() throws Exception {

        MessengerUserDTO pojo = MessengerUserDTO.builder()
                .username("user3")
                .password("password123")
                .name("Michael")
                .surname("Jackson")
                .phoneNumber("123-45-67")
                .messengerUserType(MessengerUserType.USER)
                .build();

        MessengerUser userBeforeSave = MessengerUser.builder()
                .username(pojo.getUsername())
                .name(pojo.getName())
                .surname(pojo.getSurname())
                .emailAddress(pojo.getEmail())
                .phoneNumber(pojo.getPhoneNumber())
                .messengerUserType(pojo.getMessengerUserType())
                .build();

        MessengerUser userAfterSave = MessengerUser.builder()
                .messengerUserId(3L)
                .username(pojo.getUsername())
                .name(pojo.getName())
                .surname(pojo.getSurname())
                .emailAddress(pojo.getEmail())
                .phoneNumber(pojo.getPhoneNumber())
                .messengerUserType(pojo.getMessengerUserType())
                .build();

        Mockito.when(messengerUserService.saveUser(userBeforeSave))
                .thenReturn(userAfterSave);

        mockMvc.perform(MockMvcRequestBuilders.post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pojo)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(userAfterSave)));
    }

    @Test
    void whenUpdateUserThatExistsById_thenReturnUpdatedUser() throws Exception {
        Mockito.when(messengerUserService.updateUserById(firstUserUpdate))
                .thenReturn(firstUserAfterUpdate);
        Mockito.when(messengerUserService.updateUserByUsername(firstUserUpdate))
                .thenThrow(RuntimeException.class);

        mockMvc.perform(MockMvcRequestBuilders.put("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedFirstUserDTO))
                .param("user-id","1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(firstUserAfterUpdate)));
    }

    @Test
    void whenUpdateUserThatExists_andBothIdAndUsernameAreSpecified_thenUpdateUserById() throws Exception {
        Mockito.when(messengerUserService.updateUserById(firstUserUpdate))
                .thenReturn(firstUserAfterUpdate);
        Mockito.when(messengerUserService.updateUserByUsername(firstUserUpdate))
                .thenThrow(RuntimeException.class);

        mockMvc.perform(MockMvcRequestBuilders.put("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedFirstUserDTO))
                        .param("user-id","1")
                        .param("username","anonymouse"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(firstUserAfterUpdate)));
    }

    @Test
    void whenUpdateUserThatExistsByUsername_thenReturnUpdatedUser() throws Exception{

        MessengerUser firstUserUpdateByUsername = MessengerUser.builder()
                .username("user1")
                .name(updatedFirstUserDTO.getName())
                .surname(updatedFirstUserDTO.getSurname())
                .emailAddress(updatedFirstUserDTO.getEmail())
                .phoneNumber(updatedFirstUserDTO.getPhoneNumber())
                .messengerUserType(updatedFirstUserDTO.getMessengerUserType())
                .build();

        Mockito.when(messengerUserService.updateUserByUsername(firstUserUpdateByUsername))
                .thenReturn(firstUserAfterUpdate);

        mockMvc.perform(MockMvcRequestBuilders.put("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedFirstUserDTO))
                        .param("username","user1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(firstUserAfterUpdate)));
    }

    @Test
    void whenUpdateUser_andNoIdOrUsernameIsSpecified_thenReturnHttpStatusBadRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedFirstUserDTO)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("please specify \"username\" or \"user-id\""));
    }

    @Test
    void whenUpdateUserThatDoesNotExist_thenReturnHttpStatusNotFound() throws Exception{

        MessengerUser firstUserUpdateByUsername = MessengerUser.builder()
                .username("user1")
                .name(updatedFirstUserDTO.getName())
                .surname(updatedFirstUserDTO.getSurname())
                .emailAddress(updatedFirstUserDTO.getEmail())
                .phoneNumber(updatedFirstUserDTO.getPhoneNumber())
                .messengerUserType(updatedFirstUserDTO.getMessengerUserType())
                .build();

        Mockito.when(messengerUserService.updateUserById(firstUserUpdate))
                .thenThrow(MessengerUserNotFoundException.class);
        Mockito.when(messengerUserService.updateUserByUsername(firstUserUpdateByUsername))
                .thenThrow(MessengerUserNotFoundException.class);

        mockMvc.perform(MockMvcRequestBuilders.put("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedFirstUserDTO))
                        .param("user-id","1"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("user not found"));

        mockMvc.perform(MockMvcRequestBuilders.put("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedFirstUserDTO))
                        .param("username","user1"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("user not found"));
    }

    @Test
    void whenDeleteUserThatExists_thenReturnResponseStatusOk() throws Exception {
        Mockito.doNothing()
                .when(messengerUserService).deleteById(firstUser.getMessengerUserId());
        Mockito.doNothing()
                .when(messengerUserService).deleteByUsername(firstUser.getUsername());

        mockMvc.perform(MockMvcRequestBuilders.delete("/user").param("user-id",firstUser.getMessengerUserId().toString()))
                .andExpect(MockMvcResultMatchers.status().isOk());
        mockMvc.perform(MockMvcRequestBuilders.delete("/user").param("username",firstUser.getUsername()))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void whenDeleteUserThatDoesNotExist_thenThrowHttpStatusNotFound() throws Exception {
        Mockito.doThrow(MessengerUserNotFoundException.class)
                .when(messengerUserService).deleteById(firstUser.getMessengerUserId());
        Mockito.doThrow(MessengerUserNotFoundException.class)
                .when(messengerUserService).deleteByUsername(firstUser.getUsername());

        mockMvc.perform(MockMvcRequestBuilders.delete("/user").param("user-id",firstUser.getMessengerUserId().toString()))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("user not found"));
        mockMvc.perform(MockMvcRequestBuilders.delete("/user").param("username",firstUser.getUsername()))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("user not found"));
    }

    @Test
    void whenDeleteUser_andNoUsernameOrIdIsSpecified_thenThrowHttpStatusBadRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/user"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("please specify \"username\" or \"user-id\""));
    }
}