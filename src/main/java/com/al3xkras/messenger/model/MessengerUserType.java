package com.al3xkras.messenger.model;

import com.al3xkras.messenger.model.authorities.MessengerUserAuthority;

import java.util.*;

import static com.al3xkras.messenger.model.authorities.MessengerUserAuthority.*;

public enum MessengerUserType {
    ANONYMOUS(Collections.singletonList(ADD_USER)),
    ADMIN(Arrays.asList(ADD_USER,READ_SELF_INFO,READ_ANY_USER_INFO_EXCEPT_SELF,MODIFY_SELF,MODIFY_ANY_USER_EXCEPT_SELF,
            DELETE_ANY_USER_EXCEPT_SELF,CREATE_CHAT,VIEW_PUBLIC_CHATS,VIEW_PRIVATE_CHATS
    )),
    USER(Arrays.asList(MODIFY_SELF,READ_SELF_INFO,DELETE_SELF,
            CREATE_CHAT,VIEW_PUBLIC_CHATS,VIEW_SELF_CHATS
    ));
    private final Set<MessengerUserAuthority> authorities;

    public Set<MessengerUserAuthority> authorities() {
        return authorities;
    }
    MessengerUserType(Collection<MessengerUserAuthority> authorities){
        this.authorities = Collections.unmodifiableSet(new HashSet<>(authorities));
    }
}
