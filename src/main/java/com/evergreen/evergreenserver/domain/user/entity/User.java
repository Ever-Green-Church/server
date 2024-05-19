package com.evergreen.evergreenserver.domain.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "user")
@NoArgsConstructor
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "loginId")
  private String loginId;

  @Column
  private String password;

  @Column(nullable = false, name = "role")
  @Enumerated(value = EnumType.STRING)
  private UserRoleEnum role;

  @Column(name = "kakaoId")
  private Long kakaoId;

  @Column(name = "email")
  private String email;

  public User(String loginId, String password) {
    this.loginId = loginId;
    this.password = password;
    this.role = UserRoleEnum.USER;
  }

  public User(String nickname, String email, String encodedPassword, Long kakaoId) {
    this.loginId = nickname;
    this.password = encodedPassword;
    this.email = email;
    this.kakaoId = kakaoId;
    this.role = UserRoleEnum.USER;
  }

  public User kakaoIdUpdate(Long kakaoId) {
    this.kakaoId = kakaoId;
    return this;
  }
}
