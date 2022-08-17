package com.al3xkras.messenger.model.authorities;

import org.springframework.security.core.GrantedAuthority;

public enum MessengerUserAuthority implements GrantedAuthority {
    ADD_USER,
    READ_SELF_INFO,
    READ_ANY_USER_INFO_EXCEPT_SELF,
    MODIFY_SELF,
    MODIFY_ANY_USER_EXCEPT_SELF,
    DELETE_SELF,
    DELETE_ANY_USER_EXCEPT_SELF,
    CREATE_CHAT,
    VIEW_PUBLIC_CHATS,
    VIEW_PRIVATE_CHATS,
    VIEW_SELF_CHATS;

    @Override
    public String getAuthority() {
        return this.name();
    }
}
