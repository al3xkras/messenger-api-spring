package com.al3xkras.messenger.user_service.filter;

import com.al3xkras.messenger.model.MessengerUserType;
import com.al3xkras.messenger.user_service.model.MessengerUserDetails;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
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
public class UserServiceAuthorizationFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        if (requestURI.equals("/user/login") ||
                (request.getMethod().equalsIgnoreCase("post") && requestURI.equals("/user"))){
            log.info("authorization filter ignored");
            filterChain.doFilter(request, response);
            return;
        }

        log.info("authorization filter NOT ignored");
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader==null){
            response.sendError(HttpStatus.FORBIDDEN.value());
            return;
        }

        //TODO remove hardcode
        String prefix = "Bearer ";
        if (authHeader.startsWith(prefix)){
            try {
                String token = authHeader.substring(prefix.length());

                //TODO remove hardcode
                Algorithm algorithm = Algorithm.HMAC256("secretStringHardcoded");
                JWTVerifier jwtVerifier = JWT.require(algorithm).build();
                DecodedJWT decodedJWT = jwtVerifier.verify(token);
                String username = decodedJWT.getSubject();
                //TODO remove hardcode
                String[] roles = decodedJWT.getClaim("roles").asArray(String.class);
                long id = decodedJWT.getClaim("user-id").asLong();
                log.info(Arrays.toString(roles));
                Collection<SimpleGrantedAuthority> authorities = Arrays.stream(roles)
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

                if (requestURI.startsWith("/user") && !authorities.contains(new SimpleGrantedAuthority(MessengerUserType.ADMIN.name()))){
                    if (request.getMethod().equalsIgnoreCase("get") && requestURI.equals("/user")){
                        String usernameParam = request.getParameter("username");
                        if (usernameParam==null){
                            response.getWriter().write("\"username\" param is not specified");
                            response.sendError(HttpStatus.BAD_REQUEST.value());
                            return;
                        }
                        if (!usernameParam.equals(username)){
                            response.sendError(HttpStatus.FORBIDDEN.value());
                            return;
                        }
                    }
                    if ((request.getMethod().equalsIgnoreCase("put") || request.getMethod().equalsIgnoreCase("delete")) &&
                            requestURI.equals("/user")){

                        String userIdParam = request.getParameter("user-id");
                        String usernameParam = request.getParameter("username");
                        if (userIdParam!=null && Long.parseLong(userIdParam)!=id){
                            response.sendError(HttpStatus.FORBIDDEN.value());
                            return;
                        }
                        if (usernameParam!=null && !usernameParam.equals(username)){
                            response.sendError(HttpStatus.FORBIDDEN.value());
                            return;
                        }
                    }
                    if (requestURI.matches("^/user/\\d{1,20}$") &&
                            Long.parseLong(requestURI.substring("/user/".length()))!=id){
                        response.sendError(HttpStatus.FORBIDDEN.value());
                        return;
                    }
                }


                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(username,null,authorities);
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            } catch (Exception e){
                log.info(e.toString());
                //TODO remove hardcode
                response.getWriter().write("authorization error");
                response.sendError(HttpStatus.BAD_REQUEST.value());
                return;
            }
        }
        filterChain.doFilter(request,response);
    }
}
