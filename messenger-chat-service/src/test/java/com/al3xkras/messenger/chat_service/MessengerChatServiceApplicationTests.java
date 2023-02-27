package com.al3xkras.messenger.chat_service;

import com.al3xkras.messenger.chat_service.model.JwtAccessTokens;
import com.al3xkras.messenger.chat_service.service.ChatService;
import com.al3xkras.messenger.dto.ChatDTO;
import com.al3xkras.messenger.dto.ChatUserDTO;
import com.al3xkras.messenger.dto.MessengerUserDTO;
import com.al3xkras.messenger.dto.PageRequestDto;
import com.al3xkras.messenger.entity.Chat;
import com.al3xkras.messenger.entity.ChatUser;
import com.al3xkras.messenger.entity.MessengerUser;
import com.al3xkras.messenger.model.ChatUserRole;
import com.al3xkras.messenger.model.MessengerUserType;
import com.al3xkras.messenger.model.MessengerUtils;
import com.al3xkras.messenger.model.RestResponsePage;
import com.al3xkras.messenger.model.security.JwtTokenAuth;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static com.al3xkras.messenger.model.security.JwtTokenAuth.Param.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles({"test","test-disablePasswordEncoder"})
@Disabled
class MessengerChatServiceApplicationTests {

	static {
		System.setProperty("mysql-host", "localhost");
	}

	@Autowired
	private MockMvc mockMvc;

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private RestTemplate restTemplate;
	@Autowired
	private JwtAccessTokens accessTokens;

	private final ObjectMapper objectMapper = new ObjectMapper();

	static MessengerUser firstUser = MessengerUser.FIRST_ADMIN;

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

	static ChatUser chatUser12;
	static ChatUser chatUser21;
	static ChatUser chatUser22;
	static ChatUser chatUser23;

	static void initChatUsers(){
		firstUser.setMessengerUserId(1L);
		chatUser11 = ChatUser.builder()
				.chat(firstChat)
				.messengerUser(firstUser)
				.title(ChatService.DEFAULT_TITLE_ADMIN)
				.chatUserRole(ChatUserRole.ADMIN)
				.build();

		chatUser12 = ChatUser.builder()
				.chat(firstChat)
				.messengerUser(secondUser)
				.chatUserRole(ChatUserRole.USER)
				.build();

		chatUser21 = ChatUser.builder()
				.chat(secondChat)
				.messengerUser(secondUser)
				.title(ChatService.DEFAULT_TITLE_ADMIN)
				.chatUserRole(ChatUserRole.ADMIN)
				.build();

		chatUser22 = ChatUser.builder()
				.chat(secondChat)
				.messengerUser(firstUser)
				.chatUserRole(ChatUserRole.USER)
				.build();

		chatUser23 = ChatUser.builder()
				.chat(secondChat)
				.messengerUser(thirdUser)
				.chatUserRole(ChatUserRole.USER)
				.build();
	}

	static String secondUserTokenOfTypeUser;
	static String firstUserTokenOfTypeAdmin;

	static String noChatSecondUserToken;
	static String noChatFirstUserToken;

	static String firstChatAdminAuthToken;
	static String secondChatAdminAuthToken;
	static String firstChatUserAuthToken;
	static String secondChatSuperAdminAuthToken;

	@Test
	@Transactional
	@Commit
	@Order(0)
	void createFirstAdminIfNotExists(){
		try {
			firstUser.setMessengerUserId(null);
			entityManager.persist(firstUser);
		} catch (DataIntegrityViolationException ignored){}
	}

	@Test
	@Order(1)
	void getAdminToken() throws Exception {
		firstUserTokenOfTypeAdmin = JwtTokenAuth.PREFIX_WITH_WHITESPACE+accessTokens.getUserServiceAccessToken();
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
			response = restTemplate.exchange(RequestEntity
						.get(MessengerUtils.Property.USER_SERVICE_URI.value()+"/user?username="+firstUser.getUsername())
						.header(HttpHeaders.AUTHORIZATION, firstUserTokenOfTypeAdmin).build(),
					MessengerUser.class);
			assertNotNull(response);
			firstUser = response.getBody();
			//No Auth required
			response = restTemplate.exchange(RequestEntity.post(MessengerUtils.Property.USER_SERVICE_URI.value()+"/user")
					.contentType(MediaType.APPLICATION_JSON)
					.body(objectMapper.writeValueAsString(validUserDto2)), MessengerUser.class);
			assertNotNull(response.getBody());
			secondUser = response.getBody();
			//No Auth required
			response = restTemplate.exchange(RequestEntity.post(MessengerUtils.Property.USER_SERVICE_URI.value()+"/user")
					.contentType(MediaType.APPLICATION_JSON)
					.body(objectMapper.writeValueAsString(validUserDto3)), MessengerUser.class);
			assertNotNull(response.getBody());
			thirdUser = response.getBody();

		} catch (ResourceAccessException r){
			log.error("messenger user service is down. please enable it");
		}
	}

	@Test
	@Order(11)
	void getUserToken(){
		String requestUri = UriComponentsBuilder.fromUriString(MessengerUtils.Property.USER_SERVICE_URI.value()+"/user/login")
				.queryParam(USERNAME.value(),secondUser.getUsername())
				.queryParam(PASSWORD.value(),secondUser.getPassword())
				.toUriString();
		RequestEntity<?> requestEntity = RequestEntity.post(requestUri)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.build();
		ResponseEntity<?> responseEntity = restTemplate.exchange(requestEntity,Object.class);
		secondUserTokenOfTypeUser = JwtTokenAuth.PREFIX_WITH_WHITESPACE+responseEntity.getHeaders().getFirst(HEADER_ACCESS_TOKEN.value());
	}

	@Test
	@Order(12)
	void testAuthentication() throws Exception {
		log.info("secondUserToken: "+ secondUserTokenOfTypeUser);
		MockHttpServletResponse response = mockMvc.perform(post("/auth")
						.param(USER_TOKEN.value(), secondUserTokenOfTypeUser))
				.andExpect(status().isOk())
				.andReturn().getResponse();
		noChatSecondUserToken = JwtTokenAuth.PREFIX_WITH_WHITESPACE+response.getHeader(HEADER_ACCESS_TOKEN.value());
		response = mockMvc.perform(post("/auth")
						.param(USER_TOKEN.value(), firstUserTokenOfTypeAdmin))
				.andExpect(status().isOk())
				.andReturn().getResponse();
		noChatFirstUserToken = JwtTokenAuth.PREFIX_WITH_WHITESPACE+response.getHeader(HEADER_ACCESS_TOKEN.value());
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
		ChatDTO invalidChatNameDto2 = ChatDTO.builder()
				.chatName(CHAT_NAME.value())
				.displayName("Chat 3")
				.ownerId(secondUser.getMessengerUserId())
				.build();

		ChatDTO noDisplayNameChatDTO = ChatDTO.builder()
				.chatId(4L)
				.chatName("chat3")
				.ownerId(secondUser.getMessengerUserId())
				.build();

		ChatDTO existingChatNameDto = validChatDto1;

		mockMvc.perform(post("/chat")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(validChatDto1)))
				.andExpect(status().isForbidden());

		mockMvc.perform(post("/chat")
						.header(HttpHeaders.AUTHORIZATION, noChatSecondUserToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(validChatDto1)))
				.andExpect(status().isForbidden());

		mockMvc.perform(post("/chat")
						.header(HttpHeaders.AUTHORIZATION, noChatFirstUserToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(validChatDto1)))
				.andExpect(status().isOk())
				.andExpect(content().json(objectMapper.writeValueAsString(firstChat)));

		mockMvc.perform(post("/chat")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(validChatDto2)))
				.andExpect(status().isForbidden());

		mockMvc.perform(post("/chat")
						.header(HttpHeaders.AUTHORIZATION, noChatFirstUserToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(validChatDto2)))
				.andExpect(status().isForbidden());

		mockMvc.perform(post("/chat")
						.header(HttpHeaders.AUTHORIZATION, noChatSecondUserToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(validChatDto2)))
				.andExpect(status().isOk())
				.andExpect(content().json(objectMapper.writeValueAsString(secondChat)));

		mockMvc.perform(post("/chat")
						.header(HttpHeaders.AUTHORIZATION, noChatSecondUserToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(invalidChatNameDto)))
				.andExpect(status().isBadRequest());

		mockMvc.perform(post("/chat")
						.header(HttpHeaders.AUTHORIZATION, noChatSecondUserToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(invalidChatNameDto2)))
				.andExpect(status().isBadRequest());

		mockMvc.perform(post("/chat")
						.header(HttpHeaders.AUTHORIZATION, noChatSecondUserToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(noDisplayNameChatDTO)))
				.andExpect(status().isBadRequest());

		mockMvc.perform(post("/chat")
						.header(HttpHeaders.AUTHORIZATION, noChatFirstUserToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(existingChatNameDto)))
				.andExpect(status().isBadRequest());

		mockMvc.perform(post("/chat")
						.header(HttpHeaders.AUTHORIZATION, noChatFirstUserToken))
				.andExpect(status().isBadRequest());

		mockMvc.perform(post("/chat"))
				.andExpect(status().isForbidden());

	}

	@Test
	@Order(25)
	void authenticateChatAdmins() throws Exception {
		MockHttpServletResponse response = mockMvc.perform(post("/auth")
						.param(USER_TOKEN.value(), firstUserTokenOfTypeAdmin)
						.param(CHAT_NAME.value(), firstChat.getChatName()))
				.andExpect(status().isOk())
				.andReturn().getResponse();
		firstChatAdminAuthToken = JwtTokenAuth.PREFIX_WITH_WHITESPACE+response.getHeader(HEADER_ACCESS_TOKEN.value());

		response = mockMvc.perform(post("/auth")
						.param(USER_TOKEN.value(), secondUserTokenOfTypeUser)
						.param(CHAT_NAME.value(), secondChat.getChatName()))
				.andExpect(status().isOk())
				.andReturn().getResponse();
		secondChatAdminAuthToken = JwtTokenAuth.PREFIX_WITH_WHITESPACE+response.getHeader(HEADER_ACCESS_TOKEN.value());
	}

	@Test
	@Order(30)
	void testAddChatUser() throws Exception {
		initChatUsers();
		ChatUserDTO validDto2 = ChatUserDTO.builder()
				.chatId(chatUser12.getChatId())
				.userId(chatUser12.getUserId())
				.title(chatUser12.getTitle())
				.chatUserRole(chatUser12.getChatUserRole())
				.build();
		ChatUserDTO validDto4 = ChatUserDTO.builder()
				.chatId(chatUser22.getChat().getChatId())
				.userId(chatUser22.getMessengerUser().getMessengerUserId())
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
		ChatUserDTO existingChatUserDto = ChatUserDTO.builder()
				.chatId(chatUser11.getChatId())
				.userId(chatUser11.getUserId())
				.title(chatUser11.getTitle())
				.chatUserRole(chatUser11.getChatUserRole())
				.build();

		mockMvc.perform(post("/chat/users")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(validDto2)))
				.andExpect(status().isForbidden());

		mockMvc.perform(post("/chat/users")
						.header(HttpHeaders.AUTHORIZATION,secondChatAdminAuthToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(validDto2)))
				.andExpect(status().isForbidden());

		mockMvc.perform(post("/chat/users")
						.header(HttpHeaders.AUTHORIZATION,firstChatAdminAuthToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(validDto2)))
				.andExpect(status().isOk())
				.andExpect(content().json(objectMapper.writeValueAsString(chatUser12)));

		// chatUser22 does not exist
		mockMvc.perform(post("/auth")
						.param(USER_TOKEN.value(), firstUserTokenOfTypeAdmin)
						.param(CHAT_NAME.value(), secondChat.getChatName()))
				.andExpect(status().isForbidden());

		mockMvc.perform(post("/chat/users")
						.header(HttpHeaders.AUTHORIZATION,secondChatAdminAuthToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(validDto4)))
				.andExpect(status().isOk())
				.andExpect(content().json(objectMapper.writeValueAsString(chatUser22)));
		// chatUser22 was added


		MockHttpServletResponse response = mockMvc.perform(post("/auth")
						.param(USER_TOKEN.value(), firstUserTokenOfTypeAdmin)
						.param(CHAT_NAME.value(), secondChat.getChatName()))
				.andExpect(status().isOk())
				.andReturn().getResponse();
		secondChatSuperAdminAuthToken = JwtTokenAuth.PREFIX_WITH_WHITESPACE+response.getHeader(HEADER_ACCESS_TOKEN.value());


		mockMvc.perform(post("/chat/users")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(validDto5)))
				.andExpect(status().isForbidden());

		mockMvc.perform(post("/chat/users")
						.header(HttpHeaders.AUTHORIZATION, noChatFirstUserToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(validDto5)))
				.andExpect(status().isForbidden());

		mockMvc.perform(post("/chat/users")
						.header(HttpHeaders.AUTHORIZATION, secondChatSuperAdminAuthToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(validDto5)))
				.andExpect(status().isOk())
				.andExpect(content().json(objectMapper.writeValueAsString(chatUser23)));

		mockMvc.perform(post("/chat/users")
						.header(HttpHeaders.AUTHORIZATION,secondChatAdminAuthToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(invalidIdChatUserDto1)))
				.andExpect(status().isBadRequest());

		mockMvc.perform(post("/chat/users")
						.header(HttpHeaders.AUTHORIZATION, noChatFirstUserToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(invalidIdChatUserDto1)))
				.andExpect(status().isForbidden());

		mockMvc.perform(post("/chat/users")
						.header(HttpHeaders.AUTHORIZATION,secondChatAdminAuthToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(invalidIdChatUserDto2)))
				.andExpect(status().isBadRequest());

		mockMvc.perform(post("/chat/users")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(invalidIdChatUserDto2)))
				.andExpect(status().isForbidden());

		mockMvc.perform(post("/chat/users")
						.header(HttpHeaders.AUTHORIZATION,firstChatAdminAuthToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(nullUserRoleChatDto)))
				.andExpect(status().isBadRequest());

		mockMvc.perform(post("/chat/users")
						.header(HttpHeaders.AUTHORIZATION,firstChatAdminAuthToken)
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
				.andExpect(status().isForbidden());
		mockMvc.perform(get("/chat/users")
						.header(HttpHeaders.AUTHORIZATION, noChatFirstUserToken)
						.param("chat-id",secondChat.getChatId().toString())
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(firstPageOfSizeTwo)))
				.andExpect(status().isForbidden());
		mockMvc.perform(get("/chat/users")
						.header(HttpHeaders.AUTHORIZATION,firstChatAdminAuthToken)
						.param("chat-id",secondChat.getChatId().toString())
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(firstPageOfSizeTwo)))
				.andExpect(status().isOk());
		mockMvc.perform(get("/chat/users")
						.header(HttpHeaders.AUTHORIZATION, secondChatSuperAdminAuthToken)
						.param("chat-id",secondChat.getChatId().toString())
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(firstPageOfSizeTwo)))
				.andExpect(status().isOk())
				.andDo(result -> actualPage[0] =
						objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<RestResponsePage<ChatUser>>(){}));
		assertEquals(new HashSet<>(page1), new HashSet<>(actualPage[0].toList()));

		mockMvc.perform(get("/chat/users")
						.header(HttpHeaders.AUTHORIZATION,secondChatAdminAuthToken)
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
				.andExpect(status().isForbidden());
		mockMvc.perform(get("/chat/users")
						.header(HttpHeaders.AUTHORIZATION, secondChatSuperAdminAuthToken)
						.param("chat-id", firstChat.getChatId().toString())
						.param("chat-name", "thisWillBeIgnored")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(pageOfSizeThree)))
				.andExpect(status().isOk());

		MockHttpServletResponse response = mockMvc.perform(post("/auth")
						.param(USER_TOKEN.value(), secondUserTokenOfTypeUser)
						.param(CHAT_NAME.value(), firstChat.getChatName()))
				.andExpect(status().isOk())
				.andReturn().getResponse();
		firstChatUserAuthToken = JwtTokenAuth.PREFIX_WITH_WHITESPACE+response.getHeader(HEADER_ACCESS_TOKEN.value());

		mockMvc.perform(get("/chat/users")
						.header(HttpHeaders.AUTHORIZATION,firstChatUserAuthToken)
						.param("chat-id", firstChat.getChatId().toString())
						.param("chat-name", "thisWillBeIgnored")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(pageOfSizeThree)))
				.andExpect(status().isOk())
				.andDo(result -> actualPage[0] =
						objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<RestResponsePage<ChatUser>>(){}));
		assertEquals(new HashSet<>(page3), new HashSet<>(actualPage[0].toList()));

		mockMvc.perform(get("/chat/users")
						.header(HttpHeaders.AUTHORIZATION,firstChatUserAuthToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(firstPageOfSizeTwo)))
				.andExpect(status().isBadRequest());

		mockMvc.perform(get("/chat/users")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(firstPageOfSizeTwo)))
				.andExpect(status().isForbidden());

		mockMvc.perform(get("/chat/users")
						.header(HttpHeaders.AUTHORIZATION, secondChatSuperAdminAuthToken)
						.param("chat-id",secondChat.getChatId().toString()))
				.andExpect(status().isBadRequest());

		mockMvc.perform(get("/chat/users")
						.header(HttpHeaders.AUTHORIZATION,firstChatAdminAuthToken)
						.param("chat-id",secondChat.getChatId().toString()))
				.andExpect(status().isBadRequest());

		mockMvc.perform(get("/chat/users")
						.header(HttpHeaders.AUTHORIZATION,firstChatUserAuthToken)
						.param("chat-id",secondChat.getChatId().toString()))
				.andExpect(status().isForbidden());

		mockMvc.perform(get("/chat/users")
						.param("chat-id","999")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(firstPageOfSizeTwo)))
				.andExpect(status().isForbidden());
		mockMvc.perform(get("/chat/users")
						.header(HttpHeaders.AUTHORIZATION,firstChatAdminAuthToken)
						.param("chat-id","999")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(firstPageOfSizeTwo)))
				.andExpect(status().isOk())
				.andDo(result -> actualPage[0] =
						objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<RestResponsePage<ChatUser>>(){}));
		assertEquals(new HashSet<>(actualPage[0].toList()), new HashSet<>());

		mockMvc.perform(get("/chat/users")
						.header(HttpHeaders.AUTHORIZATION, secondUserTokenOfTypeUser)
						.param("chat-name","chatName182")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(firstPageOfSizeTwo)))
				.andExpect(status().isForbidden());
		mockMvc.perform(get("/chat/users")
						.header(HttpHeaders.AUTHORIZATION,firstChatAdminAuthToken)
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
				.title("NewTitle")
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
				.andExpect(status().isForbidden());

		mockMvc.perform(put("/chat/users")
						.header(HttpHeaders.AUTHORIZATION,firstChatUserAuthToken)
						.param("chat-id",validDto.getChatId().toString())
						.param("user-id",validDto.getUserId().toString())
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(validDto)))
				.andExpect(status().isForbidden());

		mockMvc.perform(put("/chat/users")
						.header(HttpHeaders.AUTHORIZATION,secondChatSuperAdminAuthToken)
						.param("chat-id",validDto.getChatId().toString())
						.param("user-id",validDto.getUserId().toString())
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(validDto)))
				.andExpect(status().isForbidden());

		mockMvc.perform(put("/chat/users")
						.header(HttpHeaders.AUTHORIZATION,firstChatAdminAuthToken)
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
				.andExpect(status().isForbidden());

		mockMvc.perform(put("/chat/users")
						.header(HttpHeaders.AUTHORIZATION,firstChatUserAuthToken)
						.param("chat-id",validDto2.getChatId().toString())
						.param("user-id",validDto2.getUserId().toString())
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(validDto2)))
				.andExpect(status().isForbidden())
				.andExpect(content().string(MessengerUtils.Messages.EXCEPTION_CHAT_USER_ROLE_MODIFIED.value()));

		mockMvc.perform(put("/chat/users")
						.header(HttpHeaders.AUTHORIZATION,firstChatAdminAuthToken)
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
				.andExpect(status().isForbidden());

		mockMvc.perform(put("/chat/users")
						.header(HttpHeaders.AUTHORIZATION,firstChatAdminAuthToken)
						.param("chat-id",invalidIdDto.getChatId().toString())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(invalidIdDto)))
				.andExpect(status().isBadRequest());

		mockMvc.perform(put("/chat/users")
						.header(HttpHeaders.AUTHORIZATION,secondChatSuperAdminAuthToken)
						.param("chat-id",chatUserDoesNotExistsDto.getChatId().toString())
						.param("user-id",chatUserDoesNotExistsDto.getUserId().toString())
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(chatUserDoesNotExistsDto)))
				.andExpect(status().isForbidden());

		mockMvc.perform(put("/chat/users")
						.header(HttpHeaders.AUTHORIZATION,firstChatAdminAuthToken)
						.param("chat-id",chatUserDoesNotExistsDto.getChatId().toString())
						.param("user-id",chatUserDoesNotExistsDto.getUserId().toString())
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(chatUserDoesNotExistsDto)))
				.andExpect(status().isBadRequest());

		mockMvc.perform(put("/chat/users")
						.header(HttpHeaders.AUTHORIZATION,firstChatAdminAuthToken)
						.param("chat-id",chatUserDoesNotExistsDto2.getChatId().toString())
						.param("user-id",chatUserDoesNotExistsDto2.getUserId().toString())
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(chatUserDoesNotExistsDto2)))
				.andExpect(status().isForbidden());

		mockMvc.perform(put("/chat/users")
						.header(HttpHeaders.AUTHORIZATION,secondChatSuperAdminAuthToken)
						.param("chat-id",chatUserDoesNotExistsDto2.getChatId().toString())
						.param("user-id",chatUserDoesNotExistsDto2.getUserId().toString())
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(chatUserDoesNotExistsDto2)))
				.andExpect(status().isForbidden());
	}

	@Test
	@Order(55)
	void getAuthTokensAfterModifyingChatUsers() throws Exception {
		MockHttpServletResponse response = mockMvc.perform(post("/auth")
						.param(USER_TOKEN.value(), firstUserTokenOfTypeAdmin)
						.param(CHAT_NAME.value(), firstChat.getChatName()))
				.andExpect(status().isOk())
				.andReturn().getResponse();
		String firstChatUserAuthTokenNew = JwtTokenAuth.PREFIX_WITH_WHITESPACE+response.getHeader(HEADER_ACCESS_TOKEN.value());
		assertNotEquals(firstChatUserAuthToken,firstChatUserAuthTokenNew);
		firstChatUserAuthToken=firstChatUserAuthTokenNew;
		//firstChatUserAuthToken is of type SUPER_ADMIN after edit

		response = mockMvc.perform(post("/auth")
						.param(USER_TOKEN.value(), secondUserTokenOfTypeUser)
						.param(CHAT_NAME.value(), firstChat.getChatName()))
				.andExpect(status().isOk())
				.andReturn().getResponse();
		String firstChatAdminAuthTokenNew = JwtTokenAuth.PREFIX_WITH_WHITESPACE+response.getHeader(HEADER_ACCESS_TOKEN.value());
		assertNotEquals(firstChatAdminAuthToken,firstChatAdminAuthTokenNew);
		firstChatAdminAuthToken=firstChatAdminAuthTokenNew;
	}

	@Test
	@Order(60)
	void testGetChatInfo() throws Exception {

		mockMvc.perform(get("/chat")
						.param("chat-id",firstChat.getChatId().toString()))
				.andExpect(status().isForbidden());
		mockMvc.perform(get("/chat")
						.header(HttpHeaders.AUTHORIZATION, noChatFirstUserToken)
						.param("chat-id",firstChat.getChatId().toString()))
				.andExpect(status().isForbidden());
		mockMvc.perform(get("/chat")
						.header(HttpHeaders.AUTHORIZATION,firstChatUserAuthToken)
				.param("chat-id",firstChat.getChatId().toString()))
				.andExpect(status().isOk())
				.andExpect(content().json(objectMapper.writeValueAsString(firstChat)));
		mockMvc.perform(get("/chat")
						.header(HttpHeaders.AUTHORIZATION,firstChatAdminAuthToken)
						.param("chat-id",firstChat.getChatId().toString()))
				.andExpect(status().isOk())
				.andExpect(content().json(objectMapper.writeValueAsString(firstChat)));

		mockMvc.perform(get("/chat")
						.header(HttpHeaders.AUTHORIZATION,secondChatAdminAuthToken)
						.param("chat-id",firstChat.getChatId().toString())
						.param("chat-name","thisWillBeIgnored"))
				.andExpect(status().isForbidden());
		mockMvc.perform(get("/chat")
						.header(HttpHeaders.AUTHORIZATION,secondChatSuperAdminAuthToken)
						.param("chat-id",firstChat.getChatId().toString())
						.param("chat-name","thisWillBeIgnored"))
				.andExpect(status().isOk());
		mockMvc.perform(get("/chat")
						.header(HttpHeaders.AUTHORIZATION,firstChatUserAuthToken)
						.param("chat-id",firstChat.getChatId().toString())
						.param("chat-name","thisWillBeIgnored"))
				.andExpect(status().isOk())
				.andExpect(content().json(objectMapper.writeValueAsString(firstChat)));

		mockMvc.perform(get("/chat")
						.param("chat-id","100")
						.param("chat-name","thisWillBeIgnored"))
				.andExpect(status().isForbidden());

		mockMvc.perform(get("/chat")
						.header(HttpHeaders.AUTHORIZATION,secondChatSuperAdminAuthToken)
						.param("chat-id","100")
						.param("chat-name","thisWillBeIgnored"))
				.andExpect(status().isBadRequest());

		mockMvc.perform(get("/chat")
						.header(HttpHeaders.AUTHORIZATION,firstChatUserAuthToken)
						.param("chat-name",secondChat.getChatName()))
				.andExpect(status().isOk()) //firstChatUserAuthToken is of type SUPER_ADMIN after edit
				.andExpect(content().json(objectMapper.writeValueAsString(secondChat)));

		mockMvc.perform(get("/chat")
						.header(HttpHeaders.AUTHORIZATION,noChatFirstUserToken)
						.param("chat-name",secondChat.getChatName()))
				.andExpect(status().isForbidden());

		mockMvc.perform(get("/chat")
						.header(HttpHeaders.AUTHORIZATION,secondChatAdminAuthToken)
						.param("chat-name",secondChat.getChatName()))
				.andExpect(status().isOk())
				.andExpect(content().json(objectMapper.writeValueAsString(secondChat)));

		mockMvc.perform(get("/chat"))
				.andExpect(status().isForbidden());

		mockMvc.perform(get("/chat").header(HttpHeaders.AUTHORIZATION,secondChatAdminAuthToken))
				.andExpect(status().isBadRequest());

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
				.andExpect(status().isForbidden());

		mockMvc.perform(put("/chat")
						.header(HttpHeaders.AUTHORIZATION,noChatFirstUserToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(validDto1)))
				.andExpect(status().isForbidden());


		mockMvc.perform(put("/chat")
						.header(HttpHeaders.AUTHORIZATION,secondChatAdminAuthToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(validDto1)))
				.andExpect(status().isForbidden());


		mockMvc.perform(put("/chat")
						.header(HttpHeaders.AUTHORIZATION,secondChatSuperAdminAuthToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(validDto1)))
				.andExpect(status().isForbidden());

		mockMvc.perform(put("/chat")
						.header(HttpHeaders.AUTHORIZATION,firstChatAdminAuthToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(validDto1)))
				.andExpect(status().isOk())
				.andDo(result -> modified[0] = objectMapper.readValue(
						result.getResponse().getContentAsString(),Chat.class));
		assertNotNull(modified[0]);
		assertNotEquals(firstChat,modified[0]);
		firstChat = modified[0];

		mockMvc.perform(put("/chat")
						.header(HttpHeaders.AUTHORIZATION,firstChatUserAuthToken)//Super Admin
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(existingChatNameDto)))
				.andExpect(status().isBadRequest())
				.andExpect(content().string(secondChat.getChatName()));

		mockMvc.perform(put("/chat")
						.header(HttpHeaders.AUTHORIZATION,secondChatAdminAuthToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(existingChatNameDto)))
				.andExpect(status().isForbidden());

		mockMvc.perform(put("/chat")
						.header(HttpHeaders.AUTHORIZATION,firstChatAdminAuthToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(existingChatNameDto)))
				.andExpect(status().isBadRequest())
				.andExpect(content().string(secondChat.getChatName()));

		mockMvc.perform(put("/chat")
						.header(HttpHeaders.AUTHORIZATION,firstChatAdminAuthToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(validDto2)))
				.andExpect(status().isForbidden());

		mockMvc.perform(put("/chat")
						.header(HttpHeaders.AUTHORIZATION,firstChatUserAuthToken) //Super Admin after edit
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(validDto2)))
				.andExpect(status().isForbidden());

		mockMvc.perform(put("/chat")
						.header(HttpHeaders.AUTHORIZATION,secondChatSuperAdminAuthToken)
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
				.andExpect(status().isForbidden());

		mockMvc.perform(put("/chat")
						.header(HttpHeaders.AUTHORIZATION,firstChatAdminAuthToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(invalidIdChatDto1)))
				.andExpect(status().isBadRequest());

		mockMvc.perform(put("/chat")
						.header(HttpHeaders.AUTHORIZATION,secondChatAdminAuthToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(invalidIdChatDto2)))
				.andExpect(status().isForbidden())
				.andExpect(content().string(String.format(MessengerUtils.Messages.EXCEPTION_CHAT_MODIFICATION_FORBIDDEN.value(),CHAT_ID.value()+": "+invalidIdChatDto2.getChatId())));
	}

	@Test
	@Order(80)
	void testDeleteChatUser() throws Exception {
		ChatUserDTO chatUser11Dto = ChatUserDTO.builder()
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
		ChatUserDTO userThatDoesNotExistDto = chatUser11Dto;

		mockMvc.perform(delete("/chat/users")
						.param("chat-id",chatUser11Dto.getChatId().toString())
						.param("user-id",chatUser11Dto.getUserId().toString()))
				.andExpect(status().isForbidden());
		mockMvc.perform(delete("/chat/users")
						.header(HttpHeaders.AUTHORIZATION,secondChatAdminAuthToken)
						.param("chat-id",chatUser11Dto.getChatId().toString())
						.param("user-id",chatUser11Dto.getUserId().toString()))
				.andExpect(status().isForbidden());
		mockMvc.perform(delete("/chat/users")
						.header(HttpHeaders.AUTHORIZATION,firstChatAdminAuthToken)
						.param("chat-id",chatUser11Dto.getChatId().toString())
						.param("user-id",chatUser11Dto.getUserId().toString()))
				.andExpect(status().isForbidden()); //ADMIN cannot delete self
		mockMvc.perform(delete("/chat/users")
						.header(HttpHeaders.AUTHORIZATION,firstChatUserAuthToken)//SUPER_ADMIN can delete ADMIN
						.param("chat-id",chatUser11Dto.getChatId().toString())
						.param("user-id",chatUser11Dto.getUserId().toString()))
				.andExpect(status().isOk());

		mockMvc.perform(delete("/chat/users")
						.param("chat-id",invalidIdDto1.getChatId().toString()))
				.andExpect(status().isForbidden());

		mockMvc.perform(delete("/chat/users")
						.header(HttpHeaders.AUTHORIZATION,firstChatUserAuthToken)
						.param("chat-id",invalidIdDto1.getChatId().toString()))
				.andExpect(status().isBadRequest());

		mockMvc.perform(delete("/chat/users")
						.param("chat-id",invalidIdDto3.getChatId().toString())
						.param("user-id",invalidIdDto3.getUserId().toString()))
				.andExpect(status().isForbidden());

		mockMvc.perform(delete("/chat/users")
						.header(HttpHeaders.AUTHORIZATION,secondChatSuperAdminAuthToken)
						.param("chat-id",invalidIdDto3.getChatId().toString())
						.param("user-id",invalidIdDto3.getUserId().toString()))
				.andExpect(status().isForbidden());

		mockMvc.perform(delete("/chat/users")
						.header(HttpHeaders.AUTHORIZATION,secondChatSuperAdminAuthToken)
						.param("chat-id",userThatDoesNotExistDto.getChatId().toString())
						.param("user-id",userThatDoesNotExistDto.getUserId().toString()))
				.andExpect(status().isForbidden());

		mockMvc.perform(delete("/chat/users")
						.header(HttpHeaders.AUTHORIZATION,firstChatUserAuthToken)
						.param("chat-id",userThatDoesNotExistDto.getChatId().toString())
						.param("user-id",userThatDoesNotExistDto.getUserId().toString()))
				.andExpect(status().isBadRequest());

	}

}
