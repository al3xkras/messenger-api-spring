package com.al3xkras.messengeruserservice.service;

import com.al3xkras.messengeruserservice.entity.MessengerUser;
import com.al3xkras.messengeruserservice.exception.MessengerUserAlreadyExistsException;
import com.al3xkras.messengeruserservice.exception.MessengerUserNotFoundException;
import com.al3xkras.messengeruserservice.repository.MessengerUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class MessengerUserService {

    @Autowired
    public MessengerUserRepository messengerUserRepository;

    public MessengerUser findMessengerUserById(Long userId) throws MessengerUserNotFoundException{
        return messengerUserRepository.findById(userId)
                .orElseThrow(MessengerUserNotFoundException::new);
    }

    public MessengerUser findMessengerUserByUsername(String username) {
        return messengerUserRepository.findByUsername(username)
                .orElseThrow(MessengerUserNotFoundException::new);
    }

    public MessengerUser saveUser(MessengerUser messengerUser) throws MessengerUserAlreadyExistsException{
        try {
            messengerUser.setMessengerUserId(null);
            return messengerUserRepository.saveAndFlush(messengerUser);
        } catch (DataIntegrityViolationException e){
            throw new MessengerUserAlreadyExistsException(messengerUser.getUsername());
        }
    }

    @Transactional
    public MessengerUser updateUserById(MessengerUser messengerUser) throws MessengerUserNotFoundException, MessengerUserAlreadyExistsException{
        if (messengerUser.getMessengerUserId()==null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"user ID is null");
        MessengerUser beforeUpdate = messengerUserRepository.findById(messengerUser.getMessengerUserId())
                .orElseThrow(MessengerUserNotFoundException::new);
        MessengerUser updated = MessengerUser.builder()
                .messengerUserId(messengerUser.getMessengerUserId())
                .username(messengerUser.getUsername()==null?beforeUpdate.getUsername():messengerUser.getUsername())
                .name(messengerUser.getName()==null?beforeUpdate.getName():messengerUser.getName())
                .surname(messengerUser.getSurname()==null?beforeUpdate.getSurname():messengerUser.getSurname())
                .emailAddress(messengerUser.getEmailAddress()==null?beforeUpdate.getEmailAddress():messengerUser.getEmailAddress())
                .phoneNumber(messengerUser.getPhoneNumber()==null?beforeUpdate.getPhoneNumber():messengerUser.getPhoneNumber())
                .messengerUserType(messengerUser.getMessengerUserType()==null?beforeUpdate.getMessengerUserType():messengerUser.getMessengerUserType())
                .build();
        try {
            return messengerUserRepository.saveAndFlush(updated);
        } catch (DataIntegrityViolationException e){
            throw new MessengerUserAlreadyExistsException(messengerUser.getUsername());
        }
    }

    @Transactional
    public MessengerUser updateUserByUsername(MessengerUser messengerUser) throws MessengerUserNotFoundException {
        if (messengerUser.getUsername()==null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"username is null");
        MessengerUser beforeUpdate = messengerUserRepository.findByUsername(messengerUser.getUsername())
                .orElseThrow(MessengerUserNotFoundException::new);
        MessengerUser updated = MessengerUser.builder()
                .messengerUserId(beforeUpdate.getMessengerUserId())
                .username(messengerUser.getUsername())
                .name(messengerUser.getName()==null?beforeUpdate.getName():messengerUser.getName())
                .surname(messengerUser.getSurname()==null?beforeUpdate.getSurname():messengerUser.getSurname())
                .emailAddress(messengerUser.getEmailAddress()==null?beforeUpdate.getEmailAddress():messengerUser.getEmailAddress())
                .phoneNumber(messengerUser.getPhoneNumber()==null?beforeUpdate.getPhoneNumber():messengerUser.getPhoneNumber())
                .messengerUserType(messengerUser.getMessengerUserType()==null?beforeUpdate.getMessengerUserType():messengerUser.getMessengerUserType())
                .build();
        return messengerUserRepository.saveAndFlush(updated);
    }

    public Page<MessengerUser> findAllUsersByChatId(Long chatId, Pageable pageable) {
        return messengerUserRepository.findAllByChatId(chatId, pageable);
    }

    public void deleteById(Long messengerUserId) throws MessengerUserNotFoundException{
        try {
            messengerUserRepository.deleteById(messengerUserId);
        } catch (EmptyResultDataAccessException e){
            throw new MessengerUserNotFoundException();
        }
    }

    @Transactional
    public void deleteByUsername(String username) throws MessengerUserNotFoundException {
        MessengerUser existing = messengerUserRepository.findByUsername(username)
                        .orElseThrow(MessengerUserNotFoundException::new);
        messengerUserRepository.deleteById(existing.getMessengerUserId());
    }
}
