package com.al3xkras.messenger.user_service.config;

import com.al3xkras.messenger.model.MessengerUserType;
import com.al3xkras.messenger.user_service.filter.UserServiceAuthenticationFilter;
import com.al3xkras.messenger.user_service.filter.UserServiceAuthorizationFilter;
import com.al3xkras.messenger.user_service.service.MessengerUserDetailsService;
import com.al3xkras.messenger.user_service.service.MessengerUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static com.al3xkras.messenger.model.MessengerUserType.*;

@Slf4j
@Configuration
@EnableWebSecurity
@Profile(value = {"default","security-test","no-security"})
public class SecurityConfig {

    @Autowired
    private MessengerUserService messengerUserService;

    @Value("${spring.profiles.active}")
    private String profile;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain configure(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {

        if (profile.equals("no-security")){
            http.csrf().disable().authorizeRequests().antMatchers("/**").permitAll();
            log.warn("spring security is disabled!");
            return http.build();
        }

        UserServiceAuthenticationFilter userServiceAuthenticationFilter = new UserServiceAuthenticationFilter(authenticationManager);
        UserServiceAuthorizationFilter userServiceAuthorizationFilter = new UserServiceAuthorizationFilter();

        userServiceAuthenticationFilter.setFilterProcessesUrl("/user/login");

        http    .csrf().disable()
                .authorizeRequests()

                //configured in Authorization filter
                .antMatchers(HttpMethod.GET,"/user/{id}").authenticated()
                .antMatchers(HttpMethod.GET,"/user").authenticated()
                .antMatchers(HttpMethod.PUT,"/user").authenticated()
                .antMatchers(HttpMethod.DELETE,"/user").authenticated()

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
