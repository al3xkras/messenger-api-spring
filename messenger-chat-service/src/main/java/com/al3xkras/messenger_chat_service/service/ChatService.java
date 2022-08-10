package com.al3xkras.messenger_chat_service.service;

import com.al3xkras.messenger_chat_service.entity.Chat;
import com.al3xkras.messenger_chat_service.entity.ChatUser;
import com.al3xkras.messenger_chat_service.entity.MessengerUser;
import com.al3xkras.messenger_chat_service.exception.ChatNameAlreadyExistsException;
import com.al3xkras.messenger_chat_service.exception.ChatNotFoundException;
import com.al3xkras.messenger_chat_service.exception.ChatUserNotFoundException;
import com.al3xkras.messenger_chat_service.exception.InvalidMessengerUserException;
import com.al3xkras.messenger_chat_service.model.ChatUserId;
import com.al3xkras.messenger_chat_service.model.ChatUserRole;
import com.al3xkras.messenger_chat_service.repository.ChatRepository;
import com.al3xkras.messenger_chat_service.repository.ChatUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.TransientPropertyValueException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Slf4j
@Service
public class ChatService {

    public static final String DEFAULT_TITLE_ADMIN = "Admin";

    private final ChatRepository chatRepository;
    private final ChatUserRepository chatUserRepository;


    @Autowired
    public ChatService(ChatRepository chatRepository, ChatUserRepository chatUserRepository) {
        this.chatRepository = chatRepository;
        this.chatUserRepository = chatUserRepository;
    }

    public Page<Chat> findAllByMessengerUserId(Long messengerUserId, Pageable pageable) {
        return chatRepository.findAllByUserId(messengerUserId, pageable);
    }

    public Page<Chat> findAllByMessengerUserUsername(String username, Pageable pageable) {
        return chatRepository.findAllByUsername(username,pageable);
    }

    @Transactional
    public Chat saveChat(Chat chat, MessengerUser creator) throws ChatNameAlreadyExistsException,InvalidMessengerUserException{
        if (creator.getMessengerUserId()==null)
            throw new InvalidMessengerUserException("chat creator has null ID");
        ChatUser chatOwner = ChatUser.builder()
                .chat(chat)
                .messengerUser(creator)
                .title(DEFAULT_TITLE_ADMIN)
                .chatUserRole(ChatUserRole.ADMIN)
                .build();
        try {
            Chat saved = chatRepository.save(chat);
            chatUserRepository.saveAndFlush(chatOwner);
            return saved;
        } catch (DataIntegrityViolationException e){
            throw new ChatNameAlreadyExistsException(chat.getChatName());
        } catch (InvalidDataAccessApiUsageException e){
            if (e.getCause().getCause() instanceof TransientPropertyValueException){
                throw new InvalidMessengerUserException("messenger user with id "+creator.getMessengerUserId()+" not found");
            } else {
                throw e;
            }
        }
    }

    public Chat findChatById(Long chatId) {
        return chatRepository.findById(chatId)
                .orElseThrow(ChatNotFoundException::new);
    }

    public Chat findChatByChatName(String chatName) {
        return chatRepository.findByChatName(chatName)
                .orElseThrow(ChatNotFoundException::new);
    }

    @Transactional
    public Chat updateChat(Chat chat) throws ChatNotFoundException, ChatNameAlreadyExistsException {
        Chat beforeUpdate = chatRepository.findById(chat.getChatId())
                .orElseThrow(ChatNotFoundException::new);
        Chat updated = Chat.builder()
                .chatId(chat.getChatId())
                .chatName(chat.getChatName()==null?beforeUpdate.getChatName():chat.getChatName())
                .chatDisplayName(chat.getChatDisplayName()==null?beforeUpdate.getChatDisplayName():chat.getChatDisplayName())
                .build();
        try {
            return chatRepository.saveAndFlush(updated);
        } catch (DataIntegrityViolationException e){
            throw new ChatNameAlreadyExistsException(chat.getChatName());
        }
    }

    @Transactional
    public ChatUser addChatUser(ChatUser chatUser) throws ChatUserAlreadyExistsException{
        ChatUser found = chatUserRepository.findById(new ChatUserId(chatUser.getChatId(),chatUser.getUserId()))
                .orElse(null);
        if (found!=null) {
            log.error(found.toString());
            throw new ChatUserAlreadyExistsException();
        }
        return chatUserRepository.saveAndFlush(chatUser);
    }

    @Transactional
    public ChatUser updateChatUser(ChatUser chatUser) throws ChatUserNotFoundException {
        ChatUser beforeUpdate = chatUserRepository.findById(new ChatUserId(chatUser.getChatId(),chatUser.getUserId()))
                .orElseThrow(ChatUserNotFoundException::new);
        ChatUser updated = ChatUser.builder()
                .chatId(chatUser.getChatId())
                .userId(chatUser.getUserId())
                .title(chatUser.getTitle()==null?beforeUpdate.getTitle():chatUser.getTitle())
                .chatUserRole(chatUser.getChatUserRole()==null?beforeUpdate.getChatUserRole():chatUser.getChatUserRole())
                .build();
        return chatUserRepository.save(updated);
    }

    public void deleteChatUser(ChatUser chatUser) throws ChatUserNotFoundException{
        ChatUserId id = new ChatUserId(chatUser.getChatId(),chatUser.getUserId());
        try {
            chatUserRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e){
            throw new ChatUserNotFoundException();
        }
    }

    public Page<ChatUser> findAllChatUsersByChatId(Long chatId, Pageable pageable) {
        return chatUserRepository.findAllByChatId(chatId,pageable);
    }

    public Page<ChatUser> findAllChatUsersByChatName(String chatName, Pageable pageable) {
        return chatUserRepository.findAllByChatName(chatName,pageable);
    }
}
