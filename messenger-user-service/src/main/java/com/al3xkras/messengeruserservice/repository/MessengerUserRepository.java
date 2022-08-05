package com.al3xkras.messengeruserservice.repository;

import com.al3xkras.messengeruserservice.entity.MessengerUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface MessengerUserRepository extends JpaRepository<MessengerUser, Long> {
    MessengerUser findByUsername(String username);

    MessengerUser updateById(Long messengerUserId, MessengerUser messengerUser);

    MessengerUser updateByUsername(String username, MessengerUser messengerUser);

    @Transactional
    @Query("select user from ChatUser chatUser join fetch chatUser.messengerUser user " +
            "where chatUser.chatId=?1")
    Page<MessengerUser> findAllByChatId(Long chatId, Pageable pageable);

    void deleteByUsername(String username);
}
