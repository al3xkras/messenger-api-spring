package com.al3xkras.messengermessageservice.entity;

import com.al3xkras.messengermessageservice.model.MessengerUserType;
import lombok.*;

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
