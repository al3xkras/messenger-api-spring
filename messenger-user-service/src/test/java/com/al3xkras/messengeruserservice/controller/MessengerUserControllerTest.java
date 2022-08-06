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

import static org.junit.jupiter.api.Assertions.*;

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
                .content("{\"username\":\"user3\",\"password\":\"password123\",\"name\":\"Michael\",\"surname\":\"Jackson\",\"email\":null,\"phoneNumber\":\"123-45-67\",\"messengerUserType\":\"USER\"}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(userAfterSave)));

    }
}