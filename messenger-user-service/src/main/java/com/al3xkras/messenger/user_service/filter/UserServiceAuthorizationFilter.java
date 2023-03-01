package com.al3xkras.messenger.user_service.filter;

import com.al3xkras.messenger.model.authorities.MessengerUserAuthority;
import com.al3xkras.messenger.model.security.JwtTokenAuth;
import com.al3xkras.messenger.model.security.MessengerUserAuthenticationToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
import static com.al3xkras.messenger.model.security.JwtTokenAuth.Param.USERNAME;
import static com.al3xkras.messenger.model.security.JwtTokenAuth.Param.USER_ID;

@Slf4j
public class UserServiceAuthorizationFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        if (requestURI.equals("/user/login") ||
                (request.getMethod().equalsIgnoreCase("post") && requestURI.equals("/user"))){
            log.warn(String.format(WARNING_FILTER_IGNORED_FOR_REQUEST.value(),
                    UserServiceAuthorizationFilter.class.getCanonicalName(),request.getRequestURI()));
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader==null){
            log.warn(EXCEPTION_AUTHORIZE.value());
            response.sendError(HttpStatus.FORBIDDEN.value(), EXCEPTION_AUTHORIZE.value());
            return;
        }

        String prefix = JwtTokenAuth.PREFIX_WITH_WHITESPACE;
        if (!authHeader.startsWith(prefix)) {
            log.warn("invalid access token: "+authHeader);
            response.sendError(HttpStatus.BAD_REQUEST.value(), EXCEPTION_AUTHORIZE.value());
            return;
        }

        MessengerUserAuthenticationToken authResult;
        try {
            String token = authHeader.substring(prefix.length());
            authResult = JwtTokenAuth.verifyMessengerUserToken(token);
        } catch (Exception e){
            log.warn(EXCEPTION_AUTHORIZE.value(),e);
            response.sendError(HttpStatus.BAD_REQUEST.value(), EXCEPTION_AUTHORIZE.value());
            return;
        }

        HttpMethod method = HttpMethod.valueOf(request.getMethod().toUpperCase());
        Collection<GrantedAuthority> authorities = authResult.getAuthorities();

        String messageForbidden = "forbidden: ("+method+") "+requestURI;
        if (method.equals(HttpMethod.GET)){
            if (requestURI.equals("/user")){
                String usernameParam = request.getParameter(USERNAME.value());
                if (usernameParam==null){
                    response.sendError(HttpStatus.BAD_REQUEST.value(),
                            String.format(EXCEPTION_PARAMETER_NOT_SPECIFIED_PATTERN.value(),USERNAME.value()));
                    return;
                }
                boolean readingSelfInfo = usernameParam.equals(authResult.getUsername());
                if (!((readingSelfInfo && authorities.contains(MessengerUserAuthority.READ_SELF_INFO)) ||
                        (!readingSelfInfo && authorities.contains(MessengerUserAuthority.READ_ANY_USER_INFO_EXCEPT_SELF)))
                    ){
                    log.warn(messageForbidden);
                    response.sendError(HttpStatus.FORBIDDEN.value(), messageForbidden);
                    return;
                }

            } else if (requestURI.matches("^/user/\\d{1,20}$")){
                long messengerUserId = Long.parseLong(requestURI.substring("/user/".length()));
                boolean readingSelfInfo = messengerUserId==authResult.getMessengerUserId();
                if (!((readingSelfInfo && authorities.contains(MessengerUserAuthority.READ_SELF_INFO)) ||
                        (!readingSelfInfo && authorities.contains(MessengerUserAuthority.READ_ANY_USER_INFO_EXCEPT_SELF)))
                ){
                    log.warn(messageForbidden);
                    response.sendError(HttpStatus.FORBIDDEN.value(), messageForbidden);
                    return;
                }
            }

        } else if (method.equals(HttpMethod.PUT)){
            if (requestURI.equals("/user")){
                String userIdParam = request.getParameter(USER_ID.value());
                String usernameParam = request.getParameter(USERNAME.value());
                Boolean modifyingSelf = isModifyingSelf(request,response,userIdParam,usernameParam,authResult);
                if (modifyingSelf==null)
                    return;
                if (!((modifyingSelf && authorities.contains(MessengerUserAuthority.MODIFY_SELF)) ||
                        (!modifyingSelf && authorities.contains(MessengerUserAuthority.MODIFY_ANY_USER_EXCEPT_SELF)))){
                    log.warn(messageForbidden);
                    response.sendError(HttpStatus.FORBIDDEN.value(), messageForbidden);
                    return;
                }

            }

        } else if (method.equals(HttpMethod.POST)){
            //(POST) method for /user does not require authorization
            log.warn("filter is disabled for "+HttpMethod.POST);
        } else if (method.equals(HttpMethod.DELETE)){
            if (requestURI.equals("/user")){
                String userIdParam = request.getParameter(USER_ID.value());
                String usernameParam = request.getParameter(USERNAME.value());
                Boolean deletingSelf = isDeletingSelf(request,response,userIdParam,usernameParam,authResult);
                if (deletingSelf==null)
                    return;

                if (!((deletingSelf && authorities.contains(MessengerUserAuthority.DELETE_SELF)) ||
                        (!deletingSelf && authorities.contains(MessengerUserAuthority.DELETE_ANY_USER_EXCEPT_SELF)))){
                    log.warn(messageForbidden);
                    response.sendError(HttpStatus.FORBIDDEN.value(), messageForbidden);
                    return;
                }
            }
        }
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(authResult.getUsername(),null,authResult.getAuthorities()));
        filterChain.doFilter(request,response);
    }

    private Boolean isModifyingSelf(HttpServletRequest request, HttpServletResponse response, String userIdParam,String usernameParam,
                                    MessengerUserAuthenticationToken authResult) throws IOException {
        if (userIdParam!=null){
            long messengerUserId;
            try {
                messengerUserId = Long.parseLong(userIdParam);
            } catch (NumberFormatException e){
                response.sendError(HttpStatus.BAD_REQUEST.value());
                return null;
            }
            return messengerUserId==authResult.getMessengerUserId();
        } else if (usernameParam!=null){
            return usernameParam.equals(authResult.getUsername());
        } else {
            response.sendError(HttpStatus.BAD_REQUEST.value(), String.format(EXCEPTION_REQUIRED_PARAMETERS_ARE_NULL.value(),
                    String.join(",", USERNAME.value(), USER_ID.value())));
            return null;
        }
    }

    private Boolean isDeletingSelf(HttpServletRequest request, HttpServletResponse response, String userIdParam,String usernameParam,
                                   MessengerUserAuthenticationToken authResult) throws IOException {
        return isModifyingSelf(request,response,userIdParam,usernameParam,authResult);
    }
}
