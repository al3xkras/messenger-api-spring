package com.al3xkras.messenger.chat_service.repository;

import com.al3xkras.messenger.entity.ChatUser;
import com.al3xkras.messenger.model.ChatUserId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ChatUserRepository extends JpaRepository<ChatUser, ChatUserId> {

    @Query("select chatUser from ChatUser chatUser where chatUser.chatId=?1")
    Page<ChatUser> findAllByChatId(Long chatId, Pageable pageable);

    @Query("select chatUser from ChatUser chatUser where " +
            "chatUser.chatId=(select chat.chatId from Chat chat where chat.chatName=?1)")
    Page<ChatUser> findAllByChatName(String chatName, Pageable pageable);

}
