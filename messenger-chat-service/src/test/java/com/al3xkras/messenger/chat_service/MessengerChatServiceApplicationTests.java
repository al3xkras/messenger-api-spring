package com.al3xkras.messenger.chat_service;

import com.al3xkras.messenger.chat_service.config.SecurityConfig;
import com.al3xkras.messenger.chat_service.controller.ChatController;
import com.al3xkras.messenger.chat_service.repository.ChatRepository;
import com.al3xkras.messenger.dto.ChatDTO;
import com.al3xkras.messenger.dto.ChatUserDTO;
import com.al3xkras.messenger.dto.MessengerUserDTO;
import com.al3xkras.messenger.dto.PageRequestDto;
import com.al3xkras.messenger.entity.Chat;
import com.al3xkras.messenger.entity.ChatUser;
import com.al3xkras.messenger.entity.MessengerUser;
import com.al3xkras.messenger.model.ChatUserRole;
import com.al3xkras.messenger.model.MessengerUserType;
import com.al3xkras.messenger.model.RestResponsePage;
import com.al3xkras.messenger.chat_service.service.ChatService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.filter.TypeExcludeFilters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.UriComponentsBuilder;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("no-security")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MessengerChatServiceApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private RestTemplate restTemplate;

	private final ObjectMapper objectMapper = new ObjectMapper();

	static MessengerUser firstUser = MessengerUser.builder()
			.messengerUserId(1L)
			.username("user1")
			.password("Password123.")
			.name("Max")
			.emailAddress("max@gmail.com")
			.phoneNumber("+43 111-22-33")
			.messengerUserType(MessengerUserType.ADMIN)
			.build();
	static MessengerUser secondUser = MessengerUser.builder()
			.messengerUserId(2L)
			.username("user2")
			.password("Password123.")
			.name("Andrew")
			.emailAddress("andrew@gmail.com")
			.phoneNumber("+43 116-22-45")
			.messengerUserType(MessengerUserType.USER)
			.build();
	static MessengerUser thirdUser = MessengerUser.builder()
			.messengerUserId(3L)
			.username("user3")
			.password("Password123.")
			.name("Mike")
			.emailAddress("mike@gmail.com")
			.phoneNumber("+44 456-75-45")
			.messengerUserType(MessengerUserType.USER)
			.build();

	static Chat firstChat = Chat.builder()
			.chatId(1L)
			.chatName("first_chat")
			.chatDisplayName("First Chat!")
			.build();

	static Chat secondChat = Chat.builder()
			.chatId(2L)
			.chatName("second_chat")
			.chatDisplayName("Second Chat!")
			.build();

	static ChatUser chatUser11 = ChatUser.builder()
			.chat(firstChat)
			.messengerUser(firstUser)
			.title(ChatService.DEFAULT_TITLE_ADMIN)
			.chatUserRole(ChatUserRole.ADMIN)
			.build();

	static ChatUser chatUser12 = ChatUser.builder()
			.chat(firstChat)
			.messengerUser(secondUser)
			.chatUserRole(ChatUserRole.USER)
			.build();

	static ChatUser chatUser21 = ChatUser.builder()
			.chat(secondChat)
			.messengerUser(secondUser)
			.title(ChatService.DEFAULT_TITLE_ADMIN)
			.chatUserRole(ChatUserRole.ADMIN)
			.build();

	static ChatUser chatUser22 = ChatUser.builder()
			.chat(secondChat)
			.messengerUser(firstUser)
			.chatUserRole(ChatUserRole.USER)
			.build();

	static ChatUser chatUser23 = ChatUser.builder()
			.chat(secondChat)
			.messengerUser(thirdUser)
			.chatUserRole(ChatUserRole.USER)
			.build();

	static String userAuthToken="";
	static String adminAuthToken="";

	static String chatUser11AuthToken;
	static String chatUser22AuthToken;

	@Test
	@Order(0)
	void getAdminToken(){
		String requestUri = UriComponentsBuilder.fromUriString("http://localhost:10001/user/login")
				.queryParam("username",firstUser.getUsername())
				.queryParam("password",firstUser.getPassword())
				.toUriString();
		RequestEntity<?> requestEntity = RequestEntity.post(requestUri)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.build();
		ResponseEntity<?> responseEntity = restTemplate.exchange(requestEntity,Object.class);
		adminAuthToken = responseEntity.getHeaders().getFirst("access-token");
		assertNotNull(adminAuthToken);
	}

	@Test
	@Order(10)
	void persistUsers() throws Exception{
		MessengerUserDTO validUserDto1 = MessengerUserDTO.builder()
				.username(firstUser.getUsername())
				.name(firstUser.getName())
				.password(firstUser.getPassword())
				.surname(firstUser.getSurname())
				.phoneNumber(firstUser.getPhoneNumber())
				.email(firstUser.getEmailAddress())
				.messengerUserType(firstUser.getMessengerUserType())
				.build();
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

		ResponseEntity<MessengerUser> response;
		try {
			response = restTemplate.exchange(RequestEntity.post("http://localhost:10001/user")
					.contentType(MediaType.APPLICATION_JSON)
					.body(objectMapper.writeValueAsString(validUserDto1)), MessengerUser.class);
			assertNotNull(response);
			firstUser = response.getBody();
			//No Auth required
			response = restTemplate.exchange(RequestEntity.post("http://localhost:10001/user")
					.contentType(MediaType.APPLICATION_JSON)
					.body(objectMapper.writeValueAsString(validUserDto2)), MessengerUser.class);
			assertNotNull(response.getBody());
			secondUser = response.getBody();
			//No Auth required
			response = restTemplate.exchange(RequestEntity.post("http://localhost:10001/user")
					.contentType(MediaType.APPLICATION_JSON)
					.body(objectMapper.writeValueAsString(validUserDto3)), MessengerUser.class);
			assertNotNull(response.getBody());
			thirdUser = response.getBody();

		} catch (ResourceAccessException r){
			log.error("messenger user service is down. please enable it");
		} catch (HttpClientErrorException e){
			e.printStackTrace();
		}
	}

	@Test
	@Order(11)
	void getUserToken(){

		String requestUri = UriComponentsBuilder.fromUriString("http://localhost:10001/user/login")
				.queryParam("username",secondUser.getUsername())
				.queryParam("password",secondUser.getPassword())
				.toUriString();
		RequestEntity<?> requestEntity = RequestEntity.post(requestUri)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.build();
		ResponseEntity<?> responseEntity = restTemplate.exchange(requestEntity,Object.class);
		userAuthToken = responseEntity.getHeaders().getFirst("access-token");
		assertNotNull(userAuthToken);
	}

	@Test
	@Order(12)
	void testAuthentication(){

	}

	@Test
	@Order(20)
	void testCreateChat() throws Exception {
		ChatDTO validChatDto1 = ChatDTO.builder()
				.chatName(firstChat.getChatName())
				.displayName(firstChat.getChatDisplayName())
				.ownerId(firstUser.getMessengerUserId())
				.build();

		ChatDTO validChatDto2 = ChatDTO.builder()
				.chatId(2L) //ID is ignored
				.chatName(secondChat.getChatName())
				.displayName(secondChat.getChatDisplayName())
				.ownerId(secondUser.getMessengerUserId())
				.build();

		ChatDTO invalidChatNameDto = ChatDTO.builder()
				.chatName("chat 3")
				.displayName("Chat 3")
				.ownerId(secondUser.getMessengerUserId())
				.build();

		ChatDTO noDisplayNameChatDTO = ChatDTO.builder()
				.chatId(4L)
				.chatName("chat3")
				.ownerId(thirdUser.getMessengerUserId())
				.build();

		ChatDTO existingChatNameDto = validChatDto1;

		mockMvc.perform(post("/chat")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(validChatDto1)))
				.andExpect(status().isOk())
				.andExpect(content().json(objectMapper.writeValueAsString(firstChat)));

		mockMvc.perform(post("/chat")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(validChatDto2)))
				.andExpect(status().isOk())
				.andExpect(content().json(objectMapper.writeValueAsString(secondChat)));

		mockMvc.perform(post("/chat")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(invalidChatNameDto)))
				.andExpect(status().isBadRequest());

		mockMvc.perform(post("/chat")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(noDisplayNameChatDTO)))
				.andExpect(status().isBadRequest());

		mockMvc.perform(post("/chat")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(existingChatNameDto)))
				.andExpect(status().isBadRequest());

		mockMvc.perform(post("/chat"))
				.andExpect(status().isBadRequest());

	}

	@Test
	@Order(30)
	void testAddChatUser() throws Exception {

		ChatUserDTO validDto1 = ChatUserDTO.builder()
				.chatId(chatUser11.getChatId())
				.userId(chatUser11.getUserId())
				.title(chatUser11.getTitle())
				.chatUserRole(chatUser11.getChatUserRole())
				.build();
		ChatUserDTO validDto2 = ChatUserDTO.builder()
				.chatId(chatUser12.getChatId())
				.userId(chatUser12.getUserId())
				.title(chatUser12.getTitle())
				.chatUserRole(chatUser12.getChatUserRole())
				.build();
		ChatUserDTO validDto4 = ChatUserDTO.builder()
				.chatId(chatUser22.getChatId())
				.userId(chatUser22.getUserId())
				.title(chatUser22.getTitle())
				.chatUserRole(chatUser22.getChatUserRole())
				.build();
		ChatUserDTO validDto5 = ChatUserDTO.builder()
				.chatId(chatUser23.getChatId())
				.userId(chatUser23.getUserId())
				.title(chatUser23.getTitle())
				.chatUserRole(chatUser23.getChatUserRole())
				.build();
		ChatUserDTO invalidIdChatUserDto1 = ChatUserDTO.builder()
				.chatId(firstChat.getChatId())
				.title("title")
				.chatUserRole(ChatUserRole.USER)
				.build();
		ChatUserDTO invalidIdChatUserDto2 = ChatUserDTO.builder()
				.title("title")
				.chatUserRole(ChatUserRole.USER)
				.build();
		ChatUserDTO nullUserRoleChatDto = ChatUserDTO.builder()
				.chatId(firstChat.getChatId())
				.userId(thirdUser.getMessengerUserId())
				.title("title")
				.build();
		ChatUserDTO existingChatUserDto = validDto1;

		mockMvc.perform(post("/chat/users")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(validDto2)))
				.andExpect(status().isOk())
				.andExpect(content().json(objectMapper.writeValueAsString(chatUser12)));

		mockMvc.perform(post("/chat/users")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(validDto4)))
				.andExpect(status().isOk())
				.andExpect(content().json(objectMapper.writeValueAsString(chatUser22)));

		mockMvc.perform(post("/chat/users")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(validDto5)))
				.andExpect(status().isOk())
				.andExpect(content().json(objectMapper.writeValueAsString(chatUser23)));

		mockMvc.perform(post("/chat/users")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(invalidIdChatUserDto1)))
				.andExpect(status().isBadRequest());

		mockMvc.perform(post("/chat/users")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(invalidIdChatUserDto2)))
				.andExpect(status().isBadRequest());

		mockMvc.perform(post("/chat/users")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(nullUserRoleChatDto)))
				.andExpect(status().isBadRequest());

		mockMvc.perform(post("/chat/users")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(existingChatUserDto)))
				.andExpect(status().isBadRequest());

	}

	@Test
	@Order(40)
	void testGetAllChatUsersByChat() throws Exception{
		PageRequestDto firstPageOfSizeTwo = new PageRequestDto(0,2);
		PageRequestDto secondPageOfSizeTwo = new PageRequestDto(1,2);
		PageRequestDto pageOfSizeThree = new PageRequestDto(0,3);

		List<ChatUser> page1 = Arrays.asList(
				chatUser21,chatUser22
		);
		List<ChatUser> page2 = Arrays.asList(
				chatUser23
		);
		List<ChatUser> page3 = Arrays.asList(
				chatUser11,chatUser12
		);

		Page<ChatUser>[] actualPage = new Page[1];

		mockMvc.perform(get("/chat/users")
						.param("chat-id",secondChat.getChatId().toString())
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(firstPageOfSizeTwo)))
				.andExpect(status().isOk())
				.andDo(result -> actualPage[0] =
						objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<RestResponsePage<ChatUser>>(){}));
		assertEquals(new HashSet<>(page1), new HashSet<>(actualPage[0].toList()));

		mockMvc.perform(get("/chat/users")
						.param("chat-name", secondChat.getChatName())
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(secondPageOfSizeTwo)))
				.andExpect(status().isOk())
				.andDo(result -> actualPage[0] =
						objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<RestResponsePage<ChatUser>>(){}));
		assertEquals(new HashSet<>(page2), new HashSet<>(actualPage[0].toList()));

		mockMvc.perform(get("/chat/users")
						.param("chat-id", firstChat.getChatId().toString())
						.param("chat-name", "thisWillBeIgnored")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(pageOfSizeThree)))
				.andExpect(status().isOk())
				.andDo(result -> actualPage[0] =
						objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<RestResponsePage<ChatUser>>(){}));
		assertEquals(new HashSet<>(page3), new HashSet<>(actualPage[0].toList()));

		mockMvc.perform(get("/chat/users")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(firstPageOfSizeTwo)))
				.andExpect(status().isBadRequest())
				.andExpect(content().string("please specify \"chat-id\" or \"chat-name\""));

		mockMvc.perform(get("/chat/users")
						.param("chat-id",secondChat.getChatId().toString()))
				.andExpect(status().isBadRequest());

		mockMvc.perform(get("/chat/users")
						.param("chat-id","999")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(firstPageOfSizeTwo)))
				.andExpect(status().isOk())
				.andDo(result -> actualPage[0] =
						objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<RestResponsePage<ChatUser>>(){}));
		assertEquals(new HashSet<>(actualPage[0].toList()), new HashSet<>());

		mockMvc.perform(get("/chat/users")
						.param("chat-name","chatName182")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(firstPageOfSizeTwo)))
				.andExpect(status().isOk())
				.andDo(result -> actualPage[0] =
						objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<RestResponsePage<ChatUser>>(){}));
		assertEquals(new HashSet<>(actualPage[0].toList()), new HashSet<>());
	}

	@Test
	@Order(50)
	void testModifyChatUser() throws Exception {

		ChatUserDTO validDto = ChatUserDTO.builder()
				.chatId(chatUser11.getChatId())
				.userId(chatUser11.getUserId())
				.title("SuperAdmin")
				.chatUserRole(chatUser11.getChatUserRole())
				.build();
		ChatUserDTO validDto2 = ChatUserDTO.builder()
				.chatId(chatUser12.getChatId())
				.userId(chatUser12.getUserId())
				.title("Admin's proxy")
				.chatUserRole(ChatUserRole.ADMIN)
				.build();
		ChatUserDTO invalidIdDto = ChatUserDTO.builder()
				.chatId(chatUser11.getChatId())
				.title(chatUser11.getTitle())
				.chatUserRole(chatUser11.getChatUserRole())
				.build();
		ChatUserDTO chatUserDoesNotExistsDto = ChatUserDTO.builder()
				.chatId(firstChat.getChatId())
				.userId(thirdUser.getMessengerUserId())
				.chatUserRole(ChatUserRole.USER)
				.build();
		ChatUserDTO chatUserDoesNotExistsDto2 = ChatUserDTO.builder()
				.chatId(778L)
				.userId(10L)
				.title("d")
				.chatUserRole(ChatUserRole.USER)
				.build();

		ChatUser[] modified = new ChatUser[1];

		mockMvc.perform(put("/chat/users")
						.param("chat-id",validDto.getChatId().toString())
						.param("user-id",validDto.getUserId().toString())
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(validDto)))
				.andExpect(status().isOk())
				.andDo(result -> modified[0] = objectMapper.readValue(
						result.getResponse().getContentAsString(),ChatUser.class));
		assertNotNull(modified[0]);
		assertNotEquals(chatUser11,modified[0]);
		chatUser11 = modified[0];

		mockMvc.perform(put("/chat/users")
						.param("chat-id",validDto2.getChatId().toString())
						.param("user-id",validDto2.getUserId().toString())
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(validDto2)))
				.andExpect(status().isOk())
				.andDo(result -> modified[0] = objectMapper.readValue(
						result.getResponse().getContentAsString(),ChatUser.class));
		assertNotNull(modified[0]);
		assertNotEquals(chatUser12,modified[0]);
		chatUser12 = modified[0];

		mockMvc.perform(put("/chat/users")
						.param("chat-id",invalidIdDto.getChatId().toString())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(invalidIdDto)))
				.andExpect(status().isBadRequest());

		mockMvc.perform(put("/chat/users")
						.param("chat-id",chatUserDoesNotExistsDto.getChatId().toString())
						.param("user-id",chatUserDoesNotExistsDto.getUserId().toString())
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(chatUserDoesNotExistsDto)))
				.andExpect(status().isBadRequest());

		mockMvc.perform(put("/chat/users")
						.param("chat-id",chatUserDoesNotExistsDto2.getChatId().toString())
						.param("user-id",chatUserDoesNotExistsDto2.getUserId().toString())
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(chatUserDoesNotExistsDto2)))
				.andExpect(status().isBadRequest());
	}

	@Test
	@Order(60)
	void testGetChatInfo() throws Exception {

		mockMvc.perform(get("/chat")
				.param("chat-id",firstChat.getChatId().toString()))
				.andExpect(status().isOk())
				.andExpect(content().json(objectMapper.writeValueAsString(firstChat)));

		mockMvc.perform(get("/chat")
						.param("chat-id",firstChat.getChatId().toString())
						.param("chat-name","thisWillBeIgnored"))
				.andExpect(status().isOk())
				.andExpect(content().json(objectMapper.writeValueAsString(firstChat)));

		mockMvc.perform(get("/chat")
						.param("chat-id","100")
						.param("chat-name","thisWillBeIgnored"))
				.andExpect(status().isBadRequest());

		mockMvc.perform(get("/chat")
						.param("chat-name",secondChat.getChatName()))
				.andExpect(status().isOk())
				.andExpect(content().json(objectMapper.writeValueAsString(secondChat)));

		mockMvc.perform(get("/chat"))
				.andExpect(status().isBadRequest())
				.andExpect(content().string("please specify \"username\" or \"user-id\""));

	}

	@Test
	@Order(70)
	void testEditChatInfo() throws Exception {
		ChatDTO validDto1 = ChatDTO.builder()
				.chatId(firstChat.getChatId())
				.chatName(firstChat.getChatName())
				.displayName("New Display name")
				.build();

		ChatDTO existingChatNameDto = ChatDTO.builder()
				.chatId(firstChat.getChatId())
				.chatName(secondChat.getChatName())
				.displayName("dName")
				.build();

		ChatDTO validDto2 = ChatDTO.builder()
				.chatId(secondChat.getChatId())
				.chatName("chat2upd")
				.displayName("New Display name")
				.build();

		ChatDTO invalidIdChatDto1 = ChatDTO.builder()
				.chatId(null)
				.chatName(firstChat.getChatName())
				.displayName("ddddd")
				.build();

		ChatDTO invalidIdChatDto2 = ChatDTO.builder()
				.chatId(100L)
				.chatName("uniqueName")
				.displayName("ddddd")
				.build();

		Chat[] modified = new Chat[1];

		mockMvc.perform(put("/chat")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(validDto1)))
				.andExpect(status().isOk())
				.andDo(result -> modified[0] = objectMapper.readValue(
						result.getResponse().getContentAsString(),Chat.class));
		assertNotNull(modified[0]);
		assertNotEquals(firstChat,modified[0]);
		firstChat = modified[0];

		mockMvc.perform(put("/chat")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(existingChatNameDto)))
				.andExpect(status().isBadRequest());

		mockMvc.perform(put("/chat")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(validDto2)))
				.andExpect(status().isOk())
				.andDo(result -> modified[0] = objectMapper.readValue(
						result.getResponse().getContentAsString(),Chat.class));
		assertNotNull(modified[0]);
		assertNotEquals(secondChat,modified[0]);
		secondChat = modified[0];

		mockMvc.perform(put("/chat")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(invalidIdChatDto1)))
				.andExpect(status().isBadRequest());

		mockMvc.perform(put("/chat")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(invalidIdChatDto2)))
				.andExpect(status().isBadRequest());

	}

	@Test
	@Order(80)
	void testDeleteChatUser() throws Exception {
		ChatUserDTO validDto1 = ChatUserDTO.builder()
				.chatId(chatUser11.getChatId())
				.userId(chatUser11.getUserId())
				.build();
		ChatUserDTO invalidIdDto1 = ChatUserDTO.builder()
				.chatId(chatUser11.getChatId())
				.build();
		ChatUserDTO invalidIdDto3 = ChatUserDTO.builder()
				.chatId(30L)
				.userId(349L)
				.build();
		ChatUserDTO userThatDoesNotExistDto = validDto1;

		mockMvc.perform(delete("/chat/users")
						.param("chat-id",validDto1.getChatId().toString())
						.param("user-id",validDto1.getUserId().toString()))
				.andExpect(status().isOk())
				.andExpect(content().string("chat user deleted successfully"));

		mockMvc.perform(delete("/chat/users")
						.param("chat-id",invalidIdDto1.getChatId().toString()))
				.andExpect(status().isBadRequest());

		mockMvc.perform(delete("/chat/users")
						.param("chat-id",invalidIdDto3.getChatId().toString())
						.param("user-id",invalidIdDto3.getUserId().toString()))
				.andExpect(status().isBadRequest());

		mockMvc.perform(delete("/chat/users")
						.param("chat-id",userThatDoesNotExistDto.getChatId().toString())
						.param("user-id",userThatDoesNotExistDto.getUserId().toString()))
				.andExpect(status().isBadRequest());

	}

}
