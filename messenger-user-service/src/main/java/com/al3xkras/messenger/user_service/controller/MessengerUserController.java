package com.al3xkras.messenger.user_service.controller;

import com.al3xkras.messenger.dto.MessengerUserDTO;
import com.al3xkras.messenger.entity.MessengerUser;
import com.al3xkras.messenger.model.MessengerUserType;
import com.al3xkras.messenger.user_service.exception.MessengerUserAlreadyExistsException;
import com.al3xkras.messenger.user_service.exception.MessengerUserNotFoundException;
import com.al3xkras.messenger.user_service.service.MessengerUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.HashSet;

import static com.al3xkras.messenger.model.MessengerUtils.Messages.*;
import static com.al3xkras.messenger.model.security.JwtTokenAuth.Param.USERNAME;
import static com.al3xkras.messenger.model.security.JwtTokenAuth.Param.USER_ID;

@Slf4j
@RestController
@RequestMapping("/user")
public class MessengerUserController {

    private final MessengerUserService messengerUserService;
    private final HashSet<String> activeProfiles;

    @Autowired
    public MessengerUserController(MessengerUserService messengerUserService, Environment env) {
        this.messengerUserService = messengerUserService;
        activeProfiles = new HashSet<>(Arrays.asList(env.getActiveProfiles()));
    }

    @ExceptionHandler(MessengerUserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleMessengerUserNotFoundException() {
        return EXCEPTION_MESSENGER_USER_NOT_FOUND.value();
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<String> handleResponseStatusException(ResponseStatusException e){
        return ResponseEntity.status(e.getStatus()).body(e.getReason());
    }

    @ExceptionHandler(MessengerUserAlreadyExistsException.class)
    public ResponseEntity<String> handleMessengerUserAlreadyExistsException(MessengerUserAlreadyExistsException e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                String.format(EXCEPTION_MESSENGER_USER_USERNAME_EXISTS.value(),
                        e.getUsername()==null?"":e.getUsername()));
    }

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
    public MessengerUser addNewUser(@RequestBody @Valid MessengerUserDTO messengerUserDto) throws MessengerUserAlreadyExistsException {
        if (!activeProfiles.contains("no-security") && !messengerUserDto.getMessengerUserType().equals(MessengerUserType.USER))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    EXCEPTION_MESSENGER_USER_PERSIST_INVALID_USER_TYPE.value());

        MessengerUser messengerUser = MessengerUser.builder()
                .username(messengerUserDto.getUsername())
                .password(messengerUserDto.getPassword())
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
                                      @RequestBody @Valid MessengerUserDTO messengerUserDTO)
            throws MessengerUserNotFoundException,MessengerUserAlreadyExistsException{
        MessengerUser messengerUser = MessengerUser.builder()
                .username(messengerUserDTO.getUsername())
                .password(messengerUserDTO.getPassword())
                .name(messengerUserDTO.getName())
                .surname(messengerUserDTO.getSurname())
                .emailAddress(messengerUserDTO.getEmail())
                .phoneNumber(messengerUserDTO.getPhoneNumber())
                .messengerUserType(messengerUserDTO.getMessengerUserType())
                .build();
        if (messengerUserId!=null) {
            messengerUser.setMessengerUserId(messengerUserId);
            return messengerUserService.updateUserById(messengerUser);
        } else if (username!=null) {
            messengerUser.setUsername(username);
            return messengerUserService.updateUserByUsername(messengerUser);
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                String.format(EXCEPTION_REQUIRED_PARAMETERS_ARE_NULL.value(),
                        String.join(", ", USERNAME.value(), USER_ID.value())));
    }

    @DeleteMapping
    public ResponseEntity<String> deleteMessengerUser(@RequestParam(value = "user-id", required = false) Long messengerUserId,
                                    @RequestParam(value = "username", required = false) String username)
                            throws MessengerUserNotFoundException{
        if (messengerUserId!=null){
            if (!activeProfiles.contains("no-security") && messengerUserService.getUserTypeById(messengerUserId).equals(MessengerUserType.ADMIN))
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, EXCEPTION_MESSENGER_USER_DELETE_INVALID_USER_TYPE.value());
            messengerUserService.deleteById(messengerUserId);
            return ResponseEntity.status(HttpStatus.OK).build();
        } else if (username!=null){
            if (!activeProfiles.contains("no-security") && messengerUserService.getUserTypeByUsername(username).equals(MessengerUserType.ADMIN))
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, EXCEPTION_MESSENGER_USER_DELETE_INVALID_USER_TYPE.value());
            messengerUserService.deleteByUsername(username);
            return ResponseEntity.status(HttpStatus.OK).build();
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                String.format(EXCEPTION_REQUIRED_PARAMETERS_ARE_NULL.value(),
                        String.join(", ", USERNAME.value(), USER_ID.value())));
    }
}
