package com.al3xkras.messenger.chat_service.exception;

import org.springframework.security.core.AuthenticationException;

public class ChatServiceAuthenticationException extends AuthenticationException {
    public ChatServiceAuthenticationException(String s) {
        super(s);
    }
}
