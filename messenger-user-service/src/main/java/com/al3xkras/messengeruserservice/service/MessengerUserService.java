package com.al3xkras.messengeruserservice.service;

import com.al3xkras.messengeruserservice.entity.MessengerUser;
import com.al3xkras.messengeruserservice.exception.MessengerUserNotFoundException;
import com.al3xkras.messengeruserservice.repository.MessengerUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class MessengerUserService {

    @Autowired
    public MessengerUserRepository messengerUserRepository;

    public MessengerUser findMessengerUserById(Long userId) throws MessengerUserNotFoundException{
        return messengerUserRepository.findById(userId)
                .orElseThrow(MessengerUserNotFoundException::new);
    }

    public MessengerUser findMessengerUserByUsername(String username) {
        return messengerUserRepository.findByUsername(username);
    }

    public MessengerUser saveUser(MessengerUser messengerUser) {
        return messengerUserRepository.save(messengerUser);
    }

    public MessengerUser updateUserById(Long messengerUserId, MessengerUser messengerUser) {
        return messengerUserRepository.updateById(messengerUserId,messengerUser);
    }

    public MessengerUser updateUserByUsername(String username, MessengerUser messengerUser) {
        return messengerUserRepository.updateByUsername(username,messengerUser);
    }

    public Page<MessengerUser> findAllUsersByChatId(Long chatId, Pageable pageable) {
        return messengerUserRepository.findAllByChatId(chatId, pageable);
    }

    public void deleteById(Long messengerUserId) throws MessengerUserNotFoundException{
        messengerUserRepository.deleteById(messengerUserId);
    }

    public void deleteByUsername(String username) throws MessengerUserNotFoundException {
        messengerUserRepository.deleteByUsername(username);
    }
}
