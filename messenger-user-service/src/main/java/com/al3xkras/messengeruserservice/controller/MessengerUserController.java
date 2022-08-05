package com.al3xkras.messengeruserservice.controller;

import com.al3xkras.messengeruserservice.dto.MessengerUserDTO;
import com.al3xkras.messengeruserservice.dto.PageRequestDto;
import com.al3xkras.messengeruserservice.entity.Chat;
import com.al3xkras.messengeruserservice.entity.MessengerUser;
import com.al3xkras.messengeruserservice.exception.MessengerUserNotFoundException;
import com.al3xkras.messengeruserservice.service.MessengerUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

@RestController
@RequestMapping("/user")
public class MessengerUserController {

    @Autowired
    public MessengerUserService messengerUserService;

    @GetMapping("{id}")
    public MessengerUser findById(@PathVariable("id") Long userId)
            throws MessengerUserNotFoundException {
        return messengerUserService.findMessengerUserById(userId);
    }

    @GetMapping
    public MessengerUser findByUsername(@RequestParam("username") String username)
            throws MessengerUserNotFoundException {
        return messengerUserService.findMessengerUserByUsername(username);
    }

    @PostMapping
    public MessengerUser addNewUser(@Valid MessengerUserDTO messengerUserDto){
        MessengerUser messengerUser = MessengerUser.builder()
                .username(messengerUserDto.getUsername())
                .name(messengerUserDto.getName())
                .surname(messengerUserDto.getSurname())
                .emailAddress(messengerUserDto.getEmail())
                .phoneNumber(messengerUserDto.getPhoneNumber())
                .messengerUserType(messengerUserDto.getMessengerUserType())
                .build();
        return messengerUserService.saveUser(messengerUser);
    }

    @PutMapping
    public MessengerUser editUserData(@RequestParam(value = "user-id", required = false) Long messengerUserId,
                                      @RequestParam(value = "username", required = false) String username,
                                      MessengerUserDTO messengerUserDTO){
        MessengerUser messengerUser = MessengerUser.builder()
                .username(messengerUserDTO.getUsername())
                .name(messengerUserDTO.getName())
                .surname(messengerUserDTO.getSurname())
                .emailAddress(messengerUserDTO.getEmail())
                .phoneNumber(messengerUserDTO.getPhoneNumber())
                .messengerUserType(messengerUserDTO.getMessengerUserType())
                .build();
        if (messengerUserId!=null) {
            return messengerUserService.updateUserById(messengerUser);
        } else if (username!=null) {
            return messengerUserService.updateUserByUsername(messengerUser);
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"please specify \"username\" or \"user-id\"");
    }

    @DeleteMapping
    public void deleteMessengerUser(@RequestParam(value = "user-id", required = false) Long messengerUserId,
                                    @RequestParam(value = "username", required = false) String username)
                            throws MessengerUserNotFoundException{
        if (messengerUserId!=null){
            messengerUserService.deleteById(messengerUserId);
        } else if (username!=null){
            messengerUserService.deleteByUsername(username);
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"please specify \"username\" or \"user-id\"");
    }


                                      @GetMapping("user/{id}/chats")
    public Page<Chat> getUserChatsByUserId(@PathVariable("id") Long messengerUserId,
                                           @RequestBody PageRequestDto pageRequestDto){
        //TODO implement
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }
}
