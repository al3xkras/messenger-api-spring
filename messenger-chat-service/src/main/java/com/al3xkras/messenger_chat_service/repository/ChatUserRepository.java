package com.al3xkras.messenger_chat_service.repository;

import com.al3xkras.messenger_chat_service.entity.ChatUser;
import com.al3xkras.messenger_chat_service.model.ChatUserId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatUserRepository extends JpaRepository<ChatUser, ChatUserId> {

    ChatUser update(ChatUser chatUser);

}
