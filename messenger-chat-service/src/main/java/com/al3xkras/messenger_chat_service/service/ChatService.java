package com.al3xkras.messenger_chat_service.service;

import com.al3xkras.messenger_chat_service.entity.Chat;
import com.al3xkras.messenger_chat_service.entity.ChatUser;
import com.al3xkras.messenger_chat_service.entity.MessengerUser;
import com.al3xkras.messenger_chat_service.exception.ChatNameAlreadyExistsException;
import com.al3xkras.messenger_chat_service.exception.ChatNotFoundException;
import com.al3xkras.messenger_chat_service.exception.ChatUserNotFoundException;
import com.al3xkras.messenger_chat_service.repository.ChatRepository;
import com.al3xkras.messenger_chat_service.repository.ChatUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ChatService {

    @Autowired
    private ChatRepository chatRepository;
    @Autowired
    private ChatUserRepository chatUserRepository;
    @Autowired
    private RestTemplate restTemplate;

    public Page<Chat> findAllByMessengerUserId(Long messengerUserId, Pageable pageable) {
        return chatRepository.findAllByUserId(messengerUserId, pageable);
    }

    public Page<Chat> findAllByMessengerUserUsername(String username, Pageable pageable) {
        return chatRepository.findAllByUsername(username,pageable);
    }

    public Chat createNewChat(Chat chat, MessengerUser creator) {
        //TODO add first user to the chat
        return chatRepository.save(chat);
    }

    public Chat findChatById(Long chatId) {
        return chatRepository.findById(chatId)
                .orElseThrow(ChatNotFoundException::new);
    }

    public Chat findChatByChatName(String chatName) {
        return chatRepository.findByChatName(chatName)
                .orElseThrow(ChatNotFoundException::new);
    }

    public Chat updateChat(Chat chat) throws ChatNameAlreadyExistsException,ChatNotFoundException {
        //TODO throw exceptions if not found
        return chatRepository.update(chat);
    }

    public ChatUser addChatUser(ChatUser chatUser) {
        return chatUserRepository.save(chatUser);
    }

    public ChatUser updateChatUser(ChatUser chatUser) throws ChatUserNotFoundException {
        return chatUserRepository.update(chatUser);
    }

    public void deleteChatUser(ChatUser chatUser) throws ChatUserNotFoundException{
        chatUserRepository.delete(chatUser);
    }
}
