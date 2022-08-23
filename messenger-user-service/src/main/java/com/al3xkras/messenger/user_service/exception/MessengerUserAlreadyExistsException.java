package com.al3xkras.messenger.user_service.exception;

public class MessengerUserAlreadyExistsException extends RuntimeException {
    private final String username;
    public MessengerUserAlreadyExistsException(String username) {
        super(username);
        this.username=username;
    }

    public String getUsername() {
        return username;
    }
}
