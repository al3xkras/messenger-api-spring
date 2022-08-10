package com.al3xkras.messengeruserservice;

import com.al3xkras.messengeruserservice.controller.MessengerUserController;
import com.al3xkras.messengeruserservice.dto.MessengerUserDTO;
import com.al3xkras.messengeruserservice.entity.Chat;
import com.al3xkras.messengeruserservice.entity.ChatUser;
import com.al3xkras.messengeruserservice.entity.MessengerUser;
import com.al3xkras.messengeruserservice.model.ChatUserRole;
import com.al3xkras.messengeruserservice.model.MessengerUserType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class MessengerUserServiceApplicationTests {

	@Autowired
	private MessengerUserController messengerUserController;
	@Autowired
	private MockMvc mockMvc;

	private ObjectMapper objectMapper = new ObjectMapper();

	static String usernamePrefix = "user";
	static int usernameNext = 1;
	static String nextUniqueUsername(){
		return usernamePrefix+(usernameNext++);
	}

	static MessengerUser firstUser = MessengerUser.builder()
			.messengerUserId(1L)
			.username(nextUniqueUsername())
			.name("Max")
			.emailAddress("max@gmail.com")
			.phoneNumber("+48 111-22-33")
			.messengerUserType(MessengerUserType.ADMIN)
			.build();
	static MessengerUser secondUser = MessengerUser.builder()
			.messengerUserId(2L)
			.username(nextUniqueUsername())
			.name("Andrew")
			.emailAddress("andrew@gmail.com")
			.phoneNumber("+31234567")
			.messengerUserType(MessengerUserType.USER)
			.build();
	static MessengerUser thirdUser = MessengerUser.builder()
			.messengerUserId(3L)
			.username(nextUniqueUsername())
			.name("Mike")
			.emailAddress("mike@gmail.com")
			.phoneNumber("+4 564-7564")
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



	@Test
	@Order(1)
	void testAddNewUser() throws Exception {
		MessengerUserDTO validUserDto1 = MessengerUserDTO.builder()
				.username(firstUser.getUsername())
				.name(firstUser.getName())
				.password("1a83F_23.")
				.surname(firstUser.getSurname())
				.phoneNumber(firstUser.getPhoneNumber())
				.email(firstUser.getEmailAddress())
				.messengerUserType(firstUser.getMessengerUserType())
				.build();
		MessengerUserDTO validUserDto2 = MessengerUserDTO.builder()
				.username(secondUser.getUsername())
				.name(secondUser.getName())
				.password("1a83F_23.")
				.surname(secondUser.getSurname())
				.phoneNumber(secondUser.getPhoneNumber())
				.email(secondUser.getEmailAddress())
				.messengerUserType(secondUser.getMessengerUserType())
				.build();
		MessengerUserDTO validUserDto3 = MessengerUserDTO.builder()
				.username(thirdUser.getUsername())
				.name(thirdUser.getName())
				.password("1a83F_23.")
				.surname(thirdUser.getSurname())
				.phoneNumber(thirdUser.getPhoneNumber())
				.email(thirdUser.getEmailAddress())
				.messengerUserType(thirdUser.getMessengerUserType())
				.build();
		MessengerUserDTO invalidUsernameUserDto1 = MessengerUserDTO.builder()
				.username("user 1")
				.name(firstUser.getName())
				.password("1a83F_23.")
				.surname(firstUser.getSurname())
				.phoneNumber(firstUser.getPhoneNumber())
				.email(firstUser.getEmailAddress())
				.messengerUserType(firstUser.getMessengerUserType())
				.build();
		MessengerUserDTO invalidUsernameUserDto2 = MessengerUserDTO.builder()
				.username(";user?%")
				.name(firstUser.getName())
				.password("1a83F_23.")
				.surname(firstUser.getSurname())
				.phoneNumber(firstUser.getPhoneNumber())
				.email(firstUser.getEmailAddress())
				.messengerUserType(firstUser.getMessengerUserType())
				.build();
		MessengerUserDTO invalidNameUserDto = MessengerUserDTO.builder()
				.username("user_1")
				.name(null)
				.password("1a83F_23.")
				.surname(firstUser.getSurname())
				.phoneNumber(firstUser.getPhoneNumber())
				.email(firstUser.getEmailAddress())
				.messengerUserType(firstUser.getMessengerUserType())
				.build();
		MessengerUserDTO existingUsernameUserDto = validUserDto3;
		MessengerUserDTO invalidPhoneUserDto = MessengerUserDTO.builder()
				.username(nextUniqueUsername())
				.name(firstUser.getName())
				.password("1a83F_23.")
				.surname(firstUser.getSurname())
				.phoneNumber("abc957873_")
				.email(firstUser.getEmailAddress())
				.messengerUserType(firstUser.getMessengerUserType())
				.build();
		MessengerUserDTO noMessengerUserTypeUserDto = MessengerUserDTO.builder()
				.username(nextUniqueUsername())
				.name(firstUser.getName())
				.password("1a83F_23.")
				.surname(firstUser.getSurname())
				.phoneNumber(firstUser.getPhoneNumber())
				.email(firstUser.getEmailAddress())
				.messengerUserType(null)
				.build();
		MessengerUserDTO invalidPasswordUserDto = MessengerUserDTO.builder()
				.username(nextUniqueUsername())
				.name(secondUser.getName())
				.password("12345678")
				.surname(secondUser.getSurname())
				.phoneNumber(secondUser.getPhoneNumber())
				.email(secondUser.getEmailAddress())
				.messengerUserType(secondUser.getMessengerUserType())
				.build();
		MessengerUserDTO invalidPasswordUserDto2 = MessengerUserDTO.builder()
				.username(nextUniqueUsername())
				.name(secondUser.getName())
				.password("abcdefgh.")
				.surname(secondUser.getSurname())
				.phoneNumber(secondUser.getPhoneNumber())
				.email(secondUser.getEmailAddress())
				.messengerUserType(secondUser.getMessengerUserType())
				.build();
		MessengerUserDTO invalidPasswordUserDto3 = MessengerUserDTO.builder()
				.username(nextUniqueUsername())
				.name(secondUser.getName())
				.password("123abc")
				.surname(secondUser.getSurname())
				.phoneNumber(secondUser.getPhoneNumber())
				.email(secondUser.getEmailAddress())
				.messengerUserType(secondUser.getMessengerUserType())
				.build();

		mockMvc.perform(post("/user").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(validUserDto1)))
				.andExpect(status().isOk())
				.andExpect(content().json(objectMapper.writeValueAsString(firstUser)));

		mockMvc.perform(post("/user").contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(validUserDto2)))
				.andExpect(status().isOk())
				.andExpect(content().json(objectMapper.writeValueAsString(secondUser)));
		mockMvc.perform(post("/user").contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(validUserDto3)))
				.andExpect(status().isOk())
				.andExpect(content().json(objectMapper.writeValueAsString(thirdUser)));

		mockMvc.perform(post("/user").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(invalidUsernameUserDto1)))
				.andExpect(status().isBadRequest());

		mockMvc.perform(post("/user").contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(invalidUsernameUserDto2)))
				.andExpect(status().isBadRequest());

		mockMvc.perform(post("/user").contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(invalidNameUserDto)))
				.andExpect(status().isBadRequest());

		mockMvc.perform(post("/user").contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(existingUsernameUserDto)))
				.andExpect(status().isBadRequest());

		mockMvc.perform(post("/user").contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(invalidPhoneUserDto)))
				.andExpect(status().isBadRequest());

		mockMvc.perform(post("/user").contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(noMessengerUserTypeUserDto)))
				.andExpect(status().isBadRequest());

		mockMvc.perform(post("/user").contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(invalidPasswordUserDto)))
				.andExpect(status().isBadRequest());

		mockMvc.perform(post("/user").contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(invalidPasswordUserDto2)))
				.andExpect(status().isBadRequest());

		mockMvc.perform(post("/user").contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(invalidPasswordUserDto3)))
				.andExpect(status().isBadRequest());

	}

}
