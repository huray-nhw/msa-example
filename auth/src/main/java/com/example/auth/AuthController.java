package com.example.auth;

import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.endpoint.SecurityContext;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.context.support.SecurityWebApplicationContextUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.example.auth.AuthApplication.secretKey;


@RestController
@Slf4j
public class AuthController {

    @GetMapping("/")
    public String afterLogin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        OAuth2User details = (OAuth2User) authentication.getPrincipal();

        return "username is " + authentication.getName();
    }

    @PostMapping("/validateToken")
    public ResponseEntity signIn(@RequestParam String token) {
        log.info("Trying to validate token {}", token);
        Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
        return ResponseEntity.ok(null);
    }
}
