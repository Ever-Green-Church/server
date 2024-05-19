package com.evergreen.evergreenserver.domain.user.kakao;

import com.evergreen.evergreenserver.domain.user.entity.User;
import com.evergreen.evergreenserver.domain.user.repository.UserRepository;
import com.evergreen.evergreenserver.global.jwt.JwtUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class KakaoService {

  private final PasswordEncoder passwordEncoder;
  private final RestTemplate restTemplate;
  private final UserRepository userRepository;
  private final JwtUtil jwtUtil;

  @Value("${kakao.client.id}")
  public String clientId;

  public String kakaoLogin(String code)
      throws JsonProcessingException {

    // 1. "인가 코드"로 "액세스 토큰" 요청
    String accessToken = getToken(code);

    // 2. 토큰으로 카카오 API 호출 : "액세스 토큰"으로 "카카오 사용자 정보" 가져오기
    KakaoUserInfoDto kakaoUserInfo = getKakaoUserInfo(accessToken);

    // 3. 필요시에 회원가입
    User kakaoUser = registerKakaoUserIfNeeded(kakaoUserInfo);
    String loginId = kakaoUser.getLoginId();

    // 이미 로그인 되어 있는지 확인
    if (jwtUtil.checkIsLoggedIn(loginId)) {
      return jwtUtil.getAccessTokenByLoginId(loginId);
    }

    // accessToken을 만들어서 반환
    String bearerAccessToken = jwtUtil.createAccessToken(loginId);

    // 토큰을 DB에 저장
    jwtUtil.saveAccessTokenByLoginId(loginId, bearerAccessToken);
    String refreshToken = jwtUtil.createRefreshToken(loginId);
    jwtUtil.saveRefreshTokenByAccessToken(bearerAccessToken, refreshToken);

    return bearerAccessToken;
  }

  public String getToken(String code) throws JsonProcessingException {

    // 요청 URL 만들기
    URI uri = UriComponentsBuilder
        .fromUriString("https://kauth.kakao.com")
        .path("/oauth/token")
        .encode()
        .build()
        .toUri();

    // HTTP Header 생성
    HttpHeaders headers = new HttpHeaders();
    headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

    // HTTP Body 생성
    MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
    body.add("grant_type", "authorization_code");
    body.add("client_id", clientId);
    body.add("redirect_uri", "http://localhost:8080/v1/users/kakao/callback");
    body.add("code", code);

    RequestEntity<MultiValueMap<String, String>> requestEntity = RequestEntity
        .post(uri)
        .headers(headers)
        .body(body);

    // HTTP 요청 보내기
    ResponseEntity<String> response = restTemplate.exchange(
        //여기서 restTemplate를 호출 -> 카카오 서버 호출 -> 인증코드로 토큰 요청
        requestEntity,
        String.class
    );

    // HTTP 응답 (JSON) -> 액세스 토큰 파싱
    JsonNode jsonNode = new ObjectMapper().readTree(response.getBody());

    return jsonNode.get("access_token").asText();
  }

  public KakaoUserInfoDto getKakaoUserInfo(String accessToken) throws JsonProcessingException {

    // 요청 URL 만들기
    URI uri = UriComponentsBuilder
        .fromUriString("https://kapi.kakao.com")
        .path("/v2/user/me")
        .encode()
        .build()
        .toUri();

    // HTTP Header 생성
    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", "Bearer " + accessToken);
    headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

    RequestEntity<MultiValueMap<String, String>> requestEntity = RequestEntity
        .post(uri)
        .headers(headers)
        .body(new LinkedMultiValueMap<>());  //body는 보낼 필요가 없어서 그냥 생성만 해서 보냄

    // HTTP 요청 보내기
    ResponseEntity<String> response = restTemplate.exchange(
        requestEntity,
        String.class
    );

    JsonNode jsonNode = new ObjectMapper().readTree(response.getBody());
    Long id = jsonNode.get("id").asLong();
    String nickname = jsonNode.get("properties")
        .get("nickname").asText();
    String email = jsonNode.get("kakao_account")
        .get("email").asText();
    String imageUrl = jsonNode.get("properties")
        .get("profile_image").asText();

    return new KakaoUserInfoDto(id, nickname, email, imageUrl);
  }

  public User registerKakaoUserIfNeeded(KakaoUserInfoDto kakaoUserInfo) {
    // DB 에 중복된 Kakao Id 가 있는지 확인
    Long kakaoId = kakaoUserInfo.getId();
    User kakaoUser = userRepository.findByKakaoId(kakaoId).orElse(null);

    if (kakaoUser == null) {
      // 카카오 사용자 email 동일한 email 가진 회원이 있는지 확인
      String kakaoEmail = kakaoUserInfo.getEmail();
      User sameEmailUser = userRepository.findByEmail(kakaoEmail).orElse(null);
      if (sameEmailUser != null) {
        kakaoUser = sameEmailUser;
        // 기존 회원정보에 카카오 Id 추가
        kakaoUser = kakaoUser.kakaoIdUpdate(kakaoId);
        //Transactional이 필요 없게 하려고 return을 kakaoUser라는 객체로 했다.
        // save (@Transactional 걸어버리면 하나의 오류라도 있으면 다시 롤백되므로, 오류 난건 그대로 놓고 나머지 잘 수행된건 그대로 DB에 영향을 주게 하고 싶기 때문.)
      } else {
        // 신규 회원가입
        // password: random UUID
        String password = UUID.randomUUID().toString();
        String encodedPassword = passwordEncoder.encode(password);

        // email: kakao email
        String email = kakaoUserInfo.getEmail();

        // image
        String url = kakaoUserInfo.getImageUrl();

        kakaoUser = new User(kakaoUserInfo.getNickname(), email, encodedPassword, kakaoId);
      }

      userRepository.save(kakaoUser);
    }
    return kakaoUser;
  }


}
