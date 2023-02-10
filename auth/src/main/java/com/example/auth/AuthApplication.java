package com.example.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Date;

@SpringBootApplication
@Slf4j
public class AuthApplication {

	public static final String secretKey = "secret";


	public static String createToken(String subject) {
		Claims claims = Jwts.claims().setSubject(subject);

		Date now = new Date();
		Date validity = new Date(now.getTime() + 3600000); // 1 hour

		return Jwts.builder()
				.setClaims(claims)
				.setIssuedAt(now)
				.setExpiration(validity)
				.signWith(SignatureAlgorithm.HS256, secretKey)
				.compact();
	}

	public static void main(String[] args) {
		SpringApplication.run(AuthApplication.class, args);
		log.info("dummy JWT is = {}", createToken("userId"));
	}
}
