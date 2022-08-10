package com.al3xkras.messengeruserservice;

import com.al3xkras.messengeruserservice.controller.MessengerUserController;
import com.al3xkras.messengeruserservice.dto.MessengerUserDTO;
import com.al3xkras.messengeruserservice.entity.Chat;
import com.al3xkras.messengeruserservice.entity.ChatUser;
import com.al3xkras.messengeruserservice.entity.MessengerUser;
import com.al3xkras.messengeruserservice.model.ChatUserRole;
import com.al3xkras.messengeruserservice.model.MessengerUserType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.web.servlet.MockMvc;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MessengerUserServiceApplicationTests {

	@PersistenceContext
	private EntityManager entityManager;
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

	@Test
	@Order(2)
	void testFindUserById() throws Exception{

		mockMvc.perform(get("/user/"+firstUser.getMessengerUserId()))
				.andExpect(status().isOk())
				.andExpect(content().json(objectMapper.writeValueAsString(firstUser)));

		mockMvc.perform(get("/user/"+thirdUser.getMessengerUserId()))
				.andExpect(status().isOk())
				.andExpect(content().json(objectMapper.writeValueAsString(thirdUser)));

		mockMvc.perform(get("/user/"+10L))
				.andExpect(status().isNotFound())
				.andExpect(content().string("user not found"));

	}

	@Test
	@Order(3)
	void testFindUserByUsername() throws Exception {

		mockMvc.perform(get("/user").param("username",firstUser.getUsername()))
				.andExpect(status().isOk())
				.andExpect(content().json(objectMapper.writeValueAsString(firstUser)));

		mockMvc.perform(get("/user").param("username",secondUser.getUsername()))
				.andExpect(status().isOk())
				.andExpect(content().json(objectMapper.writeValueAsString(secondUser)));

		mockMvc.perform(get("/user"))
				.andExpect(status().isBadRequest());

		mockMvc.perform(get("/user").param("username","user10000"))
				.andExpect(status().isNotFound())
				.andExpect(content().string("user not found"));

	}

	@Test
	@Order(4)
	void testEditUserData() throws Exception {

		MessengerUserDTO validDto1 = MessengerUserDTO.builder()
				.username("username")
				.name(firstUser.getName())
				.password("1a83F_234567$")
				.surname(firstUser.getSurname())
				.phoneNumber(firstUser.getPhoneNumber())
				.email(firstUser.getEmailAddress())
				.messengerUserType(firstUser.getMessengerUserType())
				.build();
		MessengerUserDTO validDto2 = MessengerUserDTO.builder()
				.username(firstUser.getUsername())
				.name(firstUser.getName())
				.password("1a83F_23.")
				.surname("new Surname")
				.phoneNumber(firstUser.getPhoneNumber())
				.email(firstUser.getEmailAddress())
				.messengerUserType(firstUser.getMessengerUserType())
				.build();
		MessengerUserDTO validDto3 = MessengerUserDTO.builder()
				.username("newSecondUser")
				.name(secondUser.getName())
				.password("1a83F_23.")
				.surname("SurnameForSecondUser")
				.phoneNumber(secondUser.getPhoneNumber())
				.email(secondUser.getEmailAddress())
				.messengerUserType(secondUser.getMessengerUserType())
				.build();

		MessengerUserDTO usernameExistsWhenModifyByUserIdDto = MessengerUserDTO.builder()
				.username(thirdUser.getUsername())
				.name(thirdUser.getName())
				.password("1a83F_234567$")
				.surname(thirdUser.getSurname())
				.phoneNumber("+472 435-22-13")
				.email(thirdUser.getEmailAddress())
				.messengerUserType(thirdUser.getMessengerUserType())
				.build();

		MessengerUserDTO invalidDto = MessengerUserDTO.builder()
				.build();

		MessengerUser[] afterEdit = new MessengerUser[1];


		mockMvc.perform(put("/user").param("user-id",firstUser.getMessengerUserId().toString())
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(validDto1)))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andDo(result-> afterEdit[0] = objectMapper.readValue(
						result.getResponse().getContentAsString(),MessengerUser.class)
				);
		assertNotNull(afterEdit[0]);
		assertNotEquals(firstUser,afterEdit[0]);
		firstUser = afterEdit[0];
		log.info(firstUser.toString());


		mockMvc.perform(put("/user").param("username",firstUser.getUsername())
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(validDto2)))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andDo(result-> afterEdit[0] = objectMapper.readValue(
						result.getResponse().getContentAsString(),MessengerUser.class)
				);
		assertNotNull(afterEdit[0]);
		assertNotEquals(firstUser,afterEdit[0]);
		firstUser = afterEdit[0];
		log.info(firstUser.toString());

		//modify by ID
		mockMvc.perform(put("/user")
						.param("user-id",secondUser.getMessengerUserId().toString())
						.param("username",secondUser.getUsername())
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(validDto3)))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andDo(result-> afterEdit[0] = objectMapper.readValue(
						result.getResponse().getContentAsString(),MessengerUser.class)
				);
		assertNotNull(afterEdit[0]);
		assertNotEquals(secondUser,afterEdit[0]);
		assertEquals(validDto3.getUsername(),afterEdit[0].getUsername());
		secondUser = afterEdit[0];
		log.info(secondUser.toString());


		mockMvc.perform(put("/user")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(validDto1)))
				.andExpect(status().isBadRequest());


		mockMvc.perform(put("/user").param("user-id",firstUser.getMessengerUserId().toString())
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(usernameExistsWhenModifyByUserIdDto)))
				.andExpect(status().isBadRequest())
				.andExpect(content().string("messenger user with specified username already exists"));

		mockMvc.perform(put("/user").param("user-id",firstUser.getMessengerUserId().toString())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(invalidDto)))
				.andExpect(status().isBadRequest());

	}

	@Test
	@Order(5)
	void testFindUserByUsernameAfterUpdate() throws Exception {

		mockMvc.perform(get("/user").param("username",firstUser.getUsername()))
				.andExpect(status().isOk())
				.andExpect(content().json(objectMapper.writeValueAsString(firstUser)));

		mockMvc.perform(get("/user").param("username",secondUser.getUsername()))
				.andExpect(status().isOk())
				.andExpect(content().json(objectMapper.writeValueAsString(secondUser)));
	}

	@Test
	@Order(6)
	void testDeleteMessengerUser() throws Exception{

		mockMvc.perform(delete("/user")
						.param("user-id",firstUser.getMessengerUserId().toString())
						.param("username","thisUsernameWillBeIgnored"))
				.andExpect(status().isOk())
				.andExpect(content().string("deleted user with id "+firstUser.getMessengerUserId()));

		mockMvc.perform(delete("/user")
				.param("username",secondUser.getUsername()))
				.andExpect(status().isOk())
				.andExpect(content().string("deleted user with username : \""+secondUser.getUsername()+'\"'));

		mockMvc.perform(delete("/user"))
				.andExpect(status().isBadRequest());

		mockMvc.perform(delete("/user")
						.param("username","user1897432"))
				.andExpect(status().isNotFound())
				.andExpect(content().string( "user not found"));

		mockMvc.perform(delete("/user")
				.param("user-id",firstUser.getMessengerUserId().toString()))
				.andExpect(status().isNotFound())
				.andExpect(content().string( "user not found"));

	}

	@Test
	@Order(7)
	void testAddNewUserAfterDelete() throws Exception{
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

		firstUser.setMessengerUserId(5L);
		secondUser.setMessengerUserId(6L);
		mockMvc.perform(post("/user").contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(validUserDto1)))
				.andExpect(status().isOk())
				.andExpect(content().json(objectMapper.writeValueAsString(firstUser)));

		mockMvc.perform(post("/user").contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(validUserDto2)))
				.andExpect(status().isOk())
				.andExpect(content().json(objectMapper.writeValueAsString(secondUser)));
	}

}
