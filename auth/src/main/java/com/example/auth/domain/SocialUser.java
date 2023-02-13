package com.example.auth.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Table(name = "social_user")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SocialUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "sid")
    String sid;

    @Column(name = "provider")
    String provider;

    @Column(name = "username")
    String username;

    @Column(name = "email")
    String email;

    @Column(name = "profile_image")
    String profileImage;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;
}
