package com.al3xkras.messenger_chat_service.controller;

import com.al3xkras.messenger_chat_service.dto.ChatDTO;
import com.al3xkras.messenger_chat_service.dto.ChatUserDTO;
import com.al3xkras.messenger_chat_service.dto.PageRequestDto;
import com.al3xkras.messenger_chat_service.entity.Chat;
import com.al3xkras.messenger_chat_service.entity.ChatUser;
import com.al3xkras.messenger_chat_service.entity.MessengerUser;
import com.al3xkras.messenger_chat_service.exception.ChatNameAlreadyExistsException;
import com.al3xkras.messenger_chat_service.exception.ChatNotFoundException;
import com.al3xkras.messenger_chat_service.exception.ChatUserNotFoundException;
import com.al3xkras.messenger_chat_service.exception.InvalidMessengerUserException;
import com.al3xkras.messenger_chat_service.service.ChatService;
import com.al3xkras.messenger_chat_service.service.ChatUserAlreadyExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.net.URI;

@RestController
public class ChatController {

    @Autowired
    private ChatService chatService;
    @Autowired
    private RestTemplate restTemplate;

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

    @ExceptionHandler({ChatUserNotFoundException.class,ChatUserAlreadyExistsException.class, ChatNotFoundException.class})
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
        URI uri = URI.create("http://localhost:10001/user/"+id);
        MessengerUser user;
        try {
            user = restTemplate.getForObject(uri,MessengerUser.class);
        } catch (HttpClientErrorException e){
            throw new ResponseStatusException(e.getStatusCode(),e.getMessage());
        } catch (ResourceAccessException e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"user service is down");
        }
        if (user==null)
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"user service error");
        return user;
    }

    @PostMapping("/chat")
    public Chat createChat(@RequestBody @Valid ChatDTO chatDTO)
            throws ChatNameAlreadyExistsException,InvalidMessengerUserException{
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
    public ChatUser modifyChatUser(@RequestBody @Valid ChatUserDTO chatUserDTO)
            throws ChatUserNotFoundException {
        ChatUser chatUser = ChatUser.builder()
                .chatId(chatUserDTO.getChatId())
                .userId(chatUserDTO.getUserId())
                .title(chatUserDTO.getTitle())
                .chatUserRole(chatUserDTO.getChatUserRole())
                .build();
        return chatService.updateChatUser(chatUser);
    }

    @DeleteMapping("/chat/users")
    public ResponseEntity<String> deleteChatUser(@RequestBody ChatUserDTO chatUserDTO){
        ChatUser chatUser = ChatUser.builder()
                .chatId(chatUserDTO.getChatId())
                .userId(chatUserDTO.getUserId())
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
