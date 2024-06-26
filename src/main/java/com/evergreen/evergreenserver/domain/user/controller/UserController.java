package com.evergreen.evergreenserver.domain.user.controller;

import com.evergreen.evergreenserver.domain.user.kakao.KakaoService;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/users")
public class UserController {

  private final KakaoService kakaoService;
  private final JwtUtil jwtUtil;

  /**
   * 카카오 로그인 요청
   *
   * @return 요청 주소로 redirect
   * location.href='https://kauth.kakao.com/oauth/authorize?client_id='+${kakao.client.id}+'&redirect_uri=http://localhost:8080/v1/users/kakao/callback&response_type=code'
   */
  @GetMapping("/auth/kakao/login")
  public ResponseEntity<ApiResponse> kakaoLogin() {
    return ResponseEntity.ok().body(new ApiResponse(kakaoService.clientId, HttpStatus.OK.value()));
  }

  /**
   * 카카오 로그인 콜백
   *
   * @param code     카카오에서 전달해주는 인증코드
   * @param response HttpServletResponse 객체
   * @return 홈페이지로 리다이렉트
   * @throws JsonProcessingException
   */
  @GetMapping("/kakao/callback")
  public ResponseEntity<ApiResponse> kakaoLoginCallback(@RequestParam String code,
      HttpServletResponse response) throws JsonProcessingException {

    String accessToken = kakaoService.kakaoLogin(code);

    Cookie cookie = jwtUtil.addJwtToCookie(accessToken);
    response.addCookie(cookie);

    return ResponseEntity.status(HttpStatus.FOUND)
        .header(HttpHeaders.LOCATION, "/")
        .body(new ApiResponse("카카오 로그인 성공 및 리다이렉트", HttpStatus.FOUND.value()));
  }
}
