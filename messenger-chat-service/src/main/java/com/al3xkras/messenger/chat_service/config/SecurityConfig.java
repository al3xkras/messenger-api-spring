package com.al3xkras.messenger.chat_service.config;

import com.al3xkras.messenger.chat_service.filter.ChatServiceAuthenticationFilter;
import com.al3xkras.messenger.chat_service.filter.ChatServiceAuthorizationFilter;
import com.al3xkras.messenger.chat_service.service.ChatService;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import static com.al3xkras.messenger.model.ChatUserRole.*;

@Slf4j
@Configuration
@EnableWebSecurity
@Profile(value = {"default","security-test","no-security"})
public class SecurityConfig {

    @Autowired
    private ChatService chatService;

    @Value("${spring.profiles.active}")
    private String profile;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {

        if (profile.equals("no-security")){
            http.csrf().disable().authorizeRequests().antMatchers("/**").permitAll();
            log.warn("spring security is disabled!");
            return http.build();
        }

        ChatServiceAuthenticationFilter chatServiceAuthenticationFilter =
                new ChatServiceAuthenticationFilter("/auth",chatService);
        ChatServiceAuthorizationFilter chatServiceAuthorizationFilter = new ChatServiceAuthorizationFilter();

        http.csrf().disable()
                .authorizeRequests()
                .antMatchers("/auth").permitAll()
                .antMatchers(HttpMethod.GET,"/chat").authenticated()
                .antMatchers(HttpMethod.PUT,"/chat").hasAnyAuthority(ADMIN.name())
                .antMatchers(HttpMethod.POST,"/chat").authenticated()
                .antMatchers(HttpMethod.GET,"/chat/users").authenticated()
                .antMatchers(HttpMethod.PUT,"/chat/users").hasAnyAuthority(ADMIN.name(),USER.name())
                .antMatchers(HttpMethod.POST,"/chat/users").hasAnyAuthority(ADMIN.name())
                .antMatchers(HttpMethod.DELETE,"/chat/users").hasAnyAuthority(ADMIN.name(),USER.name())
                .antMatchers(HttpMethod.GET,"/chats").authenticated()
                .and()
                .authorizeRequests().anyRequest().authenticated()
                .and()
                .addFilterBefore(chatServiceAuthenticationFilter, BasicAuthenticationFilter.class)
                .addFilterBefore(chatServiceAuthorizationFilter, ChatServiceAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        //TODO replace with bcrypt
        return new PasswordEncoder() {
            @Override
            public String encode(CharSequence rawPassword) {
                return rawPassword.toString();
            }

            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                return rawPassword.toString().equals(encodedPassword);
            }
        };
    }
}
