package com.al3xkras.messenger.user_service;

import com.al3xkras.messenger.dto.MessengerUserDTO;
import com.al3xkras.messenger.entity.MessengerUser;
import com.al3xkras.messenger.model.MessengerUserType;
import com.al3xkras.messenger.model.MessengerUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("security-test")
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

	static MessengerUser firstUser = MessengerUser.FIRST_ADMIN;
	static MessengerUser secondUser = MessengerUser.builder()
			.messengerUserId(2L)
			.username(nextUniqueUsername())
			.password("Password123.")
			.name("Andrew")
			.emailAddress("andrew@gmail.com")
			.phoneNumber("+31234567")
			.messengerUserType(MessengerUserType.USER)
			.build();
	static MessengerUser thirdUser = MessengerUser.builder()
			.messengerUserId(3L)
			.username(nextUniqueUsername())
			.password("Password123.")
			.name("Mike")
			.emailAddress("mike@gmail.com")
			.phoneNumber("+4 564-7564")
			.messengerUserType(MessengerUserType.USER)
			.build();

	static String adminToken;
	static String secondUserToken;

	void updateUserToken() throws Exception {
		MockHttpServletResponse response = mockMvc.perform(post("/user/login")
						.param("username",secondUser.getUsername())
						.param("password",secondUser.getPassword()))
				.andExpect(status().isOk())
				.andReturn().getResponse();
		secondUserToken = response.getHeader("access-token");
		assertNotNull(secondUserToken);

	}

	void updateAdminToken() throws Exception {
		MockHttpServletResponse response = mockMvc.perform(post("/user/login")
						.param("username",firstUser.getUsername())
						.param("password",firstUser.getPassword()))
				.andExpect(status().isOk())
				.andReturn().getResponse();
		adminToken = response.getHeader("access-token");
		assertNotNull(adminToken);
	}

	@Test
	@Order(1)
	void testAddNewUser() throws Exception {
		MessengerUserDTO validUserDto2 = MessengerUserDTO.builder()
				.username(secondUser.getUsername())
				.name(secondUser.getName())
				.password(secondUser.getPassword())
				.surname(secondUser.getSurname())
				.phoneNumber(secondUser.getPhoneNumber())
				.email(secondUser.getEmailAddress())
				.messengerUserType(secondUser.getMessengerUserType())
				.build();
		MessengerUserDTO validUserDto3 = MessengerUserDTO.builder()
				.username(thirdUser.getUsername())
				.name(thirdUser.getName())
				.password(thirdUser.getPassword())
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


		updateUserToken();

	}

	@Test
	@Order(2)
	void testFindUserById() throws Exception{

		updateAdminToken();

		mockMvc.perform(get("/user/"+firstUser.getMessengerUserId())
						.header(HttpHeaders.AUTHORIZATION,"Bearer "+adminToken))
				.andExpect(status().isOk())
				.andExpect(content().json(objectMapper.writeValueAsString(firstUser)));

		mockMvc.perform(get("/user/"+firstUser.getMessengerUserId())
						.header(HttpHeaders.AUTHORIZATION,"Bearer "+ secondUserToken))
				.andExpect(status().isForbidden());

		mockMvc.perform(get("/user/"+thirdUser.getMessengerUserId())
						.header(HttpHeaders.AUTHORIZATION,"Bearer "+adminToken))
				.andExpect(status().isOk())
				.andExpect(content().json(objectMapper.writeValueAsString(thirdUser)));

		mockMvc.perform(get("/user/"+thirdUser.getMessengerUserId())
						.header(HttpHeaders.AUTHORIZATION,"Bearer "+ secondUserToken))
				.andExpect(status().isForbidden());

		mockMvc.perform(get("/user/"+secondUser.getMessengerUserId())
						.header(HttpHeaders.AUTHORIZATION,"Bearer "+ secondUserToken))
				.andExpect(status().isOk())
				.andExpect(content().json(objectMapper.writeValueAsString(secondUser)));

		mockMvc.perform(get("/user/"+10L)
						.header(HttpHeaders.AUTHORIZATION,"Bearer "+adminToken))
				.andExpect(status().isNotFound())
				.andExpect(content().string(MessengerUtils.Messages.EXCEPTION_MESSENGER_USER_NOT_FOUND.value()));

		mockMvc.perform(get("/user/"+10L))
				.andExpect(status().isForbidden());

	}

	@Test
	@Order(3)
	void testFindUserByUsername() throws Exception {

		mockMvc.perform(get("/user").param("username",firstUser.getUsername())
						.header(HttpHeaders.AUTHORIZATION,"Bearer "+adminToken))
				.andExpect(status().isOk())
				.andExpect(content().json(objectMapper.writeValueAsString(firstUser)));

		mockMvc.perform(get("/user").param("username",firstUser.getUsername())
						.header(HttpHeaders.AUTHORIZATION,"Bearer "+secondUserToken))
				.andExpect(status().isForbidden());

		mockMvc.perform(get("/user").param("username",secondUser.getUsername())
						.header(HttpHeaders.AUTHORIZATION,"Bearer "+adminToken))
				.andExpect(status().isOk())
				.andExpect(content().json(objectMapper.writeValueAsString(secondUser)));

		mockMvc.perform(get("/user").param("username",secondUser.getUsername())
						.header(HttpHeaders.AUTHORIZATION,"Bearer "+secondUserToken))
				.andExpect(status().isOk())
				.andExpect(content().json(objectMapper.writeValueAsString(secondUser)));

		mockMvc.perform(get("/user").param("username",secondUser.getUsername()))
				.andExpect(status().isForbidden());

		mockMvc.perform(get("/user")
						.header(HttpHeaders.AUTHORIZATION,"Bearer "+adminToken))
				.andExpect(status().isBadRequest());

		mockMvc.perform(get("/user"))
				.andExpect(status().isForbidden());

		mockMvc.perform(get("/user").param("username","user10000")
						.header(HttpHeaders.AUTHORIZATION,"Bearer "+adminToken))
				.andExpect(status().isNotFound())
				.andExpect(content().string(MessengerUtils.Messages.EXCEPTION_MESSENGER_USER_NOT_FOUND.value()));

		mockMvc.perform(get("/user").param("username","user10000")
						.header(HttpHeaders.AUTHORIZATION,"Bearer "+secondUserToken))
				.andExpect(status().isForbidden());

	}

	@Test
	@Order(4)
	void testEditUserData() throws Exception {

		String validPhone = "+23 245-44-29";
		MessengerUserDTO validDto1 = MessengerUserDTO.builder()
				.username("username")
				.name(firstUser.getName())
				.password("1a83F_234567$")
				.surname(firstUser.getSurname())
				.phoneNumber(validPhone)
				.email(firstUser.getEmailAddress())
				.messengerUserType(firstUser.getMessengerUserType())
				.build();
		MessengerUserDTO validDto2 = MessengerUserDTO.builder()
				.username(firstUser.getUsername())
				.name(firstUser.getName())
				.password("1a83F_23.")
				.surname("new Surname")
				.phoneNumber(validPhone)
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


		mockMvc.perform(put("/user")
						.param("user-id",firstUser.getMessengerUserId().toString())
						.header(HttpHeaders.AUTHORIZATION,"Bearer "+adminToken)
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

		mockMvc.perform(put("/user")
						.param("user-id",firstUser.getMessengerUserId().toString())
						.header(HttpHeaders.AUTHORIZATION,"Bearer "+secondUserToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(validDto1)))
				.andExpect(status().isForbidden());

		mockMvc.perform(put("/user")
						.param("user-id",firstUser.getMessengerUserId().toString())
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(validDto1)))
				.andExpect(status().isForbidden());

		mockMvc.perform(put("/user")
						.param("username",firstUser.getUsername())
						.header(HttpHeaders.AUTHORIZATION,"Bearer "+adminToken)
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
						.header(HttpHeaders.AUTHORIZATION,"Bearer "+secondUserToken)
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
						.header(HttpHeaders.AUTHORIZATION,"Bearer "+adminToken)
						.param("user-id",secondUser.getMessengerUserId().toString())
						.param("username",secondUser.getUsername())
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(validDto3)))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON));


		mockMvc.perform(put("/user")
						.header(HttpHeaders.AUTHORIZATION,"Bearer "+adminToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(validDto1)))
				.andExpect(status().isBadRequest());

		mockMvc.perform(put("/user")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(validDto1)))
				.andExpect(status().isForbidden());

		mockMvc.perform(put("/user")
						.header(HttpHeaders.AUTHORIZATION,"Bearer "+adminToken)
						.param("user-id",firstUser.getMessengerUserId().toString())
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(usernameExistsWhenModifyByUserIdDto)))
				.andExpect(status().isBadRequest())
				.andExpect(content().string(String.format(MessengerUtils.Messages.EXCEPTION_MESSENGER_USER_USERNAME_EXISTS.value(),usernameExistsWhenModifyByUserIdDto.getUsername())));

		mockMvc.perform(put("/user")
						.param("user-id",firstUser.getMessengerUserId().toString())
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(usernameExistsWhenModifyByUserIdDto)))
				.andExpect(status().isForbidden());

		mockMvc.perform(put("/user")
						.header(HttpHeaders.AUTHORIZATION,"Bearer "+adminToken)
						.param("user-id",firstUser.getMessengerUserId().toString())
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(invalidDto)))
				.andExpect(status().isBadRequest());

		mockMvc.perform(put("/user")
						.header(HttpHeaders.AUTHORIZATION,"Bearer "+secondUserToken)
						.param("user-id",firstUser.getMessengerUserId().toString())
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(invalidDto)))
				.andExpect(status().isForbidden());

		mockMvc.perform(put("/user")
						.param("user-id",firstUser.getMessengerUserId().toString())
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(invalidDto)))
				.andExpect(status().isForbidden());

		updateUserToken();
	}

	@Test
	@Order(5)
	void testFindUserByUsernameAfterUpdate() throws Exception {

		mockMvc.perform(get("/user")
						.header(HttpHeaders.AUTHORIZATION,"Bearer "+adminToken)
						.param("username",firstUser.getUsername()))
				.andExpect(status().isOk())
				.andExpect(content().json(objectMapper.writeValueAsString(firstUser)));

		mockMvc.perform(get("/user")
						.header(HttpHeaders.AUTHORIZATION,"Bearer "+secondUserToken)
						.param("username",firstUser.getUsername()))
				.andExpect(status().isForbidden());

		mockMvc.perform(get("/user")
						.param("username",firstUser.getUsername()))
				.andExpect(status().isForbidden());

		mockMvc.perform(get("/user")
						.header(HttpHeaders.AUTHORIZATION,"Bearer "+secondUserToken)
						.param("username",secondUser.getUsername()))
				.andExpect(status().isOk())
				.andExpect(content().json(objectMapper.writeValueAsString(secondUser)));

		mockMvc.perform(get("/user")
						.header(HttpHeaders.AUTHORIZATION,"Bearer "+adminToken)
						.param("username",secondUser.getUsername()))
				.andExpect(status().isOk())
				.andExpect(content().json(objectMapper.writeValueAsString(secondUser)));

		mockMvc.perform(get("/user")
						.param("username",secondUser.getUsername()))
				.andExpect(status().isForbidden());
	}

	@Test
	@Order(6)
	void testDeleteMessengerUser() throws Exception{

		mockMvc.perform(delete("/user")
						.param("user-id",firstUser.getMessengerUserId().toString())
						.param("username","thisUsernameWillBeIgnored"))
				.andExpect(status().isForbidden());

		mockMvc.perform(delete("/user")
						.header(HttpHeaders.AUTHORIZATION,"Bearer "+secondUserToken)
						.param("user-id",firstUser.getMessengerUserId().toString())
						.param("username","thisUsernameWillBeIgnored"))
				.andExpect(status().isForbidden());

		mockMvc.perform(delete("/user")
						.header(HttpHeaders.AUTHORIZATION,"Bearer "+adminToken)
						.param("user-id",firstUser.getMessengerUserId().toString())
						.param("username","thisUsernameWillBeIgnored"))
				.andExpect(status().isForbidden());

		mockMvc.perform(delete("/user")
						.header(HttpHeaders.AUTHORIZATION,"Bearer "+secondUserToken)
						.param("username",secondUser.getUsername()))
				.andExpect(status().isOk());

		mockMvc.perform(delete("/user")
						.header(HttpHeaders.AUTHORIZATION,"Bearer "+adminToken))
				.andExpect(status().isBadRequest());

		mockMvc.perform(delete("/user")
						.header(HttpHeaders.AUTHORIZATION,"Bearer "+secondUserToken))
				.andExpect(status().isBadRequest());

		mockMvc.perform(delete("/user"))
				.andExpect(status().isForbidden());

		mockMvc.perform(delete("/user")
						.header(HttpHeaders.AUTHORIZATION,"Bearer "+adminToken)
						.param("username","user1897432"))
				.andExpect(status().isNotFound())
				.andExpect(content().string( MessengerUtils.Messages.EXCEPTION_MESSENGER_USER_NOT_FOUND.value()));

		mockMvc.perform(delete("/user")
						.header(HttpHeaders.AUTHORIZATION,"Bearer "+secondUserToken)
						.param("username","user1897432"))
				.andExpect(status().isForbidden());

		mockMvc.perform(delete("/user")
						.param("username","user1897432"))
				.andExpect(status().isForbidden());

		mockMvc.perform(delete("/user")
						.header(HttpHeaders.AUTHORIZATION,"Bearer "+adminToken)
						.param("user-id",secondUser.getMessengerUserId().toString()))
				.andExpect(status().isNotFound())
				.andExpect(content().string( MessengerUtils.Messages.EXCEPTION_MESSENGER_USER_NOT_FOUND.value()));

	}

	@Test
	@Order(7)
	void testAddNewUserAfterDelete() throws Exception{
		MessengerUserDTO validUserDto2 = MessengerUserDTO.builder()
				.username(secondUser.getUsername())
				.name(secondUser.getName())
				.password(secondUser.getPassword())
				.surname(secondUser.getSurname())
				.phoneNumber(secondUser.getPhoneNumber())
				.email(secondUser.getEmailAddress())
				.messengerUserType(secondUser.getMessengerUserType())
				.build();

		secondUser.setMessengerUserId(5L);

		//No Auth
		mockMvc.perform(post("/user").contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(validUserDto2)))
				.andExpect(status().isOk())
				.andExpect(content().json(objectMapper.writeValueAsString(secondUser)));

	}

	@Test
	@Order(8)
	void testSecurity() throws Exception {

		//When Add user of type admin, expect HttpStatus.FORBIDDEN
		MessengerUserDTO validAdminDto = MessengerUserDTO.builder()
				.username(thirdUser.getUsername())
				.name(thirdUser.getName())
				.password(thirdUser.getPassword())
				.surname(thirdUser.getSurname())
				.phoneNumber(thirdUser.getPhoneNumber())
				.email(thirdUser.getEmailAddress())
				.messengerUserType(MessengerUserType.ADMIN)
				.build();

		mockMvc.perform(post("/user")
						.header(HttpHeaders.AUTHORIZATION,"Bearer "+adminToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(validAdminDto)))
				.andExpect(status().isForbidden());

		mockMvc.perform(post("/user")
						.header(HttpHeaders.AUTHORIZATION,"Bearer "+secondUserToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(validAdminDto)))
				.andExpect(status().isForbidden());

		mockMvc.perform(post("/user")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(validAdminDto)))
				.andExpect(status().isForbidden());
	}

}
