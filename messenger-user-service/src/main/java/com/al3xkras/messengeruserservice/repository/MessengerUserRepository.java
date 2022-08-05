package com.al3xkras.messengeruserservice.repository;

import com.al3xkras.messengeruserservice.entity.MessengerUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


import java.util.Optional;

@Repository
public interface MessengerUserRepository extends JpaRepository<MessengerUser, Long> {

    Optional<MessengerUser> findByUsername(String username);

    @Query("select user from ChatUser chatUser join chatUser.messengerUser user " +
            "where chatUser.chatId=?1")
    Page<MessengerUser> findAllByChatId(Long chatId, Pageable pageable);

    @Modifying
    void deleteByUsername(String username);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO messenger_user (" +
            "messenger_user_id," +
            "username," +
            "name," +
            "surname," +
            "email_address," +
            "phone_number," +
            "user_type" +
            ") VALUES (" +
            "messenger_user_seq.nextval," +
            ":#{#messengerUser.username}," +
            ":#{#messengerUser.name}," +
            ":#{#messengerUser.surname}," +
            ":#{#messengerUser.emailAddress}," +
            ":#{#messengerUser.phoneNumber}," +
            ":#{#messengerUser.messengerUserType}" +
            ")",nativeQuery = true)
    void insert( @Param("messengerUser") MessengerUser messengerUser);
}
