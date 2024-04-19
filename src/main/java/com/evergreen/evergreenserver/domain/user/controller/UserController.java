package com.evergreen.evergreenserver.domain.user.controller;

import com.evergreen.evergreenserver.domain.user.dto.SignupRequestDto;
import com.evergreen.evergreenserver.domain.user.dto.SignupResponseDto;
import com.evergreen.evergreenserver.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/users")
public class UserController {

  private final UserService userService;

  //카카오로그인만으로 회원가입

  // 테스트용 signup
  @PostMapping("/signup")
  public SignupResponseDto signup(@RequestBody SignupRequestDto signupResponseDto) {

    userService.signup(signupResponseDto);

    return new SignupResponseDto("성공");
  }
}
