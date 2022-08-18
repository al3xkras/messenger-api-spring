package com.al3xkras.messenger.chat_service.controller;

import com.al3xkras.messenger.chat_service.exception.*;
import com.al3xkras.messenger.chat_service.model.JwtAccessTokens;
import com.al3xkras.messenger.chat_service.service.ChatService;
import com.al3xkras.messenger.dto.ChatDTO;
import com.al3xkras.messenger.dto.ChatUserDTO;
import com.al3xkras.messenger.dto.PageRequestDto;
import com.al3xkras.messenger.entity.Chat;
import com.al3xkras.messenger.entity.ChatUser;
import com.al3xkras.messenger.entity.MessengerUser;
import com.al3xkras.messenger.model.security.JwtTokenAuth;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.net.URI;

@Slf4j
@RestController
public class ChatController {

    @Autowired
    private ChatService chatService;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private JwtAccessTokens accessTokens;

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<String> handleResponseStatusException(ResponseStatusException e){
        return ResponseEntity.status(e.getStatus()).body(e.getReason());
    }

    @ExceptionHandler(ChatNameAlreadyExistsException.class)
    public ResponseEntity<String> handleChatNameAlreadyExistsException(ChatNameAlreadyExistsException e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(InvalidMessengerUserException.class)
    public ResponseEntity<String> handleChatNameAlreadyExistsException(InvalidMessengerUserException e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler({ChatUserNotFoundException.class, ChatUserAlreadyExistsException.class, ChatNotFoundException.class})
    public ResponseEntity<String> handleChatUserAlreadyExistsException(Exception e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @GetMapping("/chats")
    public Page<Chat> getChatsByUser(@RequestParam(value = "user-id", required = false) Long messengerUserId,
                                     @RequestParam(value = "username", required = false) String username,
                                     @RequestBody PageRequestDto pageRequestDto){
        Pageable pageable = PageRequest.of(pageRequestDto.getPage(),pageRequestDto.getSize());
        if (messengerUserId!=null){
            return chatService.findAllByMessengerUserId(messengerUserId, pageable);
        } else if (username!=null){
           return chatService.findAllByMessengerUserUsername(username, pageable);
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"please specify \"username\" or \"user-id\"");
    }

    private MessengerUser getMessengerUserById(Long id) throws ResponseStatusException{
        String accessToken;
        try {
            accessToken = accessTokens.getUserServiceAccessToken();
        } catch (Exception e){
            log.warn("user service auth failed",e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"unable to access user service");
        }

        URI uri = URI.create("http://localhost:10001/user/"+id);
        RequestEntity<?> requestEntity = RequestEntity.get(uri)
                .header(HttpHeaders.AUTHORIZATION,JwtTokenAuth.PREFIX_WITH_WHITESPACE+accessToken)
                .build();
        MessengerUser user;
        try {
            user = restTemplate.exchange(requestEntity,MessengerUser.class).getBody();
        } catch (HttpClientErrorException e){
            throw new ResponseStatusException(e.getStatusCode(),e.getMessage());
        } catch (ResourceAccessException e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"unable to access user service");
        }
        if (user==null)
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"user service error");
        return user;
    }

    @PostMapping("/chat")
    public Chat createChat(@RequestBody @Valid ChatDTO chatDTO)
            throws ChatNameAlreadyExistsException,InvalidMessengerUserException{
        if (chatDTO.getChatName().equals(JwtTokenAuth.Param.CHAT_NAME.value())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Chat name \""+JwtTokenAuth.Param.CHAT_NAME.value()+"\" is" +
                    " not allowed. Please choose another one.");
        }
        Chat chat = Chat.builder()
                .chatName(chatDTO.getChatName())
                .chatDisplayName(chatDTO.getDisplayName())
                .build();

        MessengerUser creator = getMessengerUserById(chatDTO.getOwnerId());
        return chatService.saveChat(chat, creator);
    }

    @GetMapping("/chat")
    public Chat getChatInfo(@RequestParam(value = "chat-id", required = false) Long chatId,
                            @RequestParam(value = "chat-name",required = false) String chatName) throws ChatNotFoundException {
        if (chatId!=null){
            return chatService.findChatById(chatId);
        } else if (chatName!=null){
            return chatService.findChatByChatName(chatName);
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"please specify \"username\" or \"user-id\"");
    }

    @PutMapping("/chat")
    public Chat editChatInfo(@RequestBody ChatDTO chatDTO){
        chatDTO.setOwnerId(0L); //owner id is IGNORED, but the field is required for validation
        @Valid
        ChatDTO validChatDto = chatDTO;

        if (validChatDto.getChatId()==null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"chat ID is null");

        Chat chat = Chat.builder()
                .chatId(validChatDto.getChatId())
                .chatName(validChatDto.getChatName())
                .chatDisplayName(validChatDto.getDisplayName())
                .build();
        return chatService.updateChat(chat);

    }

    @Transactional
    @PostMapping("/chat/users")
    public ChatUser addChatUser(@RequestBody @Valid ChatUserDTO chatUserDTO)
            throws ChatUserAlreadyExistsException {
        MessengerUser user = getMessengerUserById(chatUserDTO.getUserId());
        Chat chat = chatService.findChatById(chatUserDTO.getChatId());
        ChatUser chatUser = ChatUser.builder()
                .chat(chat)
                .messengerUser(user)
                .title(chatUserDTO.getTitle())
                .chatUserRole(chatUserDTO.getChatUserRole())
                .build();
        return chatService.addChatUser(chatUser);
    }

    @PutMapping("/chat/users")
    public ChatUser modifyChatUser(@RequestParam("chat-id")Long chatId,
                                   @RequestParam("user-id")Long userId,
                                   @RequestBody ChatUserDTO chatUserDTO)
            throws ChatUserNotFoundException {
        ChatUser chatUser = ChatUser.builder()
                .chatId(chatId)
                .userId(userId)
                .title(chatUserDTO.getTitle())
                .chatUserRole(chatUserDTO.getChatUserRole())
                .build();
        return chatService.updateChatUser(chatUser);
    }

    @DeleteMapping("/chat/users")
    public ResponseEntity<String> deleteChatUser(@RequestParam("chat-id")Long chatId,
                                                 @RequestParam("user-id")Long userId){
        ChatUser chatUser = ChatUser.builder()
                .chatId(chatId)
                .userId(userId)
                .build();
        try {
            chatService.deleteChatUser(chatUser);
            return ResponseEntity.status(HttpStatus.OK).body("chat user deleted successfully");
        } catch (ChatUserNotFoundException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("chat user not found. failed to delete");
        }
    }

    @GetMapping("/chat/users")
    public Page<ChatUser> getAllChatUsersByChat(@RequestParam(value = "chat-id",required = false) Long chatId,
                                                @RequestParam(value = "chat-name",required = false) String chatName,
                                                @RequestBody PageRequestDto pageRequestDto){

        Pageable pageable = PageRequest.of(pageRequestDto.getPage(),pageRequestDto.getSize());
        if (chatId!=null){
            return chatService.findAllChatUsersByChatId(chatId,pageable);
        } else if (chatName!=null){
            return chatService.findAllChatUsersByChatName(chatName,pageable);
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"please specify \"chat-id\" or \"chat-name\"");
    }

}
