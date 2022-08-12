package com.al3xkras.messenger.message_service.exception;

public class ChatMessageNotFoundException extends RuntimeException {
    public ChatMessageNotFoundException() {
    }

    public ChatMessageNotFoundException(String message) {
        super(message);
    }

    public ChatMessageNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ChatMessageNotFoundException(Throwable cause) {
        super(cause);
    }
}
