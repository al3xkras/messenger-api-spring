package com.al3xkras.messenger.chat_service.filter;

import com.al3xkras.messenger.model.MessengerUtils;
import com.al3xkras.messenger.model.authorities.ChatUserAuthority;
import com.al3xkras.messenger.model.security.ChatUserAuthenticationToken;
import com.al3xkras.messenger.model.security.JwtTokenAuth;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;

import static com.al3xkras.messenger.model.MessengerUtils.Messages.*;
import static com.al3xkras.messenger.model.security.JwtTokenAuth.Param.*;

@Slf4j
public class ChatServiceAuthorizationFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String uri = request.getRequestURI();

        if (uri.equals("/auth") || uri.equals("/error")){
            filterChain.doFilter(request,response);
            log.warn(String.format(WARNING_FILTER_IGNORED_FOR_REQUEST.value(),
                    ChatServiceAuthorizationFilter.class.getCanonicalName(),request.getRequestURI()));
            return;
        }

        String prefix = JwtTokenAuth.PREFIX_WITH_WHITESPACE;
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader==null){
            response.sendError(HttpStatus.FORBIDDEN.value());
            return;
        }

        ChatUserAuthenticationToken authToken;
        try {
            authToken = JwtTokenAuth.verifyChatUserToken(authHeader.substring(prefix.length()));
        } catch (Exception e){
            String message=String.format(EXCEPTION_AUTH_TOKEN_IS_INVALID.value(), MessengerUtils.Property.ENTITY_CHAT_USER.value());
            log.warn(message);
            log.error("auth",e);
            log.error(authHeader.substring(prefix.length()));
            response.sendError(HttpStatus.FORBIDDEN.value(),message);
            return;
        }

        Collection<GrantedAuthority> authorities = authToken.getAuthorities();

        HttpMethod method = HttpMethod.valueOf(request.getMethod().toUpperCase());
        String messageForbidden = String.format(EXCEPTION_FORBIDDEN_PATTERN.value(),method,uri);
        if (method.equals(HttpMethod.GET)){
            if (uri.equals("/chat") || uri.equals("/chat/users")){
                boolean readingSelfChatInfo;
                String chatIdParam = request.getParameter(CHAT_ID.value());
                String chatName = request.getParameter(CHAT_NAME.value());
                if (chatIdParam!=null){
                    long chatId;
                    try {
                        chatId = Long.parseLong(chatIdParam);
                    } catch (NumberFormatException e){
                        String message = String.format(EXCEPTION_ID_IS_INVALID.value(),CHAT_ID.value());
                        response.sendError(HttpStatus.BAD_REQUEST.value(),message);
                        log.warn(message+": "+chatIdParam);
                        return;
                    }
                    readingSelfChatInfo = chatId==authToken.getChatId();
                } else if (chatName!=null){
                    readingSelfChatInfo = chatName.equals(authToken.getChatName());
                } else {
                    String message = String.format(EXCEPTION_REQUIRED_PARAMETERS_ARE_NULL.value(),
                            String.join(", ",CHAT_ID.value(),CHAT_NAME.value()));
                    log.warn(message);
                    response.sendError(HttpStatus.BAD_REQUEST.value(),message);
                    return;
                }
                if (!((readingSelfChatInfo && authorities.contains(ChatUserAuthority.READ_SELF_CHATS_INFO)) ||
                        (!readingSelfChatInfo && authorities.contains(ChatUserAuthority.READ_ANY_CHATS_INFO_EXCEPT_SELF)))){
                    log.warn(messageForbidden);
                    log.warn(authorities.toString());
                    log.warn(authToken.toString());
                    log.warn(authToken.getUsername());
                    log.warn(authToken.getChatName());
                    response.sendError(HttpStatus.FORBIDDEN.value(),messageForbidden);
                    return;
                }
            } else if (uri.equals("/chats")){
                String username = request.getParameter(USERNAME.value());
                String userIdParam = request.getParameter(USER_ID.value());
                boolean readingSelfChats;
                if (userIdParam!=null){
                    long userId;
                    try {
                        userId = Long.parseLong(userIdParam);
                    } catch (NumberFormatException e){
                        String message = String.format(EXCEPTION_ID_IS_INVALID.value(),USER_ID.value());
                        log.warn(message+": "+userIdParam);
                        response.sendError(HttpStatus.BAD_REQUEST.value(),message);
                        return;
                    }
                    readingSelfChats = userId==authToken.getUserId();
                } else if (username!=null){
                    readingSelfChats = username.equals(authToken.getUsername());
                } else {
                    String message = String.format(EXCEPTION_REQUIRED_PARAMETERS_ARE_NULL.value(),
                            String.join(", ",USERNAME.value(),USER_ID.value()));
                    log.warn(message);
                    response.sendError(HttpStatus.BAD_REQUEST.value(),message);
                    return;
                }
                if (!((readingSelfChats && authorities.contains(ChatUserAuthority.READ_SELF_CHATS_INFO)) ||
                        (!readingSelfChats && authorities.contains(ChatUserAuthority.READ_ANY_CHATS_INFO_EXCEPT_SELF)))){
                    response.sendError(HttpStatus.FORBIDDEN.value(),messageForbidden);
                    return;
                }
            }
        } else if (method.equals(HttpMethod.PUT)){
            if (uri.equals("/chat")){
                if (!authorities.contains(ChatUserAuthority.MODIFY_CHAT_INFO)){
                    log.warn(messageForbidden);
                    response.sendError(HttpStatus.FORBIDDEN.value(),messageForbidden);
                    return;
                }
            } else if (uri.equals("/chat/users")){
                long chatId,userId;
                try {
                    chatId = Long.parseLong(request.getParameter(CHAT_ID.value()));
                    userId = Long.parseLong(request.getParameter(USER_ID.value()));
                } catch (RuntimeException e){
                    String message = String.format(EXCEPTION_ID_IS_INVALID.value(), MessengerUtils.Property.ENTITY_CHAT_USER.value(),request.getParameter(CHAT_ID.value()));
                    log.warn(message);
                    response.sendError(HttpStatus.BAD_REQUEST.value(),message);
                    return;
                }
                if (chatId!=authToken.getChatId()){
                    log.warn(messageForbidden);
                    response.sendError(HttpStatus.FORBIDDEN.value(),messageForbidden);
                    return;
                }
                boolean modifyingSelf = userId==authToken.getUserId();
                if (!((modifyingSelf && authorities.contains(ChatUserAuthority.MODIFY_SELF_INFO)) ||
                        (!modifyingSelf && authorities.contains(ChatUserAuthority.MODIFY_CHAT_USER_INFO_EXCEPT_SELF)))){
                    log.warn(messageForbidden);
                    response.sendError(HttpStatus.FORBIDDEN.value(),messageForbidden);
                    return;
                }
            }
        } else if (method.equals(HttpMethod.POST)){
            if (uri.equals("/chat")){
                if (!authorities.contains(ChatUserAuthority.CREATE_CHAT)){
                    log.warn(messageForbidden);
                    response.sendError(HttpStatus.FORBIDDEN.value(),messageForbidden);
                    return;
                }
            } else if (uri.equals("/chat/users")){
                if (!authorities.contains(ChatUserAuthority.ADD_CHAT_USER)){
                    log.warn(messageForbidden);
                    response.sendError(HttpStatus.FORBIDDEN.value(),messageForbidden);
                    return;
                }
            }
        } else if (method.equals(HttpMethod.DELETE)){
            if (uri.equals("/chat/users")){
                long chatId,userId;
                try {
                    chatId = Long.parseLong(request.getParameter(CHAT_ID.value()));
                    userId = Long.parseLong(request.getParameter(USER_ID.value()));
                } catch (RuntimeException e){
                    String message = String.format(EXCEPTION_ID_IS_INVALID.value(), MessengerUtils.Property.ENTITY_CHAT_USER.value(),"");
                    log.warn(message);
                    response.sendError(HttpStatus.BAD_REQUEST.value(),message);
                    return;
                }
                boolean deletingSelf = (chatId==authToken.getChatId() && userId==authToken.getUserId());
                if (!((deletingSelf && authorities.contains(ChatUserAuthority.DELETE_SELF)) ||
                        (!deletingSelf && authorities.contains(ChatUserAuthority.DELETE_ANYONE_EXCEPT_SELF)))){
                    log.warn(messageForbidden);
                    response.sendError(HttpStatus.FORBIDDEN.value(),messageForbidden);
                    return;
                }
            }
        }

        SecurityContextHolder.getContext().setAuthentication(authToken);
        filterChain.doFilter(request,response);
    }
}
