package com.al3xkras.messenger.user_service.filter;

import com.al3xkras.messenger.model.MessengerResponse;
import com.al3xkras.messenger.user_service.model.MessengerUserDetails;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
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

        if (username==null || password==null || username.isEmpty() || password.isEmpty())
            throw new BadCredentialsException(MessengerResponse.Messages.EXCEPTION_AUTHORIZE.value());

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

        response.setHeader(HEADER_ACCESS_TOKEN.value(), accessToken);
        response.setHeader(HEADER_REFRESH_TOKEN.value(),refreshToken);
    }
}
