package com.al3xkras.messengeruserservice.dto;

import com.al3xkras.messengeruserservice.model.MessengerUserType;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
public class MessengerUserDTO {

    @NotNull
    @Size(min = 1, max = 15)
    private String username;

    @NotNull
    @Size(min = 8, max = 150)
    private String password;

    @NotNull
    @Size(min = 1, max = 20)
    private String name;
    private String surname = "";
    private String email;
    @NotNull
    private String phoneNumber;
    @NotNull
    private MessengerUserType messengerUserType;
}
