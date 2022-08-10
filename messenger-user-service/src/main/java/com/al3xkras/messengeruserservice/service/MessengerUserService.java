package com.al3xkras.messengeruserservice.service;

import com.al3xkras.messengeruserservice.entity.MessengerUser;
import com.al3xkras.messengeruserservice.exception.MessengerUserAlreadyExistsException;
import com.al3xkras.messengeruserservice.exception.MessengerUserNotFoundException;
import com.al3xkras.messengeruserservice.repository.MessengerUserRepository;
import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLIntegrityConstraintViolationException;

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
    public MessengerUser updateUserById(MessengerUser messengerUser) {
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
        return messengerUserRepository.save(updated);
    }

    @Transactional
    public MessengerUser updateUserByUsername(MessengerUser messengerUser) {
        MessengerUser beforeUpdate = messengerUserRepository.findById(messengerUser.getMessengerUserId())
                .orElseThrow(MessengerUserNotFoundException::new);
        MessengerUser updated = MessengerUser.builder()
                .messengerUserId(messengerUser.getMessengerUserId()==null?beforeUpdate.getMessengerUserId():messengerUser.getMessengerUserId())
                .username(messengerUser.getUsername())
                .name(messengerUser.getName()==null?beforeUpdate.getName():messengerUser.getName())
                .surname(messengerUser.getSurname()==null?beforeUpdate.getSurname():messengerUser.getSurname())
                .emailAddress(messengerUser.getEmailAddress()==null?beforeUpdate.getEmailAddress():messengerUser.getEmailAddress())
                .phoneNumber(messengerUser.getPhoneNumber()==null?beforeUpdate.getPhoneNumber():messengerUser.getPhoneNumber())
                .messengerUserType(messengerUser.getMessengerUserType()==null?beforeUpdate.getMessengerUserType():messengerUser.getMessengerUserType())
                .build();
        return messengerUserRepository.save(updated);
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
