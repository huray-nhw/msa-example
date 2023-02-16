package com.example.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@Slf4j
public class IndexController {

    @GetMapping("/userAgent")
    @ResponseBody
    public String afterLogin(HttpServletRequest request) {
        String useragent = request.getHeader("User-Agent");
        log.info("userAgent={}", useragent);
        return useragent;
    }

    @GetMapping("/")
    @ResponseBody
    public String index() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        OAuth2User details = (OAuth2User) authentication.getPrincipal();
        return "userName is " + authentication.getName();
    }

    @GetMapping("/hello")
    public String hello(Model model) {
        model.addAttribute("message","하이");
        return "hello";
    }

    @GetMapping("/appLoginComplete")
    public String appLoginComplete(String token, Model model) {
        model.addAttribute("token", token);
        return "appLoginComplete";
    }


}
