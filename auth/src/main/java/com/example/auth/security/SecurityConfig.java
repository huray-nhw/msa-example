package com.example.auth.security;

import com.example.auth.domain.User;
import com.example.auth.domain.UserDetail;
import com.example.auth.service.UserService;
import com.google.gson.JsonObject;
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
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

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

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        System.out.println("====== this is oauth2 onAuthenticationSuccess ========");

        //회원가입 또는 로그인
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        User user = userService.saveOrUpdateOauth2User(oAuth2User);

        //create our jwt
        String token = JwtManager.createToken(user.getId().toString());

        //send Response
        //todo 로그인 폼 헤더값에 토큰을 주는건?
        tokenResponse(response, token);
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
