package com.al3xkras.messenger.message_service.entity;

import com.al3xkras.messenger.message_service.model.MessengerUserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessengerUser {
    public Long messengerUserId;
    public String username;
    public String name;
    public String surname;
    public String emailAddress;
    public String phoneNumber;
    public MessengerUserType messengerUserType;
}
