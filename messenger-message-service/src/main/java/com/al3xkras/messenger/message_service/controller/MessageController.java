package com.al3xkras.messenger.message_service.controller;

import com.al3xkras.messenger.message_service.exception.ChatMessageAlreadyExistsException;
import com.al3xkras.messenger.message_service.service.MessageService;
import com.al3xkras.messenger.dto.MessageDTO;
import com.al3xkras.messenger.dto.PageRequestDto;
import com.al3xkras.messenger.entity.ChatMessage;
import com.al3xkras.messenger.message_service.exception.ChatMessageNotFoundException;
import com.al3xkras.messenger.model.ChatMessageId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.net.URI;

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
        ChatMessage chatMessage = ChatMessage.builder()
                .chatId(messageDTO.getChatId())
                .userId(messageDTO.getUserId())
                .submissionDate(messageDTO.getSubmissionDate())
                .message(messageDTO.getMessage())
                .build();
        return messageService.saveMessage(chatMessage);
    }

    @GetMapping("/message")
    public ChatMessage findById(@RequestBody ChatMessageId chatMessageId)
            throws ChatMessageNotFoundException {
        return messageService.findById(chatMessageId);
    }

    @GetMapping("/messages")
    public Page<ChatMessage> findAllMessages(@RequestParam(value = "chat-id",required = false) Long chatId,
                                             @RequestParam(value = "chat-name",required = false) String chatName,
                                             @RequestBody PageRequestDto pageRequestDto){
        Pageable pageable = PageRequest.of(pageRequestDto.getPage(),pageRequestDto.getSize());
        if (chatId!=null){
            return messageService.findAllMessagesByChatId(chatId,pageable);
        } else if (chatName!=null){
            return messageService.findAllMessagesByChatName(chatName,pageable);
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"please specify \"chat-id\" or \"chat-name\"");
    }

    @PutMapping("/message")
    public ChatMessage editMessage(@RequestBody @Valid MessageDTO messageDTO)
            throws ChatMessageNotFoundException {
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
    public void deleteMessage(@RequestBody ChatMessageId id)
            throws ChatMessageNotFoundException {
        messageService.deleteMessage(id);
    }

}
