package com.al3xkras.messenger.chat_service.filter;

import com.al3xkras.messenger.entity.ChatUser;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
public class ChatServiceAuthorizationFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String uri = request.getRequestURI();

        if (uri.equals("/auth")){
            filterChain.doFilter(request,response);
            return;
        }

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader==null){
            response.sendError(HttpStatus.FORBIDDEN.value());
            return;
        }

        //TODO remove hardcode
        String prefix = "Bearer ";
        if (authHeader.startsWith(prefix)){
            String username;
            long userId;
            Collection<SimpleGrantedAuthority> authorities;
            try {
                String token = authHeader.substring(prefix.length());

                //TODO remove hardcode
                Algorithm algorithm = Algorithm.HMAC256("secretStringHardcoded");
                JWTVerifier jwtVerifier = JWT.require(algorithm).build();
                DecodedJWT decodedJWT = jwtVerifier.verify(token);
                username = decodedJWT.getSubject();
                //TODO remove hardcode
                String[] roles = decodedJWT.getClaim("roles").asArray(String.class);
                userId = decodedJWT.getClaim("user-id").asLong();
                log.info(Arrays.toString(roles));
                authorities = Arrays.stream(roles)
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());
            } catch (Exception e){
                log.info(e.toString());
                //TODO remove hardcode
                response.getWriter().write("authorization error");
                response.sendError(HttpStatus.BAD_REQUEST.value());
                return;
            }
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(username,null,authorities);
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            if (request.getMethod().equalsIgnoreCase("get") &&
                    uri.equals("/chat")){
            }
        }
        filterChain.doFilter(request,response);
    }
}
