package com.al3xkras.messengeruserservice.controller;

import com.al3xkras.messengeruserservice.dto.MessengerUserDTO;
import com.al3xkras.messengeruserservice.dto.PageRequestDto;
import com.al3xkras.messengeruserservice.entity.Chat;
import com.al3xkras.messengeruserservice.entity.MessengerUser;
import com.al3xkras.messengeruserservice.exception.MessengerUserAlreadyExistsException;
import com.al3xkras.messengeruserservice.exception.MessengerUserNotFoundException;
import com.al3xkras.messengeruserservice.service.MessengerUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/user")
public class MessengerUserController {

    @Autowired
    private MessengerUserService messengerUserService;

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
                                      @RequestBody @Valid MessengerUserDTO messengerUserDTO)
            throws MessengerUserNotFoundException,MessengerUserAlreadyExistsException{
        MessengerUser messengerUser = MessengerUser.builder()
                .username(messengerUserDTO.getUsername())
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
            messengerUserService.deleteById(messengerUserId);
            return ResponseEntity.status(HttpStatus.OK).body("deleted user with id "+messengerUserId);
        } else if (username!=null){
            messengerUserService.deleteByUsername(username);
            return ResponseEntity.status(HttpStatus.OK).body("deleted user with username : \""+username+'\"');
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"please specify \"username\" or \"user-id\"");
    }
}
