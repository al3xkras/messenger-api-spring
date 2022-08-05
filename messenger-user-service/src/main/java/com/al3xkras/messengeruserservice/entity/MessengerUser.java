package com.al3xkras.messengeruserservice.entity;

import com.al3xkras.messengeruserservice.model.MessengerUserType;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;


@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "messenger_user")
public class MessengerUser {
    @Id
    public Long messengerUserId;

    public String username;

    public String name;
    public String surname;
    public String emailAddress;
    public String phoneNumber;
    @Enumerated(EnumType.STRING)
    public MessengerUserType messengerUserType;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        MessengerUser that = (MessengerUser) o;
        return messengerUserId != null && Objects.equals(messengerUserId, that.messengerUserId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
