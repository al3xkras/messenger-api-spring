package com.al3xkras.messenger.user_service.filter;

import com.al3xkras.messenger.model.MessengerUtils;
import com.al3xkras.messenger.user_service.model.MessengerUserDetails;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.al3xkras.messenger.model.security.JwtTokenAuth.*;
import static com.al3xkras.messenger.model.security.JwtTokenAuth.Param.*;

@Slf4j
public class UserServiceAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    public UserServiceAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String username = request.getParameter(USERNAME.value());
        String password = request.getParameter(PASSWORD.value());

        log.info("username: "+username);
        log.info("password: "+password);
        if (username==null || password==null || username.isEmpty() || password.isEmpty())
            throw new BadCredentialsException(MessengerUtils.Messages.EXCEPTION_AUTHORIZE.value());

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username,password);

        return authenticationManager.authenticate(token);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        MessengerUserDetails user = (MessengerUserDetails) authResult.getPrincipal();
        Algorithm algorithm = getJwtAuthAlgorithm();
        String accessToken = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis()+ DEFAULT_TOKEN_EXPIRATION_TIME_MILLIS))
                .withIssuer(request.getRequestURI())
                .withClaim(ROLES.value(), user.getMessengerUserType().name())
                .withClaim(USER_ID.value(), user.getMessengerUserId())
                .sign(algorithm);
        String refreshToken = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis()+ DEFAULT_REFRESH_TOKEN_EXPIRATION_TIME_MILLIS))
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
