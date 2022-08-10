package com.al3xkras.messenger_chat_service;

import com.al3xkras.messenger_chat_service.model.MessengerUserType;
import lombok.Builder;
import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@Builder
public class MessengerUserDTO {

    @NotNull
    @Size(min = 1, max = 15)
    @Pattern(regexp = "[a-zA-Z0-9_]{1,15}")
    private String username;

    @NotNull
    @Size(min = 8, max = 150)
    @Pattern(regexp = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[-+_!@#$%^&*.,?]).{8,}$")
    private String password;

    @NotNull
    @Size(min = 1, max = 20)
    private String name;
    private String surname;
    @Email
    private String email;
    @NotNull
    @Pattern(regexp = "^(\\+?\\d{1,3} ?\\d{3}-?\\d{2}-?\\d{2})$")
    private String phoneNumber;
    @NotNull
    @Enumerated(EnumType.STRING)
    private MessengerUserType messengerUserType;
}
