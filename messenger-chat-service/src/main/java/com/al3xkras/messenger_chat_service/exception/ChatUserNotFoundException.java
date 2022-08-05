package com.al3xkras.messenger_chat_service.exception;

public class ChatUserNotFoundException extends RuntimeException {
    public ChatUserNotFoundException() {
    }

    public ChatUserNotFoundException(String message) {
        super(message);
    }

    public ChatUserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ChatUserNotFoundException(Throwable cause) {
        super(cause);
    }
}
