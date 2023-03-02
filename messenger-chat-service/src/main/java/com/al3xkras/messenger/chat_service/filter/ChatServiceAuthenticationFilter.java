package com.al3xkras.messenger.chat_service.filter;

import com.al3xkras.messenger.chat_service.exception.ChatNotFoundException;
import com.al3xkras.messenger.chat_service.exception.ChatServiceAuthenticationException;
import com.al3xkras.messenger.chat_service.exception.ChatUserNotFoundException;
import com.al3xkras.messenger.chat_service.service.ChatService;
import com.al3xkras.messenger.entity.ChatUser;
import com.al3xkras.messenger.model.ChatUserId;
import com.al3xkras.messenger.model.ChatUserRole;
import com.al3xkras.messenger.model.MessengerUserType;
import com.al3xkras.messenger.model.security.ChatUserAuthenticationToken;
import com.al3xkras.messenger.model.security.JwtTokenAuth;
import com.al3xkras.messenger.model.security.MessengerUserAuthenticationToken;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.al3xkras.messenger.model.MessengerUtils.Messages.*;
import static com.al3xkras.messenger.model.security.JwtTokenAuth.Param.*;

@Slf4j
public class ChatServiceAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private final ChatService chatService;
    private String filterProcessesUrl;
    public ChatServiceAuthenticationFilter(String filterProcessesUrl, ChatService chatService) {
        super(filterProcessesUrl);
        this.filterProcessesUrl = filterProcessesUrl;
        this.chatService = chatService;
    }

    @Override
    public void setFilterProcessesUrl(String filterProcessesUrl) {
        super.setFilterProcessesUrl(filterProcessesUrl);
        this.filterProcessesUrl=filterProcessesUrl;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        if (!request.getRequestURI().equals(filterProcessesUrl)){
            log.warn(String.format(WARNING_FILTER_IGNORED_FOR_REQUEST.value(),
                    ChatServiceAuthenticationFilter.class.getCanonicalName(),request.getRequestURI()));
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

        boolean isAnonymousChatUserAuth = authToken.getChatName().equals(CHAT_NAME.value());

        long chatId = -1L;
        ChatUser chatUser = null;
        if (!isAnonymousChatUserAuth){
            try {
                chatId=chatService.getChatIdByChatName(authToken.getChatName());
            }catch (ChatNotFoundException e){
                String message = String.format(EXCEPTION_CHAT_NOT_FOUND.value(),authToken.getChatName());
                log.warn(message);
                unsuccessfulAuthentication(request, response, new ChatServiceAuthenticationException(
                        message));
                return;
            }
            try {
                ChatUserId id = new ChatUserId(chatId,authToken.getUserId());
                chatUser = chatService.findChatUserById(id);
            } catch (ChatUserNotFoundException e){
                log.warn("chat user not found:" +
                        " chat: "+authToken.getChatName()+
                        " user: "+authToken.getUsername());
                response.sendError(HttpStatus.FORBIDDEN.value(), EXCEPTION_CHAT_USER_NOT_FOUND.value());
                return;
            }
        }

        ChatUserAuthenticationToken authResult = new ChatUserAuthenticationToken(
                authToken.getUsername(),authToken.getUserId(),authToken.getChatName(),Collections.emptyList());
        if (!isAnonymousChatUserAuth)
            authResult.setChatId(chatId);
        if (!isAnonymousChatUserAuth && authToken.getChatUserRole()==null){
            authResult.setChatUserRole(chatUser.getChatUserRole());
        } else {
            authResult.setChatUserRole(authToken.getChatUserRole());
        }

        successfulAuthentication(request,response,filterChain,authResult);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String userAuth = request.getParameter(USER_TOKEN.value());
        String chatName = request.getParameter(CHAT_NAME.value());

        if (userAuth==null || userAuth.isEmpty()) {
            String message = String.format(EXCEPTION_AUTH_TOKEN_IS_NULL.value(),USER_TOKEN.value());
            log.warn(message);
            throw new BadCredentialsException(message);
        }
        if (chatName==null || chatName.isEmpty()){
            chatName = CHAT_NAME.value();
        }

        String prefix = JwtTokenAuth.PREFIX_WITH_WHITESPACE;
        if (!userAuth.startsWith(prefix)) {
            String message = String.format(EXCEPTION_AUTH_TOKEN_IS_INVALID.value(),USER_TOKEN.value());
            log.warn(message);
            log.error(userAuth);
            throw new BadCredentialsException(message);
        }

        MessengerUserAuthenticationToken userAuthToken;
        try {
            String token = userAuth.substring(prefix.length());
            userAuthToken = JwtTokenAuth.verifyMessengerUserToken(token);
        } catch (Exception e){
            String message = String.format(EXCEPTION_AUTH_TOKEN_IS_INVALID.value(),USER_TOKEN.value());
            log.warn(message);
            log.error("auth",e);
            throw new BadCredentialsException(message);
        }

        ChatUserAuthenticationToken chatUserAuthenticationToken =
                new ChatUserAuthenticationToken(userAuthToken.getUsername(),userAuthToken.getMessengerUserId(),chatName,Collections.emptyList());
        if (chatName.equals(CHAT_NAME.value())){
            chatUserAuthenticationToken.setChatUserRole(ChatUserRole.ANONYMOUS);
        } else if (userAuthToken.getMessengerUserType().equals(MessengerUserType.ADMIN)){
            chatUserAuthenticationToken.setChatUserRole(ChatUserRole.SUPER_ADMIN);
        }
        return chatUserAuthenticationToken;
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain filterChain, Authentication authResult) throws IOException {
        ChatUserAuthenticationToken token = (ChatUserAuthenticationToken) authResult;
        Algorithm algorithm = JwtTokenAuth.getJwtAuthAlgorithm();
        String subject = token.getChatName()+' '+token.getUsername();
        String accessToken = JWT.create()
                .withSubject(subject)
                .withExpiresAt(new Date(System.currentTimeMillis()+JwtTokenAuth.DEFAULT_TOKEN_EXPIRATION_TIME_MILLIS))
                .withIssuer(request.getRequestURI())
                .withClaim(ROLES.value(),token.getChatUserRole().name())
                .withClaim(CHAT_ID.value(), token.getChatId())
                .withClaim(USER_ID.value(), token.getUserId())
                .sign(algorithm);
        String refreshToken = JWT.create()
                .withSubject(subject)
                .withExpiresAt(new Date(System.currentTimeMillis()+JwtTokenAuth.DEFAULT_REFRESH_TOKEN_EXPIRATION_TIME_MILLIS))
                .withIssuer(request.getRequestURI())
                .sign(algorithm);

        response.addHeader(HEADER_ACCESS_TOKEN.value(),accessToken);
        response.addHeader(HEADER_REFRESH_TOKEN.value(), refreshToken);

        Map<String,String> body = new HashMap<>();
        body.put(HEADER_ACCESS_TOKEN.value(), accessToken);
        body.put(HEADER_REFRESH_TOKEN.value(),refreshToken);
        String json = new ObjectMapper().writeValueAsString(body);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().print(json);
        response.getWriter().flush();
    }
}
