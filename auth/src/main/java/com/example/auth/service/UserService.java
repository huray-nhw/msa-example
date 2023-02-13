package com.example.auth.service;

import com.example.auth.domain.SocialUser;
import com.example.auth.domain.User;
import com.example.auth.repository.SocialUserRepository;
import com.example.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final SocialUserRepository socialUserRepository;

    @Transactional
    public User saveOrUpdateOauth2User(OAuth2User oAuth2User) {
        String sid = oAuth2User.getName();
        String provider = oAuth2User.getAttribute("provider");
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String profileImage = oAuth2User.getAttribute("profile");
        // 없으면 회원가입
        Optional<SocialUser> bySidAndProvider = socialUserRepository.findBySidAndProvider(sid, provider);
        if (bySidAndProvider.isEmpty()) {
            SocialUser socialUser = socialUserRepository.save(SocialUser.builder()
                    .sid(sid)
                    .username(name)
                    .email(email)
                    .provider(provider)
                    .profileImage(profileImage)
                    .build());
            User newUser = userRepository.save(User.builder()
                    .name(name)
                    .email(email)
                    .joinType(User.JoinType.SOCIAL)
                    .build());
            socialUser.setUser(newUser);
            return newUser;
        } else {
            SocialUser socialUser = bySidAndProvider.get();
            socialUser.setUsername(name);
            socialUser.setProfileImage(profileImage);
            return socialUser.getUser();
        }

        //소셜 유저 삽입
        //sid, provider, username, email, profile사진
        //일반 유저 삽입
        // 있으면 업데이트

        //유저 객체 리턴
    }
}
