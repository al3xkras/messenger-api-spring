package com.al3xkras.messenger.model.security;

import com.al3xkras.messenger.model.ChatUserRole;
import com.al3xkras.messenger.model.MessengerUserType;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.ResourceBundle;

import static com.al3xkras.messenger.model.security.JwtTokenAuth.Param.*;

public class JwtTokenAuth {

    public static final long DEFAULT_TOKEN_EXPIRATION_TIME_MILLIS;
    public static final long DEFAULT_REFRESH_TOKEN_EXPIRATION_TIME_MILLIS;

    private static final ResourceBundle authBundle = ResourceBundle.getBundle("messenger-auth");
    private static final String jwtAuthSecret = authBundle.getString("jwt.secret");

    private static final String WHITESPACE = " ";
    public static final String PREFIX_WITH_WHITESPACE =  authBundle.getString("token.prefix")+WHITESPACE;

    static {
        DEFAULT_TOKEN_EXPIRATION_TIME_MILLIS = Long.parseLong(authBundle.getString("token.default-expiration-time-millis"));
        DEFAULT_REFRESH_TOKEN_EXPIRATION_TIME_MILLIS = Long.parseLong(authBundle.getString("refresh-token.default-expiration-time-millis"));
    }

    public enum Param {
        //User Service Auth parameters
        HEADER_REFRESH_TOKEN(authBundle.getString("header.refresh.token")),
        HEADER_ACCESS_TOKEN(authBundle.getString("header.access_token")),
        USERNAME(authBundle.getString("messenger_user.auth.username")),
        USER_ID(authBundle.getString("messenger_user.auth.id")),
        PASSWORD(authBundle.getString("messenger_user.auth.password")),
        //Chat Service Auth parameters
        USER_TOKEN(authBundle.getString("chat_user.auth.user_access_token")),
        CHAT_NAME(authBundle.getString("chat_user.auth.chat_name")),
        CHAT_ID(authBundle.getString("chat_user.auth.chat_id")),
        ROLES(authBundle.getString("claim.roles"));
        private final String value;
        public String value(){
            return value;
        }
        Param(String value) {
            this.value=value;
        }
    }

    public static MessengerUserAuthenticationToken verifyMessengerUserToken(String token) throws Exception {
        Algorithm algorithm = Algorithm.HMAC256(jwtAuthSecret);
        JWTVerifier jwtVerifier = JWT.require(algorithm).build();
        DecodedJWT decodedJWT = jwtVerifier.verify(token);
        String username = decodedJWT.getSubject();
        long id = decodedJWT.getClaim(USER_ID.value()).asLong();
        MessengerUserType messengerUserType = decodedJWT.getClaim(ROLES.value()).as(MessengerUserType.class);
        Collection<? extends GrantedAuthority> authorities = messengerUserType.authorities();
        MessengerUserAuthenticationToken authenticationToken = new MessengerUserAuthenticationToken(username,id,authorities);
        authenticationToken.setMessengerUserType(messengerUserType);
        return authenticationToken;
    }

    public static ChatUserAuthenticationToken verifyChatUserToken(String token) throws Exception {
        Algorithm algorithm = getJwtAuthAlgorithm();
        JWTVerifier jwtVerifier = JWT.require(algorithm).build();
        DecodedJWT decodedJWT = jwtVerifier.verify(token);
        String[] subject = decodedJWT.getSubject().split(WHITESPACE,2);
        String username = subject[1];
        String chatName = subject[0];
        long messengerUserId = decodedJWT.getClaim(USER_ID.value()).asLong();
        long chatId = decodedJWT.getClaim(CHAT_ID.value()).asLong();
        ChatUserRole role = decodedJWT.getClaim(ROLES.value()).as(ChatUserRole.class);
        Collection<? extends GrantedAuthority> authorities = role.authorities();
        ChatUserAuthenticationToken authenticationToken = new ChatUserAuthenticationToken(username,messengerUserId,chatName,authorities);
        authenticationToken.setChatId(chatId);
        authenticationToken.setChatUserRole(role);
        return authenticationToken;
    }

    public static Algorithm getJwtAuthAlgorithm(){
        return Algorithm.HMAC256(jwtAuthSecret);
    }
}
