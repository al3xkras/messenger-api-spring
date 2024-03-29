package com.al3xkras.messenger.user_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EntityScan("com.al3xkras.messenger")
@ComponentScan({"com.al3xkras.messenger.user_service","com.al3xkras.messenger.model"})
public class MessengerUserServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(MessengerUserServiceApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate(){
		return new RestTemplate();
	}

}
