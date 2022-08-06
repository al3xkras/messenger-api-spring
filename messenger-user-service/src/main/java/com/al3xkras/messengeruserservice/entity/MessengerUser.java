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
@EqualsAndHashCode
@Builder
@Entity
@Table(name = "messenger_user")
public class MessengerUser {
    @Id
    @SequenceGenerator(name = "messenger_user_seq", sequenceName = "messenger_user_seq",allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "messenger_user_seq")
    @Column(name = "messenger_user_id", nullable = false)
    public Long messengerUserId;

    @Column(name = "username")
    public String username;
    @Column(name = "name")
    public String name;
    @Column(name = "surname")
    public String surname;
    @Column(name = "email_address")
    public String emailAddress;
    @Column(name = "phone_umber")
    public String phoneNumber;
    @Column(name = "user_type")
    @Enumerated(EnumType.STRING)
    public MessengerUserType messengerUserType;


}
