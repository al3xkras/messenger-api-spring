package com.al3xkras.messenger.user_service.service;

import com.al3xkras.messenger.entity.MessengerUser;
import com.al3xkras.messenger.user_service.exception.MessengerUserNotFoundException;
import com.al3xkras.messenger.user_service.model.MessengerUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Slf4j
public class MessengerUserDetailsService implements UserDetailsService {

    private final MessengerUserService messengerUserService;

    public MessengerUserDetailsService(MessengerUserService messengerUserService) {
        this.messengerUserService = messengerUserService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        MessengerUser messengerUser;
        try {
            messengerUser = messengerUserService.findMessengerUserByUsername(username);
        } catch (MessengerUserNotFoundException e){
            throw new UsernameNotFoundException(username);
        }
        return MessengerUserDetails.builder()
                .messengerUserId(messengerUser.getMessengerUserId())
                .username(messengerUser.getUsername())
                .password(messengerUser.getPassword())
                .messengerUserType(messengerUser.getMessengerUserType())
                .build();
    }
}
