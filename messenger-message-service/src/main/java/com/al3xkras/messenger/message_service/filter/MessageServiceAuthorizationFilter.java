package com.al3xkras.messenger.message_service.filter;

import com.al3xkras.messenger.model.security.ChatUserAuthenticationToken;
import com.al3xkras.messenger.model.security.JwtTokenAuth;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.al3xkras.messenger.model.MessengerUtils.Messages;
import static com.al3xkras.messenger.model.MessengerUtils.Property;

@Slf4j
public class MessageServiceAuthorizationFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String uri = request.getRequestURI();

        if (uri.equals("/auth") || uri.equals("/error")){
            filterChain.doFilter(request,response);
            log.info(Messages.WARNING_FILTER_IGNORED_FOR_REQUEST.value(),MessageServiceAuthorizationFilter.class.getSimpleName(),request.getRequestURI());
            return;
        }

        String prefix = JwtTokenAuth.PREFIX_WITH_WHITESPACE;
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        log.info("auth header:"+authHeader);
        if (authHeader==null){
            response.sendError(HttpStatus.FORBIDDEN.value());
            return;
        }

        ChatUserAuthenticationToken authToken;
        try {
            authToken = JwtTokenAuth.verifyChatUserToken(authHeader.substring(prefix.length()));
            System.out.println("authorities: "+authToken.getAuthorities());
        } catch (Exception e){
            String message = String.format(Messages.EXCEPTION_AUTH_TOKEN_IS_INVALID.value(), Property.USER_SERVICE_NAME.value());
            log.warn(message+": "+authHeader.substring(prefix.length()),e);
            response.sendError(HttpStatus.FORBIDDEN.value(), message);
            return;
        }

        SecurityContextHolder.getContext().setAuthentication(authToken);
        filterChain.doFilter(request,response);
    }
}
