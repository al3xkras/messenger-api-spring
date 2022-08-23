package com.al3xkras.messenger.chat_service.exception;

public class ChatNameAlreadyExistsException extends RuntimeException {
    public ChatNameAlreadyExistsException(String message) {
        super(message);
    }
}
