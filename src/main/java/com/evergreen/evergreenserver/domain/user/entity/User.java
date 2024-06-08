package com.evergreen.evergreenserver.domain.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "user")
@NoArgsConstructor
public class User {

    @Id
    @Column(name = "kakaoId")
    private Long kakaoId;

    @Column(name = "nickname")
    private String nickname;

    @Column
    private String password;

    @Column(nullable = false, name = "role")
    @Enumerated(value = EnumType.STRING)
    private UserRoleEnum role;

    @Column(name = "email")
    private String email;

    @Column(name = "image")
    private String image;

    public User(String nickname, String password) {
        this.nickname = nickname;
        this.password = password;
        this.role = UserRoleEnum.USER;
    }

    public User(String nickname, String email, String encodedPassword, Long kakaoId, String imageUrl) {
        this.nickname = nickname;
        this.password = encodedPassword;
        this.email = email;
        this.kakaoId = kakaoId;
        this.role = UserRoleEnum.USER;
        this.image = imageUrl;
    }

}
