package com.al3xkras.messenger.model.security;

import com.al3xkras.messenger.model.ChatUserRole;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.Collection;

public class ChatUserAuthenticationToken extends AbstractAuthenticationToken {
    private final String username;
    private final Long userId;
    private final String chatName;
    private long chatId;
    private ChatUserRole chatUserRole;

    public ChatUserAuthenticationToken(String username, long userId, String chatName, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.username = username;
        this.userId = userId;
        this.chatName = chatName;
    }

    public ChatUserRole getChatUserRole() {
        return chatUserRole;
    }

    public void setChatUserRole(ChatUserRole chatUserRole) {
        this.chatUserRole = chatUserRole;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return new String[] {username, chatName};
    }

    //<editor-fold defaultstate="collapsed" desc="delombok">
    @SuppressWarnings("all")
    public String getUsername() {
        return this.username;
    }

    @SuppressWarnings("all")
    public Long getUserId() {
        return this.userId;
    }

    @SuppressWarnings("all")
    public String getChatName() {
        return this.chatName;
    }

    @SuppressWarnings("all")
    public long getChatId() {
        return this.chatId;
    }

    @SuppressWarnings("all")
    public void setChatId(final long chatId) {
        this.chatId = chatId;
    }
    //</editor-fold>
}
