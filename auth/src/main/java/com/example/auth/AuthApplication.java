package com.example.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static com.example.auth.security.JwtManager.createToken;

@SpringBootApplication
@Slf4j
public class AuthApplication {


	public static void main(String[] args) {
		SpringApplication.run(AuthApplication.class, args);
		log.info("dummy JWT is = {}", createToken("userId"));
	}
}
