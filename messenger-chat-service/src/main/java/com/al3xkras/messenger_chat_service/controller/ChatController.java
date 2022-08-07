package com.al3xkras.messenger_chat_service.controller;

import com.al3xkras.messenger_chat_service.dto.ChatDTO;
import com.al3xkras.messenger_chat_service.dto.ChatUserDTO;
import com.al3xkras.messenger_chat_service.dto.PageRequestDto;
import com.al3xkras.messenger_chat_service.entity.Chat;
import com.al3xkras.messenger_chat_service.entity.ChatUser;
import com.al3xkras.messenger_chat_service.exception.ChatUserNotFoundException;
import com.al3xkras.messenger_chat_service.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

@RestController
public class ChatController {

    @Autowired
    private ChatService chatService;

    @GetMapping("/chats")
    public Page<Chat> getChatsByUserId(@RequestParam(value = "user-id", required = false) Long messengerUserId,
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

    @PostMapping("chat")
    public Chat createChat(@RequestBody @Valid ChatDTO chatDTO){
        Chat chat = Chat.builder()
                .chatName(chatDTO.getChatName())
                .chatDisplayName(chatDTO.getDisplayName())
                .build();

        return chatService.createNewChat(chat, null);
    }

    @GetMapping("chat")
    public Chat getChatInfo(@RequestParam(value = "chat-id", required = false) Long chatId,
                            @RequestParam(value = "chat-name",required = false) String chatName){
        if (chatId!=null){
            return chatService.findChatById(chatId);
        } else if (chatName!=null){
            return chatService.findChatByChatName(chatName);
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"please specify \"username\" or \"user-id\"");
    }

    @PutMapping("chat")
    public Chat editChatInfo(@RequestBody ChatDTO chatDTO){
        if (chatDTO.getChatId()==null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"please specify chat id of the chat to be modified");

        Chat chat = Chat.builder()
                .chatId(chatDTO.getChatId())
                .chatName(chatDTO.getChatName())
                .chatDisplayName(chatDTO.getDisplayName())
                .build();
        return chatService.updateChat(chat);
    }

    @PostMapping("chat/users")
    public ChatUser addChatUser(@RequestBody @Valid ChatUserDTO chatUserDTO){
        ChatUser chatUser = ChatUser.builder()
                .chatId(chatUserDTO.getChatId())
                .userId(chatUserDTO.getUserId())
                .title(chatUserDTO.getTitle())
                .chatUserRole(chatUserDTO.getChatUserRole())
                .build();
        return chatService.addChatUser(chatUser);
    }

    @PutMapping("chat/users")
    public ChatUser modifyChatUser(@RequestBody ChatUserDTO chatUserDTO) throws ChatUserNotFoundException {
        if (chatUserDTO.getChatId()==null || chatUserDTO.getUserId()==null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"invalid chat DTO");

        ChatUser chatUser = ChatUser.builder()
                .chatId(chatUserDTO.getChatId())
                .userId(chatUserDTO.getUserId())
                .title(chatUserDTO.getTitle())
                .chatUserRole(chatUserDTO.getChatUserRole())
                .build();
        return chatService.updateChatUser(chatUser);
    }

    @DeleteMapping("chat/users")
    public void deleteChatUser(@RequestBody ChatUserDTO chatUserDTO){
        if (chatUserDTO.getChatId()==null || chatUserDTO.getUserId()==null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"invalid chat DTO");

        ChatUser chatUser = ChatUser.builder()
                .chatId(chatUserDTO.getChatId())
                .userId(chatUserDTO.getUserId())
                .build();
        chatService.deleteChatUser(chatUser);
    }

    @GetMapping
    public Page<ChatUser> getAllChatUsersByChat(@RequestParam(value = "chat-id",required = false) Long chatId,
                                                @RequestParam(value = "chat-name",required = false) String chatName,
                                                @RequestBody PageRequestDto pageRequestDto){

        Pageable pageable = PageRequest.of(pageRequestDto.getPage(),pageRequestDto.getSize());
        if (chatId!=null){
            return chatService.findAllChatUsersByChatId(chatId,pageable);
        } else if (chatName!=null){
            return chatService.findAllChatUsersByChatName(chatName,pageable);
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"please specify \"username\" or \"user-id\"");
    }

}
