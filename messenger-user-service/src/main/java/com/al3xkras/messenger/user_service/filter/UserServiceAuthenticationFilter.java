package com.al3xkras.messenger.user_service.filter;

import com.al3xkras.messenger.user_service.model.MessengerUserDetails;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
public class UserServiceAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    public UserServiceAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        log.info(username+" "+password);

        if (username==null || password==null || username.isEmpty() || password.isEmpty())
            throw new BadCredentialsException("invalid credentials");

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username,password);

        return authenticationManager.authenticate(token);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        MessengerUserDetails user = (MessengerUserDetails) authResult.getPrincipal();
        log.info(user.getUsername());
        //TODO remove hardcode
        Algorithm algorithm = Algorithm.HMAC256("secretStringHardcoded");
        String accessToken = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis()+1000L*60*10))
                .withIssuer(request.getRequestURI())
                .withClaim("roles",user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .withClaim("user-id", user.getMessengerUserId())
                .sign(algorithm);
        String refreshToken = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis()+1000L*60*60*24*30))
                .withIssuer(request.getRequestURI())
                .sign(algorithm);
        //TODO remove hardcode
        response.setHeader("access-token",accessToken);
        response.setHeader("refresh-token",refreshToken);
    }
}
