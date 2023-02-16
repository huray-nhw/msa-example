package com.example.auth.security;

import com.example.auth.domain.User;
import com.example.auth.domain.UserDetail;
import com.example.auth.service.UserService;
import com.google.gson.JsonObject;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static com.example.auth.security.FormAuthenticationSuccessHandler.tokenResponse;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final UserService userService;

    @Bean
    public SimpleUrlAuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler(UserService userService) {
        return new OAuth2AuthenticationSuccessHandler(userService);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        //TODO jwt 토큰 필터 만들고 등록할 것
        http
                .csrf().disable()
                .headers().frameOptions().disable()
                .and()
                .authorizeHttpRequests(
                        authorize -> authorize
                                .requestMatchers("/hello/**").permitAll()
                                .requestMatchers("/userAgent/**").permitAll()
                                .requestMatchers("/validateToken/**").permitAll()
                                .requestMatchers("/h2-console/**").permitAll()
                                .requestMatchers("/actuator/**").permitAll()
                                .anyRequest().authenticated()
                ).formLogin(login -> login.successHandler(new FormAuthenticationSuccessHandler()))
                .oauth2Login(login -> login.successHandler(oAuth2AuthenticationSuccessHandler(userService)))
        ;

        return http.build();
    }

//    @Bean
//    public InMemoryUserDetailsManager userDetailsService() {
//        UserDetails user = User.withDefaultPasswordEncoder()
//                .username("user")
//                .password("password")
//                .roles("USER", "ACTUATOR")
//                .build();
//        return new InMemoryUserDetailsManager(user);
//    }


}


@RequiredArgsConstructor
class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserService userService;

    RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        System.out.println("====== this is oauth2 onAuthenticationSuccess ========");

        String userAgent = request.getHeader("User-Agent");
        System.out.println("userAgent = " + userAgent);

        String requestURI = request.getRequestURI();
        System.out.println("requestURI = " + requestURI);

        //회원가입 또는 로그인
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        User user = userService.saveOrUpdateOauth2User(oAuth2User);

        //create our jwt/ref
        String token = JwtManager.createToken(user.getId().toString());

//        tokenResponse(response, token);
        //분기 처리 후
        String[] split = requestURI.split("/");
        String lastOne = split[split.length - 1];
        boolean app = lastOne.contains("app");
        //웹으로 보내기
        //앱으로 보내기
        if (app) {
            this.forwardToAppLoginComplete(request, response, token);
        }
        else{
            this.handle(request, response, authentication);
        }
        super.clearAuthenticationAttributes(request);
    }


    protected void handle(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        String targetUrl = "/";

        if (response.isCommitted()) {
            super.logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
        } else {
            redirectStrategy.sendRedirect(request, response, targetUrl);
        }
    }

    protected void forwardToAppLoginComplete(HttpServletRequest request, HttpServletResponse response, String token)
            throws IOException, ServletException {
        String targetUrl = "/appLoginComplete?token=" + token;
        RequestDispatcher requestDispatcher = request.getRequestDispatcher(targetUrl);
        requestDispatcher.forward(request, response);
    }
}

class FormAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        System.out.println("====== this is form AuthenticationSuccess ========");

        UserDetail userDetail = (UserDetail) authentication.getPrincipal();
        String token = JwtManager.createToken(Long.toString(userDetail.getUserId()));
        tokenResponse(response, token);
    }

    public static void tokenResponse(HttpServletResponse response, String token) {
        try (PrintWriter writer = response.getWriter()) {
            JsonObject json = new JsonObject();
            json.addProperty("accessToken", token);
            json.addProperty("refreshToken", "QAWSEDRF123");

            response.setStatus(HttpStatus.ACCEPTED.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(StandardCharsets.UTF_8.toString());

            writer.write(json.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
