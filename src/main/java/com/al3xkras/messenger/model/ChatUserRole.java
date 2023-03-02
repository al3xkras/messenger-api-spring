package com.al3xkras.messenger.model;

import com.al3xkras.messenger.model.authorities.ChatUserAuthority;

import java.util.*;

import static com.al3xkras.messenger.model.authorities.ChatUserAuthority.*;

public enum ChatUserRole {
    ANONYMOUS(Arrays.asList(CREATE_CHAT,READ_SELF_CHATS_INFO)),

    USER(Arrays.asList(ADD_CHAT_USER,READ_SELF_CHATS_INFO,READ_SELF_CHAT_MESSAGES,MODIFY_SELF_INFO,DELETE_SELF,
            VIEW_MESSAGES,SEND_MESSAGES,MODIFY_SELF_CHAT_MESSAGES)),

    ADMIN(Arrays.asList(READ_SELF_CHATS_INFO,READ_SELF_CHAT_MESSAGES,
            MODIFY_CHAT_INFO, ADD_CHAT_USER, MODIFY_SELF_INFO,
            MODIFY_CHAT_USER_INFO_EXCEPT_SELF, MODIFY_CHAT_USER_TYPE,
            DELETE_CHAT_USER_EXCEPT_SELF, VIEW_MESSAGES, SEND_MESSAGES,
            MODIFY_ANY_CHAT_MESSAGE, DELETE_ANY_CHAT_MESSAGE)),

    SUPER_ADMIN(Arrays.asList(ChatUserAuthority.values()));
    private final Set<ChatUserAuthority> authorities;
    public Set<ChatUserAuthority> authorities() {
        return authorities;
    }
    ChatUserRole(Collection<ChatUserAuthority> authorities) {
        this.authorities = Collections.unmodifiableSet(new HashSet<>(authorities));
    }
}
