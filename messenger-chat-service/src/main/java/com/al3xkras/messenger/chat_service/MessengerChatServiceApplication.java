package com.al3xkras.messenger.chat_service;

import com.al3xkras.messenger.chat_service.model.JwtAccessTokens;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@SpringBootApplication
@EntityScan("com.al3xkras.messenger")
public class MessengerChatServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(MessengerChatServiceApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate(){
		return new RestTemplate();
	}

	@Bean
	public JwtAccessTokens jwtAccessTokens(){
		return new JwtAccessTokens();
	}

	@Bean
	public PasswordEncoder passwordEncoder(Environment environment){
		if (Arrays.stream(environment.getActiveProfiles()).anyMatch(x->x.contains("test"))){
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
		return new BCryptPasswordEncoder(12);
	}

}
