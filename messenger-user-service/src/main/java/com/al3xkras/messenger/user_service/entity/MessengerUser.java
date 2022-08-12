package com.al3xkras.messenger.user_service.entity;

import com.al3xkras.messenger.user_service.model.MessengerUserType;
import lombok.*;

import javax.persistence.*;


@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "messenger_user", uniqueConstraints = @UniqueConstraint(name = "messenger_user_username_un",columnNames = {
        "username"
}))
@EqualsAndHashCode
public class MessengerUser {
    @Id
    @SequenceGenerator(name = "messenger_user_seq",sequenceName = "messenger_user_seq",allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "messenger_user_seq")
    @Column(name = "messenger_user_id", nullable = false)
    public Long messengerUserId;

    @Column(name = "username", columnDefinition = "nvarchar(15)",nullable = false)
    public String username;
    @Column(name = "name", columnDefinition = "varchar(25)", nullable = false)
    public String name;
    @Column(name = "surname", columnDefinition = "varchar(25)")
    public String surname;
    @Column(name = "email_address")
    public String emailAddress;
    @Column(name = "phone_number")
    public String phoneNumber;
    @Enumerated(EnumType.STRING)
    @Column(name = "user_type", nullable = false)
    public MessengerUserType messengerUserType;

}
