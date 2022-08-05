package com.al3xkras.messenger_chat_service.repository;

import com.al3xkras.messenger_chat_service.entity.Chat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface ChatRepository extends JpaRepository<Chat,Long> {

    @Transactional
    @Query("select chat from ChatUser chatUser join chatUser.chat chat where chatUser.userId=?1")
    Page<Chat> findAllChatsByUserId(Long messengerUserId, Pageable pageable);

    @Transactional
    @Query("select chat from ChatUser chatUser join chatUser.chat chat where chatUser.userId=?1")
    Page<Chat> findAllByUserId(Long messengerUserId, Pageable pageable);

    @Transactional
    @Query("select chat from ChatUser chatUser join chatUser.chat chat where chatUser.userId=" +
            "(select messengerUser.messengerUserId from MessengerUser messengerUser where messengerUser.username=?1)")
    Page<Chat> findAllByUsername(String username, Pageable pageable);

    Optional<Chat> findByChatName(String chatName);
}
