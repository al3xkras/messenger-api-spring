package com.al3xkras.messenger.dto;

import com.al3xkras.messenger.model.MessengerUserType;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

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

    //<editor-fold defaultstate="collapsed" desc="delombok">
    @SuppressWarnings("all")
    MessengerUserDTO(final String username, final String password, final String name, final String surname, final String email, final String phoneNumber, final MessengerUserType messengerUserType) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.messengerUserType = messengerUserType;
    }


    @SuppressWarnings("all")
    public static class MessengerUserDTOBuilder {
        @SuppressWarnings("all")
        private String username;
        @SuppressWarnings("all")
        private String password;
        @SuppressWarnings("all")
        private String name;
        @SuppressWarnings("all")
        private String surname;
        @SuppressWarnings("all")
        private String email;
        @SuppressWarnings("all")
        private String phoneNumber;
        @SuppressWarnings("all")
        private MessengerUserType messengerUserType;

        @SuppressWarnings("all")
        MessengerUserDTOBuilder() {
        }

        @SuppressWarnings("all")
        public MessengerUserDTO.MessengerUserDTOBuilder username(final String username) {
            this.username = username;
            return this;
        }

        @SuppressWarnings("all")
        public MessengerUserDTO.MessengerUserDTOBuilder password(final String password) {
            this.password = password;
            return this;
        }

        @SuppressWarnings("all")
        public MessengerUserDTO.MessengerUserDTOBuilder name(final String name) {
            this.name = name;
            return this;
        }

        @SuppressWarnings("all")
        public MessengerUserDTO.MessengerUserDTOBuilder surname(final String surname) {
            this.surname = surname;
            return this;
        }

        @SuppressWarnings("all")
        public MessengerUserDTO.MessengerUserDTOBuilder email(final String email) {
            this.email = email;
            return this;
        }

        @SuppressWarnings("all")
        public MessengerUserDTO.MessengerUserDTOBuilder phoneNumber(final String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        @SuppressWarnings("all")
        public MessengerUserDTO.MessengerUserDTOBuilder messengerUserType(final MessengerUserType messengerUserType) {
            this.messengerUserType = messengerUserType;
            return this;
        }

        @SuppressWarnings("all")
        public MessengerUserDTO build() {
            return new MessengerUserDTO(this.username, this.password, this.name, this.surname, this.email, this.phoneNumber, this.messengerUserType);
        }

        @Override
        @SuppressWarnings("all")
        public String toString() {
            return "MessengerUserDTO.MessengerUserDTOBuilder(username=" + this.username + ", password=" + this.password + ", name=" + this.name + ", surname=" + this.surname + ", email=" + this.email + ", phoneNumber=" + this.phoneNumber + ", messengerUserType=" + this.messengerUserType + ")";
        }
    }

    @SuppressWarnings("all")
    public static MessengerUserDTO.MessengerUserDTOBuilder builder() {
        return new MessengerUserDTO.MessengerUserDTOBuilder();
    }

    @SuppressWarnings("all")
    public String getUsername() {
        return this.username;
    }

    @SuppressWarnings("all")
    public String getPassword() {
        return this.password;
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
    public String getEmail() {
        return this.email;
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
    public void setUsername(final String username) {
        this.username = username;
    }

    @SuppressWarnings("all")
    public void setPassword(final String password) {
        this.password = password;
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
    public void setEmail(final String email) {
        this.email = email;
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
    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof MessengerUserDTO)) return false;
        final MessengerUserDTO other = (MessengerUserDTO) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$username = this.getUsername();
        final Object other$username = other.getUsername();
        if (this$username == null ? other$username != null : !this$username.equals(other$username)) return false;
        final Object this$password = this.getPassword();
        final Object other$password = other.getPassword();
        if (this$password == null ? other$password != null : !this$password.equals(other$password)) return false;
        final Object this$name = this.getName();
        final Object other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals(other$name)) return false;
        final Object this$surname = this.getSurname();
        final Object other$surname = other.getSurname();
        if (this$surname == null ? other$surname != null : !this$surname.equals(other$surname)) return false;
        final Object this$email = this.getEmail();
        final Object other$email = other.getEmail();
        if (this$email == null ? other$email != null : !this$email.equals(other$email)) return false;
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
        return other instanceof MessengerUserDTO;
    }

    @Override
    @SuppressWarnings("all")
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $username = this.getUsername();
        result = result * PRIME + ($username == null ? 43 : $username.hashCode());
        final Object $password = this.getPassword();
        result = result * PRIME + ($password == null ? 43 : $password.hashCode());
        final Object $name = this.getName();
        result = result * PRIME + ($name == null ? 43 : $name.hashCode());
        final Object $surname = this.getSurname();
        result = result * PRIME + ($surname == null ? 43 : $surname.hashCode());
        final Object $email = this.getEmail();
        result = result * PRIME + ($email == null ? 43 : $email.hashCode());
        final Object $phoneNumber = this.getPhoneNumber();
        result = result * PRIME + ($phoneNumber == null ? 43 : $phoneNumber.hashCode());
        final Object $messengerUserType = this.getMessengerUserType();
        result = result * PRIME + ($messengerUserType == null ? 43 : $messengerUserType.hashCode());
        return result;
    }

    @Override
    @SuppressWarnings("all")
    public String toString() {
        return "MessengerUserDTO(username=" + this.getUsername() + ", password=" + this.getPassword() + ", name=" + this.getName() + ", surname=" + this.getSurname() + ", email=" + this.getEmail() + ", phoneNumber=" + this.getPhoneNumber() + ", messengerUserType=" + this.getMessengerUserType() + ")";
    }
    //</editor-fold>
}
