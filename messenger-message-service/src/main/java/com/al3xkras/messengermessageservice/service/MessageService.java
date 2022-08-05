package com.al3xkras.messengermessageservice.service;

import com.al3xkras.messengermessageservice.entity.ChatMessage;
import com.al3xkras.messengermessageservice.exception.ChatMessageNotFoundException;
import com.al3xkras.messengermessageservice.model.ChatMessageId;
import com.al3xkras.messengermessageservice.repository.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MessageService {
    @Autowired
    private ChatMessageRepository chatMessageRepository;

    public ChatMessage saveMessage(ChatMessage chatMessage) {
        return chatMessageRepository.save(chatMessage);
    }

    public Page<ChatMessage> findAllMessagesByChatId(Long chatId, Pageable pageable) {
        return chatMessageRepository.findAllByChatId(chatId,pageable);
    }

    public Page<ChatMessage> findAllMessagesByChatName(String chatName, Pageable pageable) {
        return chatMessageRepository.findAllByChatName(chatName,pageable);
    }

    @Transactional
    public ChatMessage updateMessage(ChatMessage chatMessage) throws ChatMessageNotFoundException {
        ChatMessage beforeUpdate = chatMessageRepository.findById(new ChatMessageId(chatMessage.getChatId(),chatMessage.getUserId(), chatMessage.getSubmissionDate()))
                .orElseThrow(ChatMessageNotFoundException::new);
        ChatMessage updated = ChatMessage.builder()
                .chatId(chatMessage.getChatId())
                .userId(chatMessage.getUserId())
                .submissionDate(chatMessage.getSubmissionDate())
                .message(chatMessage.getMessage()==null?beforeUpdate.getMessage():chatMessage.getMessage())
                .build();
        return chatMessageRepository.save(updated);
    }

    public void deleteMessage(ChatMessage chatMessage) {
        chatMessageRepository.delete(chatMessage);
    }
}
