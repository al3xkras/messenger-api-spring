package com.al3xkras.messenger_chat_service.service;

import com.al3xkras.messenger_chat_service.entity.Chat;
import com.al3xkras.messenger_chat_service.entity.ChatUser;
import com.al3xkras.messenger_chat_service.entity.MessengerUser;
import com.al3xkras.messenger_chat_service.exception.ChatNotFoundException;
import com.al3xkras.messenger_chat_service.exception.ChatUserNotFoundException;
import com.al3xkras.messenger_chat_service.model.ChatUserId;
import com.al3xkras.messenger_chat_service.model.ChatUserRole;
import com.al3xkras.messenger_chat_service.repository.ChatRepository;
import com.al3xkras.messenger_chat_service.repository.ChatUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
public class ChatService {

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
    public Chat createNewChat(Chat chat, MessengerUser creator) {
        chat.setChatId(null);
        ChatUser chatOwner = ChatUser.builder()
                .chatId(chat.getChatId())
                .userId(creator.getMessengerUserId())
                .title("Admin")
                .chatUserRole(ChatUserRole.ADMIN)
                .build();
        chatUserRepository.save(chatOwner);
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

    @Transactional
    public Chat updateChat(Chat chat) throws ChatNotFoundException {
        Chat beforeUpdate = chatRepository.findById(chat.getChatId())
                .orElseThrow(ChatNotFoundException::new);
        Chat updated = Chat.builder()
                .chatId(chat.getChatId())
                .chatName(chat.getChatName()==null?beforeUpdate.getChatName():chat.getChatName())
                .chatDisplayName(chat.getChatDisplayName()==null?beforeUpdate.getChatDisplayName():chat.getChatDisplayName())
                .build();
        return chatRepository.save(updated);
    }

    public ChatUser addChatUser(ChatUser chatUser) {
        return chatUserRepository.save(chatUser);
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
        chatUserRepository.delete(chatUser);
    }

    public Page<ChatUser> findAllChatUsersByChatIdFetchMessengerUser(Long chatId, Pageable pageable) {
        return chatUserRepository.findAllByChatIdFetchMessengerUser(chatId,pageable);
    }

    public Page<ChatUser> findAllChatUsersByChatId(Long chatId, Pageable pageable) {
        return chatUserRepository.findAllByChatId(chatId,pageable);
    }

    public Page<ChatUser> findAllChatUsersByChatNameFetchMessengerUser(String chatName, Pageable pageable) {
        return chatUserRepository.findAllByChatNameFetchMessengerUser(chatName,pageable);
    }

    public Page<ChatUser> findAllChatUsersByChatName(String chatName, Pageable pageable) {
        return chatUserRepository.findAllByChatName(chatName,pageable);
    }
}
