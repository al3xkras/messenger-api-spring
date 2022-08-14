package com.al3xkras.messenger.user_service.config;

import com.al3xkras.messenger.model.MessengerUserType;
import com.al3xkras.messenger.user_service.exception.MessengerUserNotFoundException;
import com.al3xkras.messenger.user_service.filter.UserServiceAuthenticationFilter;
import com.al3xkras.messenger.user_service.filter.UserServiceAuthorizationFilter;
import com.al3xkras.messenger.user_service.service.MessengerUserDetailsService;
import com.al3xkras.messenger.user_service.service.MessengerUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private MessengerUserService messengerUserService;

    @Bean
    public SecurityFilterChain configure(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {

        UserServiceAuthenticationFilter userServiceAuthenticationFilter = new UserServiceAuthenticationFilter(authenticationManager);
        UserServiceAuthorizationFilter userServiceAuthorizationFilter = new UserServiceAuthorizationFilter();

        userServiceAuthenticationFilter.setFilterProcessesUrl("/user/login");

        http    .csrf().disable()
                .authorizeRequests()
                .antMatchers("/user/**").hasAnyAuthority(MessengerUserType.USER.name(),MessengerUserType.ADMIN.name())
                .antMatchers("/user/login").permitAll()
                .antMatchers(HttpMethod.POST,"/user").permitAll()
                .and()
                .authorizeRequests().anyRequest().authenticated()
                .and()
                .addFilter(userServiceAuthenticationFilter)
                .addFilterBefore(userServiceAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService(){
        return new MessengerUserDetailsService(messengerUserService);
    }



}
