package com.al3xkras.messengeruserservice.exception;

public class MessengerUserNotFoundException extends RuntimeException{
    public MessengerUserNotFoundException() {
    }

    public MessengerUserNotFoundException(String message) {
        super(message);
    }

    public MessengerUserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public MessengerUserNotFoundException(Throwable cause) {
        super(cause);
    }
}
