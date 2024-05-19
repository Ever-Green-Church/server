package com.evergreen.evergreenserver.domain.user.controller;

import com.evergreen.evergreenserver.domain.user.dto.SignupRequestDto;
import com.evergreen.evergreenserver.domain.user.dto.SignupResponseDto;
import com.evergreen.evergreenserver.domain.user.kakao.KakaoService;
import com.evergreen.evergreenserver.domain.user.service.UserService;
import com.evergreen.evergreenserver.global.jwt.JwtUtil;
import com.evergreen.evergreenserver.global.response.ApiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/users")
public class UserController {

  private final UserService userService;
  private final KakaoService kakaoService;
  private final JwtUtil jwtUtil;

  //카카오로그인만으로 회원가입

  // 테스트용 signup
  @PostMapping("/signup")
  public SignupResponseDto signup(@RequestBody SignupRequestDto signupResponseDto) {

    userService.signup(signupResponseDto);

    return new SignupResponseDto("성공");
  }

  //  location.href='https://kauth.kakao.com/oauth/authorize?client_id='+${kakao.client.id}+'&redirect_uri=http://localhost:8080/v1/users/kakao/callback&response_type=code'
  @GetMapping("/kakao/callback")
  public ResponseEntity<ApiResponse> kakaoLoginCallback(
      @RequestParam String code,
      HttpServletResponse response) throws JsonProcessingException {

    String accessToken = kakaoService.kakaoLogin(code);

    Cookie cookie = jwtUtil.addJwtToCookie(accessToken);
    response.addCookie(cookie);

    return ResponseEntity.status(HttpStatus.FOUND)
        .header(HttpHeaders.LOCATION,
            "/")
        .body(new ApiResponse("카카오 로그인 성공 및 리다이렉트", HttpStatus.FOUND.value()));
  }
}
