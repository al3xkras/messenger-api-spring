package com.al3xkras.messenger_chat_service.repository;

import com.al3xkras.messenger_chat_service.entity.ChatUser;
import com.al3xkras.messenger_chat_service.model.ChatUserId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ChatUserRepository extends JpaRepository<ChatUser, ChatUserId> {

    @Query("select chatUser from ChatUser chatUser fetch all properties where chatUser.chatId=?1")
    Page<ChatUser> findAllByChatIdFetchMessengerUser(Long chatId, Pageable pageable);

    Page<ChatUser> findAllByChatId(Long chatId, Pageable pageable);

    @Query("select chatUser from ChatUser chatUser fetch all properties where " +
            "chatUser.chatId=(select chat.chatId from Chat chat where chat.chatName=?1)")
    Page<ChatUser> findAllByChatNameFetchMessengerUser(String chatName, Pageable pageable);

    @Query("select chatUser from ChatUser chatUser where " +
            "chatUser.chatId=(select chat.chatId from Chat chat where chat.chatName=?1)")
    Page<ChatUser> findAllByChatName(String chatName, Pageable pageable);
}
