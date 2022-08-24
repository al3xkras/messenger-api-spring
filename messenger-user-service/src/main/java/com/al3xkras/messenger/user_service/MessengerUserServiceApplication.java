package com.al3xkras.messenger.user_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

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
