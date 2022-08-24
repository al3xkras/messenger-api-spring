package com.al3xkras.messenger.chat_service;

import com.al3xkras.messenger.chat_service.model.JwtAccessTokens;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EntityScan("com.al3xkras.messenger")
@ComponentScan({"com.al3xkras.messenger.chat_service","com.al3xkras.messenger.model"})
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

}
