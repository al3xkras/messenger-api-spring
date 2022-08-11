package com.al3xkras.messengermessageservice.controller;

import com.al3xkras.messengermessageservice.dto.MessageDTO;
import com.al3xkras.messengermessageservice.dto.PageRequestDto;
import com.al3xkras.messengermessageservice.entity.ChatMessage;
import com.al3xkras.messengermessageservice.exception.ChatMessageNotFoundException;
import com.al3xkras.messengermessageservice.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

@RestController
public class MessageController {

    @Autowired
    private MessageService messageService;

    @PostMapping("/message")
    public ChatMessage sendMessage(@RequestBody @Valid MessageDTO messageDTO){
        ChatMessage chatMessage = ChatMessage.builder()
                .chatId(messageDTO.getChatId())
                .userId(messageDTO.getUserId())
                .submissionDate(messageDTO.getSubmissionDate())
                .message(messageDTO.getMessage())
                .build();
        return messageService.saveMessage(chatMessage);
    }

    @GetMapping("/messages")
    public Page<ChatMessage> findAllMessages(@RequestParam(value = "chat_id",required = false) Long chatId,
                                             @RequestParam(value = "chat_name",required = false) String chatName,
                                             @RequestBody PageRequestDto pageRequestDto){
        Pageable pageable = PageRequest.of(pageRequestDto.getPage(),pageRequestDto.getSize());
        if (chatId!=null){
            return messageService.findAllMessagesByChatId(chatId,pageable);
        } else if (chatName!=null){
            return messageService.findAllMessagesByChatName(chatName,pageable);
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"please specify \"chat-id\" or \"chat-name\"");
    }

    @PutMapping("/messages")
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

    @DeleteMapping("/messages")
    public void deleteMessage(@RequestBody @Valid MessageDTO messageDTO)
            throws ChatMessageNotFoundException {

        ChatMessage chatMessage = ChatMessage.builder()
                .chatId(messageDTO.getChatId())
                .userId(messageDTO.getUserId())
                .submissionDate(messageDTO.getSubmissionDate())
                .build();

        messageService.deleteMessage(chatMessage);
    }

}
