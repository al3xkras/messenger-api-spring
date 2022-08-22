package com.al3xkras.messenger.message_service.filter;

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

import static com.al3xkras.messenger.model.security.JwtTokenAuth.Param.*;

@Slf4j
public class MessageServiceAuthorizationFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String uri = request.getRequestURI();

        if (uri.equals("/auth") || uri.equals("/error")){
            filterChain.doFilter(request,response);
            log.info("authorization filter ignored for URI: "+uri);
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
            log.warn("invalid auth token: "+authHeader.substring(prefix.length()),e);
            response.sendError(HttpStatus.FORBIDDEN.value(),"bad auth token");
            return;
        }

        SecurityContextHolder.getContext().setAuthentication(authToken);
        filterChain.doFilter(request,response);
    }
}
