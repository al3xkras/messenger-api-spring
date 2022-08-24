package com.al3xkras.messenger.message_service.config;

import com.al3xkras.messenger.message_service.filter.MessageServiceAuthorizationFilter;
import com.al3xkras.messenger.message_service.model.MessageServiceAuthenticationManager;
import com.al3xkras.messenger.model.authorities.ChatUserAuthority;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableWebSecurity
@Profile(value = {"default","security-test"})
public class SecurityConfig {

    @Bean
    public AuthenticationManager authenticationManager() {
        return new MessageServiceAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        MessageServiceAuthorizationFilter messageServiceAuthorizationFilter =
                new MessageServiceAuthorizationFilter();
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers(HttpMethod.GET,"/message").hasAnyAuthority(ChatUserAuthority.READ_SELF_CHAT_MESSAGES.getAuthority())
                .antMatchers(HttpMethod.PUT,"/message").hasAnyAuthority(ChatUserAuthority.MODIFY_ANY_CHAT_MESSAGE.getAuthority(),ChatUserAuthority.MODIFY_ANY_CHAT_MESSAGE.getAuthority())
                .antMatchers(HttpMethod.POST,"/message").hasAnyAuthority(ChatUserAuthority.SEND_MESSAGES.getAuthority())
                .antMatchers(HttpMethod.DELETE,"/message").hasAnyAuthority(ChatUserAuthority.DELETE_SELF_CHAT_MESSAGES.getAuthority(),ChatUserAuthority.DELETE_ANY_CHAT_MESSAGE.getAuthority())
                .antMatchers(HttpMethod.GET,"/messages").hasAnyAuthority(ChatUserAuthority.READ_SELF_CHAT_MESSAGES.getAuthority())
                .and()
                .authorizeRequests().anyRequest().authenticated()
                .and()
                .addFilterBefore(messageServiceAuthorizationFilter,BasicAuthenticationFilter.class);
        return http.build();
    }
}
