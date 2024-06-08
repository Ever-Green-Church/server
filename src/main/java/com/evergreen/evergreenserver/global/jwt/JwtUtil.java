package com.evergreen.evergreenserver.global.jwt;

import com.evergreen.evergreenserver.global.exception.ApiException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtUtil {

  public static final String AUTHORIZATION_HEADER = "Authorization";
  public static final String BEARER_PREFIX = "Bearer ";
  public static final long ACCESS_TOKEN_TIME = 15 * 60 * 1000;  // 15분
  public static final long REFRESH_TOKEN_TIME = 7 * 24 * 60 * 60 * 1000;  // 7일
  private final RedisTemplate<String, String> redisTemplate;
  private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
  @Value("${jwt.secret.key}")
  private String secretKey;
  private Key key;

  @PostConstruct
  public void init() {
    byte[] bytes = Base64.getDecoder()
        .decode(secretKey);
    key = Keys.hmacShaKeyFor(bytes);
  }

  public String createAccessToken(String email) {
    return createToken(email, ACCESS_TOKEN_TIME);
  }

  public String createRefreshToken(String email) {
    return createToken(email, REFRESH_TOKEN_TIME);
  }

  private String createToken(String email, long tokenTime) {
    Date date = new Date();

    return BEARER_PREFIX +
        Jwts.builder()
            .setSubject(email)
            .setExpiration(new Date(date.getTime() + tokenTime))
            .setIssuedAt(date)
            .signWith(key, signatureAlgorithm)
            .compact();
  }

  public void saveAccessTokenByEmail(String email, String accessToken) {
    redisTemplate.opsForValue()
        .set(email, accessToken, REFRESH_TOKEN_TIME, TimeUnit.MILLISECONDS);
  }

  public void saveRefreshTokenByAccessToken(String accessToken, String refreshToken) {
    redisTemplate.opsForValue()
        .set(accessToken, refreshToken, REFRESH_TOKEN_TIME, TimeUnit.MILLISECONDS);
  }

  // 세션 쿠키
  public Cookie addJwtToCookie(String bearerAccessToken) {
    String spaceRemovedToken = URLEncoder.encode(bearerAccessToken, StandardCharsets.UTF_8)
        .replaceAll("\\+", "%20"); // 공백 제거

    Cookie cookie = new Cookie(AUTHORIZATION_HEADER, spaceRemovedToken);
    cookie.setPath("/");

    return cookie;
  }

  // JWT 검증
  public boolean validateToken(String token) {
    try {
      Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
      return true;
    } catch (Exception ignored) {
    }
    return false;
  }

  // JWT에서 사용자 정보 가져오기
  public Claims getUserInfoFromToken(String token) {
    try {
      return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    } catch (Exception ex) {
      throw new ApiException("토큰에서 유저 정보 조회 실패", HttpStatus.BAD_REQUEST);
    }
  }


  // HttpServletRequest 에서 Cookie Value : JWT 가져오는 메서드
  public String getTokenFromRequest(HttpServletRequest req) {
    Cookie[] cookies = req.getCookies();   //여러개 담겨있는 쿠키들을 배열로 가져옴
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if (cookie.getName().equals(AUTHORIZATION_HEADER)) {
          return URLDecoder.decode(cookie.getValue(),
              StandardCharsets.UTF_8); // Encode 되어 넘어간 Value를 다시 Decode 해줘야함
        }
      }
    }
    return null;
  }

  public Boolean checkIsLoggedIn(String email) {
    return redisTemplate.hasKey(email);
  }

  public String getAccessTokenByEmail(String email) {
    return redisTemplate.opsForValue().get(email);
  }

  public boolean shouldAccessTokenBeRefreshed(String accessTokenValue) {
    try {
      Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessTokenValue);
      return false;
    } catch (ExpiredJwtException e) {
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  public String getRefreshtokenByAccessToken(String accessToken) {
    return redisTemplate.opsForValue().get(accessToken);
  }

  public String createAccessTokenByRefreshToken(String refreshTokenValue) {
    String email = getUserInfoFromToken(refreshTokenValue).getSubject();
    return createAccessToken(email);
  }

  public void regenerateToken(String newAccessToken, String accessToken,
      String refreshTokenValue) {
    Claims info = getUserInfoFromToken(refreshTokenValue);
    String email = info.getSubject();

    Long expirationTime = info.getExpiration().getTime();

    // 새로 만든 AccessToken을 redis에 저장
    redisTemplate.opsForValue()
        .set(email, newAccessToken,
            expirationTime, TimeUnit.MILLISECONDS);

    // 새로 만든 AccessToken을 key로 refreshToken을 다시 DB에 저장
    redisTemplate.opsForValue().set(newAccessToken,
        BEARER_PREFIX + refreshTokenValue,
        expirationTime, TimeUnit.MILLISECONDS);

    // 만료된 token으로 저장되어있는 refreshToken은 삭제
    redisTemplate.delete(accessToken);
  }
}
