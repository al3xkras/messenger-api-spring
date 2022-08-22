package com.al3xkras.messenger.message_service.controller;

import com.al3xkras.messenger.message_service.exception.ChatMessageAlreadyExistsException;
import com.al3xkras.messenger.message_service.service.MessageService;
import com.al3xkras.messenger.dto.MessageDTO;
import com.al3xkras.messenger.dto.PageRequestDto;
import com.al3xkras.messenger.entity.ChatMessage;
import com.al3xkras.messenger.message_service.exception.ChatMessageNotFoundException;
import com.al3xkras.messenger.model.ChatMessageId;
import com.al3xkras.messenger.model.authorities.ChatUserAuthority;
import com.al3xkras.messenger.model.security.ChatUserAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.net.URI;
import java.util.Collection;

@RestController
public class MessageController {

    @Autowired
    private MessageService messageService;
    @Autowired
    private RestTemplate restTemplate;

    @ExceptionHandler({ChatMessageNotFoundException.class, ChatMessageAlreadyExistsException.class})
    ResponseEntity<String> handleChatMessageNotFoundException(Exception e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(ResponseStatusException.class)
    ResponseEntity<String> handleResponseStatusException(ResponseStatusException e){
        return ResponseEntity.status(e.getStatus()).body(e.getReason());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    ResponseEntity<String> handleDataIntegrityViolationException(DataIntegrityViolationException e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @PostMapping("/message")
    public ChatMessage sendMessage(@RequestBody @Valid MessageDTO messageDTO)
            throws ChatMessageAlreadyExistsException {
        ChatUserAuthenticationToken token = (ChatUserAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        Collection<GrantedAuthority> authorities = token.getAuthorities();

        boolean sendOwnMessage = (token.getUserId().equals(messageDTO.getUserId()));
        boolean sendMessageToOwnChat = (token.getChatId()==messageDTO.getChatId());
        if (!authorities.contains(ChatUserAuthority.SEND_MESSAGES))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"forbidden: no authorities to send messages to this chat.");
        if (!sendOwnMessage || !sendMessageToOwnChat)
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        ChatMessage chatMessage = ChatMessage.builder()
                .chatId(messageDTO.getChatId())
                .userId(messageDTO.getUserId())
                .submissionDate(messageDTO.getSubmissionDate())
                .message(messageDTO.getMessage())
                .build();
        return messageService.saveMessage(chatMessage);
    }

    @GetMapping("/message")
    public ChatMessage findMessageById(@RequestBody @Valid ChatMessageId chatMessageId) throws ChatMessageNotFoundException {
        ChatUserAuthenticationToken token = (ChatUserAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        Collection<GrantedAuthority> authorities = token.getAuthorities();

        boolean readMessageInOwnChat = token.getChatId()==chatMessageId.getChatId();
        if (!readMessageInOwnChat || !authorities.contains(ChatUserAuthority.READ_SELF_CHAT_MESSAGES))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"forbidden: no authorities to read messages from this chat.");
        return messageService.findById(chatMessageId);
    }

    @GetMapping("/messages")
    public Page<ChatMessage> findAllMessagesByChat(@RequestParam(value = "chat-id",required = false) Long chatId,
                                             @RequestParam(value = "chat-name",required = false) String chatName,
                                             @RequestBody PageRequestDto pageRequestDto){
        ChatUserAuthenticationToken token = (ChatUserAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        Collection<GrantedAuthority> authorities = token.getAuthorities();

        boolean findAllMessagesInOwnChat;

        Pageable pageable = PageRequest.of(pageRequestDto.getPage(),pageRequestDto.getSize());
        if (chatId!=null){
            findAllMessagesInOwnChat = chatId==token.getChatId();
        } else if (chatName!=null){
            findAllMessagesInOwnChat = chatName.equals(token.getChatName());
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"please specify \"chat-id\" or \"chat-name\"");
        }

        if (!findAllMessagesInOwnChat || !authorities.contains(ChatUserAuthority.READ_SELF_CHAT_MESSAGES))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"forbidden: no authorities to read messages from this chat.");

        if (chatId!=null)
            return messageService.findAllMessagesByChatId(chatId,pageable);
        return messageService.findAllMessagesByChatName(chatName,pageable);
    }

    @PutMapping("/message")
    public ChatMessage editMessageById(@RequestBody @Valid MessageDTO messageDTO)
            throws ChatMessageNotFoundException {
        ChatUserAuthenticationToken token = (ChatUserAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        Collection<GrantedAuthority> authorities = token.getAuthorities();

        boolean modifyMessageInOwnChat = messageDTO.getChatId().equals(token.getChatId());
        boolean modifyOwnMessage = messageDTO.getUserId().equals(token.getUserId());
        if (!modifyMessageInOwnChat || !((modifyOwnMessage && authorities.contains(ChatUserAuthority.MODIFY_SELF_CHAT_MESSAGES)) ||
                (!modifyOwnMessage && authorities.contains(ChatUserAuthority.MODIFY_ANY_CHAT_MESSAGE)))){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"forbidden: no authorities to modify this message.");
        }
        ChatMessage chatMessage = ChatMessage.builder()
                .chatId(messageDTO.getChatId())
                .userId(messageDTO.getUserId())
                .submissionDate(messageDTO.getSubmissionDate())
                .message(messageDTO.getMessage())
                .build();
        return messageService.updateMessage(chatMessage);
    }

    @DeleteMapping("/message")
    @ResponseStatus(HttpStatus.OK)
    public void deleteMessageById(@RequestBody @Valid ChatMessageId id)
            throws ChatMessageNotFoundException {

        ChatUserAuthenticationToken token = (ChatUserAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        Collection<GrantedAuthority> authorities = token.getAuthorities();

        boolean deleteMessageInOwnChat = id.getChatId().equals(token.getChatId());
        boolean deleteOwnMessage = id.getUserId().equals(token.getUserId());
        if (!deleteMessageInOwnChat || !((deleteOwnMessage && authorities.contains(ChatUserAuthority.DELETE_SELF_CHAT_MESSAGES)) ||
                (!deleteOwnMessage && authorities.contains(ChatUserAuthority.DELETE_ANY_CHAT_MESSAGE)))){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"forbidden: no authorities to delete this message.");
        }
        messageService.deleteMessage(id);
    }

}
