package com.al3xkras.messengermessageservice.service;

import com.al3xkras.messengermessageservice.entity.ChatMessage;
import com.al3xkras.messengermessageservice.exception.ChatMessageNotFoundException;
import com.al3xkras.messengermessageservice.repository.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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

    public ChatMessage updateMessage(ChatMessage chatMessage) throws ChatMessageNotFoundException {
        return chatMessageRepository.update(chatMessage);
    }

    public void deleteMessage(ChatMessage chatMessage) {
        chatMessageRepository.delete(chatMessage);
    }
}
