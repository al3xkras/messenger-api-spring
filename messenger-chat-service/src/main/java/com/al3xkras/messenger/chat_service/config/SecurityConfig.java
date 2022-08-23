package com.al3xkras.messenger.chat_service.config;

import com.al3xkras.messenger.chat_service.filter.ChatServiceAuthenticationFilter;
import com.al3xkras.messenger.chat_service.filter.ChatServiceAuthorizationFilter;
import com.al3xkras.messenger.chat_service.model.ChatUserAuthenticationManager;
import com.al3xkras.messenger.chat_service.service.ChatService;
import com.al3xkras.messenger.model.MessengerUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.util.Arrays;
import java.util.HashSet;

import static com.al3xkras.messenger.model.authorities.ChatUserAuthority.*;

@Slf4j
@Configuration
@EnableWebSecurity
@Profile(value = {"default","security-test","no-security"})
public class SecurityConfig {

    private final ChatService chatService;
    private final HashSet<String> activeProfiles;
    @Autowired
    public SecurityConfig(ChatService chatService, Environment environment) {
        this.chatService = chatService;
        activeProfiles = new HashSet<>(Arrays.asList(environment.getActiveProfiles()));
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new ChatUserAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {

        if (activeProfiles.contains("no-security")){
            http.csrf().disable().authorizeRequests().antMatchers("/**").permitAll();
            log.warn(MessengerUtils.Messages.WARNING_SECURITY_DISABLED.value());
            return http.build();
        }

        ChatServiceAuthenticationFilter chatServiceAuthenticationFilter = new ChatServiceAuthenticationFilter("/auth",chatService);
        ChatServiceAuthorizationFilter chatServiceAuthorizationFilter = new ChatServiceAuthorizationFilter();

        http.csrf().disable()
                .authorizeRequests()
                .antMatchers("/auth").permitAll()
                .antMatchers(HttpMethod.GET,"/chat").hasAnyAuthority(READ_SELF_CHATS_INFO.getAuthority(),READ_ANY_CHATS_INFO_EXCEPT_SELF.getAuthority())
                .antMatchers(HttpMethod.PUT,"/chat").hasAuthority(MODIFY_CHAT_INFO.getAuthority())
                .antMatchers(HttpMethod.POST,"/chat").hasAuthority(CREATE_CHAT.getAuthority())
                .antMatchers(HttpMethod.GET,"/chat/users").hasAnyAuthority(READ_SELF_CHATS_INFO.getAuthority(),READ_ANY_CHATS_INFO_EXCEPT_SELF.getAuthority())
                .antMatchers(HttpMethod.PUT,"/chat/users").hasAnyAuthority(MODIFY_SELF_INFO.getAuthority(),MODIFY_CHAT_USER_INFO_EXCEPT_SELF.getAuthority())
                .antMatchers(HttpMethod.POST,"/chat/users").hasAuthority(ADD_CHAT_USER.getAuthority())
                .antMatchers(HttpMethod.DELETE,"/chat/users").hasAnyAuthority(DELETE_SELF.getAuthority(),DELETE_ANYONE_EXCEPT_SELF.getAuthority())
                .antMatchers(HttpMethod.GET,"/chats").hasAnyAuthority(READ_SELF_CHATS_INFO.getAuthority(),READ_ANY_CHATS_INFO_EXCEPT_SELF.getAuthority())
                .and()
                .authorizeRequests().anyRequest().authenticated()
                .and()
                .addFilterBefore(chatServiceAuthenticationFilter, BasicAuthenticationFilter.class)
                .addFilterBefore(chatServiceAuthorizationFilter, ChatServiceAuthenticationFilter.class);
        return http.build();
    }

}
