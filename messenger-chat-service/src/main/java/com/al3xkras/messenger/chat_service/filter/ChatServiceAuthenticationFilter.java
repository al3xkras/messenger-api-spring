package com.al3xkras.messenger.chat_service.filter;

import com.al3xkras.messenger.chat_service.exception.ChatNotFoundException;
import com.al3xkras.messenger.chat_service.exception.ChatServiceAuthenticationException;
import com.al3xkras.messenger.chat_service.exception.ChatUserNotFoundException;
import com.al3xkras.messenger.chat_service.model.ChatUserAuthenticationToken;
import com.al3xkras.messenger.chat_service.service.ChatService;
import com.al3xkras.messenger.entity.ChatUser;
import com.al3xkras.messenger.model.ChatUserId;
import com.al3xkras.messenger.model.ChatUserRole;
import com.al3xkras.messenger.model.MessengerUserType;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class ChatServiceAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private final ChatService chatService;

    public ChatServiceAuthenticationFilter(String url, ChatService chatService) {
        super(url);
        this.chatService = chatService;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        if (request.getRequestURI().equals("/error")){
            filterChain.doFilter(req,res);
            return;
        }
        HttpServletResponse response = (HttpServletResponse) res;

        ChatUserAuthenticationToken authToken;
        try {
            authToken = (ChatUserAuthenticationToken) attemptAuthentication(request,response);
        } catch (AuthenticationException failed){
            unsuccessfulAuthentication(request,response,failed);
            return;
        }
        long chatId;
        try {
            chatId=chatService.getChatIdByChatName(authToken.getChatName());
        }catch (ChatNotFoundException e){
            log.warn("chat not found: \""+authToken.getChatName()+'"');
            unsuccessfulAuthentication(request, response, new ChatServiceAuthenticationException("chat not found: \""+authToken.getChatName()+'"'));
            return;
        }
        ChatUser chatUser;
        try {
            ChatUserId id = new ChatUserId(chatId,authToken.getUserId());
            chatUser = chatService.findChatUserById(id);
        } catch (ChatUserNotFoundException e){
            response.sendError(HttpStatus.FORBIDDEN.value());
            return;
        }
        authToken.setChatId(chatId);
        authToken.getAuthorities().add(new SimpleGrantedAuthority(chatUser.getChatUserRole().name()));
        successfulAuthentication(request,response,filterChain,authToken);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String userAuth = request.getParameter("user-auth-token");
        String chatName = request.getParameter("chat-name");

        if (userAuth==null || userAuth.isEmpty() ||
                chatName==null || chatName.isEmpty()) {
            log.info("auth failed");
            throw new BadCredentialsException("invalid credentials");
        }

        String username;
        List<String> roles;
        long id;
        Collection<SimpleGrantedAuthority> authorities = new HashSet<>();
        //TODO remove hardcode
        String prefix = "Bearer ";
        if (userAuth.startsWith(prefix)) {
            String token = userAuth.substring(prefix.length());

            //TODO remove hardcode
            Algorithm algorithm = Algorithm.HMAC256("secretStringHardcoded");
            JWTVerifier jwtVerifier = JWT.require(algorithm).build();
            try {
                DecodedJWT decodedJWT = jwtVerifier.verify(token);
                username = decodedJWT.getSubject();
                roles = decodedJWT.getClaim("roles").asList(String.class);
                id = decodedJWT.getClaim("user-id").asLong();
            } catch (Exception e){
                log.info("invalid token (1)");
                throw new BadCredentialsException("invalid user auth token");
            }
        } else {
            log.info("invalid token (2)");
            throw new BadCredentialsException("invalid user auth token");
        }

        if (roles.contains(MessengerUserType.ADMIN.name())){
            authorities.add(new SimpleGrantedAuthority(ChatUserRole.ADMIN.name()));
        }

        return new ChatUserAuthenticationToken(username,id,chatName,authorities);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain, Authentication authResult) {
        ChatUserAuthenticationToken token = (ChatUserAuthenticationToken) authResult;
        //TODO remove hardcode
        Algorithm algorithm = Algorithm.HMAC256("secretStringHardcoded");
        String accessToken = JWT.create()
                .withSubject(token.getChatName()+":"+token.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis()+1000L*60*10))
                .withIssuer(request.getRequestURI())
                .withClaim("roles",token.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .withClaim("chat-id", token.getChatId())
                .withClaim("user-id", token.getUserId())
                .sign(algorithm);
        String refreshToken = JWT.create()
                .withSubject(token.getChatName()+":"+token.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis()+1000L*60*60*24*30))
                .withIssuer(request.getRequestURI())
                .sign(algorithm);
        //TODO remove hardcode
        response.setHeader("access-token",accessToken);
        response.setHeader("refresh-token",refreshToken);
    }
}
