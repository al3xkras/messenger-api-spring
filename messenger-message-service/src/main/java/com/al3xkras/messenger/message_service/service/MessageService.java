package com.al3xkras.messenger.message_service.service;

import com.al3xkras.messenger.entity.ChatMessage;
import com.al3xkras.messenger.entity.ChatUser;
import com.al3xkras.messenger.message_service.exception.ChatMessageAlreadyExistsException;
import com.al3xkras.messenger.message_service.exception.ChatMessageNotFoundException;
import com.al3xkras.messenger.model.ChatMessageId;
import com.al3xkras.messenger.message_service.repository.ChatMessageRepository;
import com.al3xkras.messenger.model.ChatUserId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Service
public class MessageService {
    @Autowired
    private ChatMessageRepository chatMessageRepository;
    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public ChatMessage saveMessage(ChatMessage chatMessage) throws ChatMessageAlreadyExistsException {
        ChatMessageId id = new ChatMessageId(chatMessage.getChatId(),chatMessage.getUserId(),chatMessage.getSubmissionDate());
        if (chatMessageRepository.findById(id).isPresent())
            throw new ChatMessageAlreadyExistsException();
        return chatMessageRepository.saveAndFlush(chatMessage);
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
        return chatMessageRepository.saveAndFlush(updated);
    }

    public void deleteMessage(ChatMessageId id)
            throws ChatMessageNotFoundException{
        try {
            chatMessageRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e){
            throw new ChatMessageNotFoundException();
        }
    }

    public ChatMessage findById(ChatMessageId chatMessageId)
            throws ChatMessageNotFoundException {
        return chatMessageRepository.findById(chatMessageId)
                .orElseThrow(ChatMessageNotFoundException::new);
    }
}
