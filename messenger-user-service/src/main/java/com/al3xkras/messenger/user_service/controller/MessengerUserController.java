package com.al3xkras.messenger.user_service.controller;

import com.al3xkras.messenger.entity.MessengerUser;
import com.al3xkras.messenger.model.MessengerUserType;
import com.al3xkras.messenger.user_service.service.MessengerUserService;
import com.al3xkras.messenger.dto.MessengerUserDTO;
import com.al3xkras.messenger.user_service.exception.MessengerUserAlreadyExistsException;
import com.al3xkras.messenger.user_service.exception.MessengerUserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/user")
public class MessengerUserController {

    @Autowired
    private MessengerUserService messengerUserService;
    @Value("${spring.profiles.active}")
    private String profile;

    @ExceptionHandler(MessengerUserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleMessengerUserNotFoundException() {
        return "user not found";
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<String> handleResponseStatusException(ResponseStatusException e){
        return ResponseEntity.status(e.getStatus()).body(e.getReason());
    }

    @ExceptionHandler(MessengerUserAlreadyExistsException.class)
    public ResponseEntity<String> handleMessengerUserAlreadyExistsException(){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("messenger user with specified username already exists");
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
        if (!profile.equals("no-security") && !messengerUserDto.getMessengerUserType().equals(MessengerUserType.USER))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Messenger user of type ADMIN cannot be added");

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
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"please specify \"username\" or \"user-id\"");
    }

    @DeleteMapping
    public ResponseEntity<String> deleteMessengerUser(@RequestParam(value = "user-id", required = false) Long messengerUserId,
                                    @RequestParam(value = "username", required = false) String username)
                            throws MessengerUserNotFoundException{
        if (messengerUserId!=null){
            if (!profile.equals("no-security") && messengerUserService.getUserTypeById(messengerUserId).equals(MessengerUserType.ADMIN))
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Messenger user of type ADMIN cannot be deleted");
            messengerUserService.deleteById(messengerUserId);
            return ResponseEntity.status(HttpStatus.OK).body("deleted user with id "+messengerUserId);
        } else if (username!=null){
            if (!profile.equals("no-security") && messengerUserService.getUserTypeByUsername(username).equals(MessengerUserType.ADMIN))
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Messenger user of type ADMIN cannot be deleted");
            messengerUserService.deleteByUsername(username);
            return ResponseEntity.status(HttpStatus.OK).body("deleted user with username : \""+username+'\"');
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"please specify \"username\" or \"user-id\"");
    }
}
