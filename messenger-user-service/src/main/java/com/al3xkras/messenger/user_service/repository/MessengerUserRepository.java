package com.al3xkras.messenger.user_service.repository;

import com.al3xkras.messenger.entity.MessengerUser;
import com.al3xkras.messenger.model.MessengerUserType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MessengerUserRepository extends JpaRepository<MessengerUser, Long> {

    @Query("select user.messengerUserType from MessengerUser user where user.messengerUserId=?1")
    Optional<MessengerUserType> getUserTypeById(Long messengerUserId);
    @Query("select user.messengerUserType from MessengerUser user where user.username=?1")
    Optional<MessengerUserType> getUserTypeByUsername(String username);

    Optional<MessengerUser> findByUsername(String username);

    @Query("select user from ChatUser chatUser join chatUser.messengerUser user " +
            "where chatUser.chatId=?1")
    Page<MessengerUser> findAllByChatId(Long chatId, Pageable pageable);

}
