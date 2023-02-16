package com.example.auth.controller;

import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import static com.example.auth.security.JwtManager.secretKey;


@RestController
@Slf4j
public class AuthController {

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
