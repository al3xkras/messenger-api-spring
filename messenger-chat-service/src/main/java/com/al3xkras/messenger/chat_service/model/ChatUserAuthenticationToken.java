package com.al3xkras.messenger.chat_service.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;

@Getter
public class ChatUserAuthenticationToken extends AbstractAuthenticationToken {
    private final String username;
    private final Long userId;
    private final String chatName;
    @Setter
    private long chatId;

    public ChatUserAuthenticationToken(String username, long userId, String chatName, Collection<SimpleGrantedAuthority> authorities) {
        super(authorities);
        this.username=username;
        this.userId=userId;
        this.chatName=chatName;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return new String[]{username,chatName};
    }
}
