package com.al3xkras.messenger.model.authorities;

import org.springframework.security.core.GrantedAuthority;

public enum ChatUserAuthority implements GrantedAuthority {
    MODIFY_CHAT_INFO,
    ADD_CHAT_USER,
    MODIFY_SELF_INFO,
    MODIFY_CHAT_USER_INFO_EXCEPT_SELF,
    MODIFY_CHAT_USER_TYPE,
    DELETE_CHAT_USER_EXCEPT_SELF,
    DELETE_SELF,
    VIEW_MESSAGES,
    SEND_MESSAGES,
    MODIFY_SELF_CHAT_MESSAGES,
    MODIFY_ANY_CHAT_MESSAGE,
    DELETE_SELF_CHAT_MESSAGES,
    DELETE_ANY_CHAT_MESSAGE;
    @Override
    public String getAuthority() {
        return this.name();
    }
}
