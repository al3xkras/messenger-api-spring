package com.al3xkras.messenger.user_service.exception;

public class MessengerUserAlreadyExistsException extends RuntimeException {
    public MessengerUserAlreadyExistsException(String username) {
        super("MessengerUser with username \""+username+"\" already exists");
    }
}
