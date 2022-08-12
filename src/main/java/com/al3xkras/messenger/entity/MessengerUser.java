package com.al3xkras.messenger.entity;

import com.al3xkras.messenger.model.MessengerUserType;
import javax.persistence.*;

@Entity
@Table(name = "messenger_user", uniqueConstraints = @UniqueConstraint(name = "messenger_user_username_un", columnNames = {"username"}))
public class MessengerUser {
    @Id
    @SequenceGenerator(name = "messenger_user_seq", sequenceName = "messenger_user_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "messenger_user_seq")
    @Column(name = "messenger_user_id", nullable = false)
    public Long messengerUserId;
    @Column(name = "username", columnDefinition = "nvarchar(15)", nullable = false)
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


    //<editor-fold defaultstate="collapsed" desc="delombok">
    @SuppressWarnings("all")
    public static class MessengerUserBuilder {
        @SuppressWarnings("all")
        private Long messengerUserId;
        @SuppressWarnings("all")
        private String username;
        @SuppressWarnings("all")
        private String name;
        @SuppressWarnings("all")
        private String surname;
        @SuppressWarnings("all")
        private String emailAddress;
        @SuppressWarnings("all")
        private String phoneNumber;
        @SuppressWarnings("all")
        private MessengerUserType messengerUserType;

        @SuppressWarnings("all")
        MessengerUserBuilder() {
        }

        @SuppressWarnings("all")
        public MessengerUser.MessengerUserBuilder messengerUserId(final Long messengerUserId) {
            this.messengerUserId = messengerUserId;
            return this;
        }

        @SuppressWarnings("all")
        public MessengerUser.MessengerUserBuilder username(final String username) {
            this.username = username;
            return this;
        }

        @SuppressWarnings("all")
        public MessengerUser.MessengerUserBuilder name(final String name) {
            this.name = name;
            return this;
        }

        @SuppressWarnings("all")
        public MessengerUser.MessengerUserBuilder surname(final String surname) {
            this.surname = surname;
            return this;
        }

        @SuppressWarnings("all")
        public MessengerUser.MessengerUserBuilder emailAddress(final String emailAddress) {
            this.emailAddress = emailAddress;
            return this;
        }

        @SuppressWarnings("all")
        public MessengerUser.MessengerUserBuilder phoneNumber(final String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        @SuppressWarnings("all")
        public MessengerUser.MessengerUserBuilder messengerUserType(final MessengerUserType messengerUserType) {
            this.messengerUserType = messengerUserType;
            return this;
        }

        @SuppressWarnings("all")
        public MessengerUser build() {
            return new MessengerUser(this.messengerUserId, this.username, this.name, this.surname, this.emailAddress, this.phoneNumber, this.messengerUserType);
        }

        @Override
        @SuppressWarnings("all")
        public String toString() {
            return "MessengerUser.MessengerUserBuilder(messengerUserId=" + this.messengerUserId + ", username=" + this.username + ", name=" + this.name + ", surname=" + this.surname + ", emailAddress=" + this.emailAddress + ", phoneNumber=" + this.phoneNumber + ", messengerUserType=" + this.messengerUserType + ")";
        }
    }

    @SuppressWarnings("all")
    public static MessengerUser.MessengerUserBuilder builder() {
        return new MessengerUser.MessengerUserBuilder();
    }

    @SuppressWarnings("all")
    public Long getMessengerUserId() {
        return this.messengerUserId;
    }

    @SuppressWarnings("all")
    public String getUsername() {
        return this.username;
    }

    @SuppressWarnings("all")
    public String getName() {
        return this.name;
    }

    @SuppressWarnings("all")
    public String getSurname() {
        return this.surname;
    }

    @SuppressWarnings("all")
    public String getEmailAddress() {
        return this.emailAddress;
    }

    @SuppressWarnings("all")
    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    @SuppressWarnings("all")
    public MessengerUserType getMessengerUserType() {
        return this.messengerUserType;
    }

    @SuppressWarnings("all")
    public void setMessengerUserId(final Long messengerUserId) {
        this.messengerUserId = messengerUserId;
    }

    @SuppressWarnings("all")
    public void setUsername(final String username) {
        this.username = username;
    }

    @SuppressWarnings("all")
    public void setName(final String name) {
        this.name = name;
    }

    @SuppressWarnings("all")
    public void setSurname(final String surname) {
        this.surname = surname;
    }

    @SuppressWarnings("all")
    public void setEmailAddress(final String emailAddress) {
        this.emailAddress = emailAddress;
    }

    @SuppressWarnings("all")
    public void setPhoneNumber(final String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @SuppressWarnings("all")
    public void setMessengerUserType(final MessengerUserType messengerUserType) {
        this.messengerUserType = messengerUserType;
    }

    @Override
    @SuppressWarnings("all")
    public String toString() {
        return "MessengerUser(messengerUserId=" + this.getMessengerUserId() + ", username=" + this.getUsername() + ", name=" + this.getName() + ", surname=" + this.getSurname() + ", emailAddress=" + this.getEmailAddress() + ", phoneNumber=" + this.getPhoneNumber() + ", messengerUserType=" + this.getMessengerUserType() + ")";
    }

    @SuppressWarnings("all")
    public MessengerUser(final Long messengerUserId, final String username, final String name, final String surname, final String emailAddress, final String phoneNumber, final MessengerUserType messengerUserType) {
        this.messengerUserId = messengerUserId;
        this.username = username;
        this.name = name;
        this.surname = surname;
        this.emailAddress = emailAddress;
        this.phoneNumber = phoneNumber;
        this.messengerUserType = messengerUserType;
    }

    @SuppressWarnings("all")
    public MessengerUser() {
    }

    @Override
    @SuppressWarnings("all")
    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof MessengerUser)) return false;
        final MessengerUser other = (MessengerUser) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$messengerUserId = this.getMessengerUserId();
        final Object other$messengerUserId = other.getMessengerUserId();
        if (this$messengerUserId == null ? other$messengerUserId != null : !this$messengerUserId.equals(other$messengerUserId)) return false;
        final Object this$username = this.getUsername();
        final Object other$username = other.getUsername();
        if (this$username == null ? other$username != null : !this$username.equals(other$username)) return false;
        final Object this$name = this.getName();
        final Object other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals(other$name)) return false;
        final Object this$surname = this.getSurname();
        final Object other$surname = other.getSurname();
        if (this$surname == null ? other$surname != null : !this$surname.equals(other$surname)) return false;
        final Object this$emailAddress = this.getEmailAddress();
        final Object other$emailAddress = other.getEmailAddress();
        if (this$emailAddress == null ? other$emailAddress != null : !this$emailAddress.equals(other$emailAddress)) return false;
        final Object this$phoneNumber = this.getPhoneNumber();
        final Object other$phoneNumber = other.getPhoneNumber();
        if (this$phoneNumber == null ? other$phoneNumber != null : !this$phoneNumber.equals(other$phoneNumber)) return false;
        final Object this$messengerUserType = this.getMessengerUserType();
        final Object other$messengerUserType = other.getMessengerUserType();
        if (this$messengerUserType == null ? other$messengerUserType != null : !this$messengerUserType.equals(other$messengerUserType)) return false;
        return true;
    }

    @SuppressWarnings("all")
    protected boolean canEqual(final Object other) {
        return other instanceof MessengerUser;
    }

    @Override
    @SuppressWarnings("all")
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $messengerUserId = this.getMessengerUserId();
        result = result * PRIME + ($messengerUserId == null ? 43 : $messengerUserId.hashCode());
        final Object $username = this.getUsername();
        result = result * PRIME + ($username == null ? 43 : $username.hashCode());
        final Object $name = this.getName();
        result = result * PRIME + ($name == null ? 43 : $name.hashCode());
        final Object $surname = this.getSurname();
        result = result * PRIME + ($surname == null ? 43 : $surname.hashCode());
        final Object $emailAddress = this.getEmailAddress();
        result = result * PRIME + ($emailAddress == null ? 43 : $emailAddress.hashCode());
        final Object $phoneNumber = this.getPhoneNumber();
        result = result * PRIME + ($phoneNumber == null ? 43 : $phoneNumber.hashCode());
        final Object $messengerUserType = this.getMessengerUserType();
        result = result * PRIME + ($messengerUserType == null ? 43 : $messengerUserType.hashCode());
        return result;
    }
    //</editor-fold>
}