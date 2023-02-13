package com.example.auth.repository;

import com.example.auth.domain.SocialUser;
import com.example.auth.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SocialUserRepository extends JpaRepository<SocialUser, Long> {
    Optional<SocialUser> findBySidAndProvider(String sid, String provider);
}
