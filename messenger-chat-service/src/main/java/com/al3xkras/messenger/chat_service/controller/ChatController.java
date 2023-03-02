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
import com.al3xkras.messenger.model.MessengerUtils;
import com.al3xkras.messenger.model.authorities.ChatUserAuthority;
import com.al3xkras.messenger.model.security.ChatUserAuthenticationToken;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.net.URI;

import static com.al3xkras.messenger.model.MessengerUtils.Messages.*;

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
                                     @RequestParam(value = "page", required = false) Integer page,
                                     @RequestParam(value = "size", required = false) Integer size,
                                     @RequestBody(required = false) PageRequestDto pageRequestDto){
        if (pageRequestDto==null){
            if (page==null || size==null)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            pageRequestDto=new PageRequestDto(page,size);
        }
        Pageable pageable = PageRequest.of(pageRequestDto.getPage(),pageRequestDto.getSize());
        if (messengerUserId!=null){
            return chatService.findAllByMessengerUserId(messengerUserId, pageable);
        } else if (username!=null){
           return chatService.findAllByMessengerUserUsername(username, pageable);
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format(EXCEPTION_REQUIRED_PARAMETERS_ARE_NULL.value(),
                String.join(", ",JwtTokenAuth.Param.USER_ID.value(),JwtTokenAuth.Param.USERNAME.value())));
    }

    private MessengerUser getMessengerUserById(Long id) throws ResponseStatusException{
        String accessToken;
        try {
            accessToken = accessTokens.getUserServiceAccessToken();
        } catch (Exception e){
            log.warn(EXCEPTION_AUTHORIZE.value(),e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,EXCEPTION_USER_SERVICE_UNREACHABLE.value());
        }

        URI uri = URI.create(MessengerUtils.Property.USER_SERVICE_URI.value()+"/user/"+id);
        RequestEntity<?> requestEntity = RequestEntity.get(uri)
                .header(HttpHeaders.AUTHORIZATION,JwtTokenAuth.PREFIX_WITH_WHITESPACE+accessToken)
                .build();
        MessengerUser user;
        try {
            user = restTemplate.exchange(requestEntity,MessengerUser.class).getBody();
        } catch (HttpClientErrorException e){
            throw new ResponseStatusException(e.getStatusCode(),e.getMessage());
        } catch (ResourceAccessException e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,EXCEPTION_USER_SERVICE_UNREACHABLE.value());
        }
        if (user==null)
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,EXCEPTION_USER_SERVICE_INTERNAL_ERROR.value());
        return user;
    }

    @PostMapping("/chat")
    public Chat createChat(@RequestBody @Valid ChatDTO chatDTO)
            throws ChatNameAlreadyExistsException,InvalidMessengerUserException{
        if (chatDTO.getChatName().equals(JwtTokenAuth.Param.CHAT_NAME.value())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format(EXCEPTION_CHAT_NAME_IS_INVALID.value(),JwtTokenAuth.Param.CHAT_NAME.value()));
        }
        ChatUserAuthenticationToken token = (ChatUserAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        if (chatDTO.getOwnerId()==null || !chatDTO.getOwnerId().equals(token.getUserId())){
            log.error(token.getUserId().toString());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, String.format(EXCEPTION_CHAT_OWNER_ID_IS_INVALID.value(),chatDTO.getOwnerId().toString()));

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
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,String.format(EXCEPTION_REQUIRED_PARAMETERS_ARE_NULL.value(),
                String.join(", ",JwtTokenAuth.Param.USER_ID.value(),JwtTokenAuth.Param.USERNAME.value())));
    }

    @PutMapping("/chat")
    public Chat editChatInfo(@RequestBody ChatDTO chatDTO){
        chatDTO.setOwnerId(0L); //owner id is IGNORED, but the field is required for the validation
        @Valid
        ChatDTO validChatDto = chatDTO;

        ChatUserAuthenticationToken token = (ChatUserAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        if ((validChatDto.getChatId()!=null && validChatDto.getChatId()!=token.getChatId())){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,String.format(EXCEPTION_CHAT_MODIFICATION_FORBIDDEN.value(),
                    JwtTokenAuth.Param.CHAT_ID.value()+": "+validChatDto.getChatId()));
        } else if (validChatDto.getChatId()==null && !validChatDto.getChatName().equals(token.getChatName())){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,String.format(EXCEPTION_CHAT_MODIFICATION_FORBIDDEN.value(),
                    JwtTokenAuth.Param.CHAT_NAME.value()+": "+validChatDto.getChatName()));
        }

        if (validChatDto.getChatId()==null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format(EXCEPTION_ARGUMENT_ISNULL.value(),JwtTokenAuth.Param.CHAT_ID.value(),ChatDTO.class.getSimpleName()));

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

        ChatUserAuthenticationToken token = (ChatUserAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        if (token.getChatId()!=chatUserDTO.getChatId()){
            log.warn("forbidden: expected chat ID: "+chatUserDTO.getChatId()+"; actual: "+token.getChatId());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, String.format(
                    EXCEPTION_CHAT_ADD_USER_IS_FORBIDDEN.value(),chatUserDTO.getTitle(),token.getChatName()));
        }
        MessengerUser user = getMessengerUserById(chatUserDTO.getUserId());
        Chat chat = chatService.findChatById(chatUserDTO.getChatId());
        ChatUser chatUser = ChatUser.builder()
                .chat(chat)
                .messengerUser(user)
                .title(chatUserDTO.getTitle())
                .chatUserRole(chatUserDTO.getChatUserRole())
                .build();
        log.info(chatUserDTO.toString());
        return chatService.addChatUser(chatUser);
    }

    @PutMapping("/chat/users")
    public ChatUser modifyChatUser(@RequestParam("chat-id")Long chatId,
                                   @RequestParam("user-id")Long userId,
                                   @RequestBody ChatUserDTO chatUserDTO)
            throws ChatUserNotFoundException {

        ChatUserAuthenticationToken token = (ChatUserAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        if (!token.getAuthorities().contains(ChatUserAuthority.MODIFY_CHAT_USER_TYPE)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,EXCEPTION_CHAT_USER_ROLE_MODIFIED.value());
        }
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

        ChatUserAuthenticationToken token = (ChatUserAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        if (token.getChatId()!=chatId){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, String.format(
                    EXCEPTION_CHAT_ADD_USER_IS_FORBIDDEN.value(),token.getChatName()));
        }
        ChatUser chatUser = ChatUser.builder()
                .chatId(chatId)
                .userId(userId)
                .build();
        try {
            chatService.deleteChatUser(chatUser);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (ChatUserNotFoundException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(EXCEPTION_CHAT_USER_NOT_FOUND.value());
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
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,String.format(EXCEPTION_REQUIRED_PARAMETERS_ARE_NULL.value(),
                String.join(", ",JwtTokenAuth.Param.USER_ID.value(),JwtTokenAuth.Param.USERNAME.value())));
    }

}
