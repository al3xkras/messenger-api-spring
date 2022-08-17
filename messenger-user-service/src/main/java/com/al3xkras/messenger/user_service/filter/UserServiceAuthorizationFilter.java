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

import static com.al3xkras.messenger.model.security.JwtTokenAuth.Param.USERNAME;
import static com.al3xkras.messenger.model.security.JwtTokenAuth.Param.USER_ID;

@Slf4j
public class UserServiceAuthorizationFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        if (requestURI.equals("/user/login") ||
                (request.getMethod().equalsIgnoreCase("post") && requestURI.equals("/user"))){
            log.info("authorization filter ignored for url: "+requestURI+" method:"+request.getMethod());
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader==null){
            log.warn("unauthorized request");
            response.sendError(HttpStatus.FORBIDDEN.value(),"unauthorized request");
            return;
        }

        String prefix = JwtTokenAuth.PREFIX_WITH_WHITESPACE;
        if (!authHeader.startsWith(prefix)) {
            log.warn("invalid access token: "+authHeader);
            response.sendError(HttpStatus.BAD_REQUEST.value(),"invalid access token");
            return;
        }

        MessengerUserAuthenticationToken authResult;
        try {
            String token = authHeader.substring(prefix.length());
            authResult = JwtTokenAuth.verifyMessengerUserToken(token);
        } catch (Exception e){
            log.warn("authorization error: "+e.getMessage());
            response.sendError(HttpStatus.BAD_REQUEST.value(),"authorization error");
            return;
        }

        HttpMethod method = HttpMethod.valueOf(request.getMethod().toUpperCase());
        Collection<GrantedAuthority> authorities = authResult.getAuthorities();

        if (method.equals(HttpMethod.GET)){
            if (requestURI.equals("/user")){
                String usernameParam = request.getParameter(USERNAME.value());
                if (usernameParam==null){
                    response.sendError(HttpStatus.BAD_REQUEST.value(),"\""+USERNAME.value()+"\" param is not specified");
                    return;
                }
                boolean readingSelfInfo = usernameParam.equals(authResult.getUsername());
                if (!((readingSelfInfo && authorities.contains(MessengerUserAuthority.READ_SELF_INFO)) ||
                        (!readingSelfInfo && authorities.contains(MessengerUserAuthority.READ_ANY_USER_INFO_EXCEPT_SELF)))
                    ){
                    String message = "forbidden: (GET) /user for "+USERNAME.value()+" \""+usernameParam+"\"";
                    log.warn(message);
                    response.sendError(HttpStatus.FORBIDDEN.value(), message);
                    return;
                }

            } else if (requestURI.matches("^/user/\\d{1,20}$")){
                long messengerUserId = Long.parseLong(requestURI.substring("/user/".length()));
                boolean readingSelfInfo = messengerUserId==authResult.getMessengerUserId();
                if (!((readingSelfInfo && authorities.contains(MessengerUserAuthority.READ_SELF_INFO)) ||
                        (!readingSelfInfo && authorities.contains(MessengerUserAuthority.READ_ANY_USER_INFO_EXCEPT_SELF)))
                ){
                    String message = "forbidden: (GET) /user, "+USER_ID.value()+": "+messengerUserId;
                    log.warn(message);
                    response.sendError(HttpStatus.FORBIDDEN.value(), message);
                    return;
                }
            }

        } else if (method.equals(HttpMethod.PUT)){
            if (requestURI.equals("/user")){
                String userIdParam = request.getParameter("user-id");
                String usernameParam = request.getParameter("username");
                Boolean modifyingSelf = isModifyingSelf(request,response,userIdParam,usernameParam,authResult);
                if (modifyingSelf==null)
                    return;
                if (!((modifyingSelf && authorities.contains(MessengerUserAuthority.MODIFY_SELF)) ||
                        (!modifyingSelf && authorities.contains(MessengerUserAuthority.MODIFY_ANY_USER_EXCEPT_SELF)))){
                    log.warn("forbidden: (PUT) /user");
                    response.sendError(HttpStatus.FORBIDDEN.value(), "forbidden: (PUT) /user");
                    return;
                }

            }

        } else if (method.equals(HttpMethod.POST)){
            //(POST) method for /user does not require authorization
            log.warn("post method is unauthorized");
        } else if (method.equals(HttpMethod.DELETE)){
            if (requestURI.equals("/user")){
                String userIdParam = request.getParameter("user-id");
                String usernameParam = request.getParameter("username");
                Boolean deletingSelf = isDeletingSelf(request,response,userIdParam,usernameParam,authResult);
                if (deletingSelf==null)
                    return;

                if (!((deletingSelf && authorities.contains(MessengerUserAuthority.DELETE_SELF)) ||
                        (!deletingSelf && authorities.contains(MessengerUserAuthority.DELETE_ANY_USER_EXCEPT_SELF)))){
                    log.warn("forbidden: (DELETE) /user");
                    response.sendError(HttpStatus.FORBIDDEN.value(), "forbidden: (DELETE) /user");
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
                response.sendError(HttpStatus.BAD_REQUEST.value(),"invalid user id");
                return null;
            }
            return messengerUserId==authResult.getMessengerUserId();
        } else if (usernameParam!=null){
            return usernameParam.equals(authResult.getUsername());
        } else {
            response.sendError(HttpStatus.BAD_REQUEST.value(),"please specify \"username\" or \"user-id\"");
            return null;
        }
    }

    private Boolean isDeletingSelf(HttpServletRequest request, HttpServletResponse response, String userIdParam,String usernameParam,
                                   MessengerUserAuthenticationToken authResult) throws IOException {
        return isModifyingSelf(request,response,userIdParam,usernameParam,authResult);
    }
}
