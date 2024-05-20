package com.evergreen.evergreenserver.global.jwt;

import com.evergreen.evergreenserver.global.exception.ApiException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class JwtUtil {

  private final RedisTemplate<String, String> redisTemplate;

  public static final String AUTHORIZATION_HEADER = "Authorization";
  public static final String BEARER_PREFIX = "Bearer ";
  public static final long ACCESS_TOKEN_TIME = 15 * 60 * 1000;  // 15분
  public static final long REFRESH_TOKEN_TIME = 7 * 24 * 60 * 60 * 1000;  // 7일

  @Value("${jwt.secret.key}")
  private String secretKey;
  private Key key;
  private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

  @PostConstruct
  public void init() {
    byte[] bytes = Base64.getDecoder()
        .decode(secretKey);
    key = Keys.hmacShaKeyFor(bytes);
  }

  public String createAccessToken(String loginId) {
    return createToken(loginId, ACCESS_TOKEN_TIME);
  }

  public String createRefreshToken(String loginId) {
    return createToken(loginId, REFRESH_TOKEN_TIME);
  }

  private String createToken(String loginId, long tokenTime) {
    Date date = new Date();

    return BEARER_PREFIX +
        Jwts.builder()
            .setSubject(loginId)
            .setExpiration(new Date(date.getTime() + tokenTime))
            .setIssuedAt(date)
            .signWith(key, signatureAlgorithm)
            .compact();
  }

  public void saveAccessTokenByLoginId(String loginId, String accessToken) {
    redisTemplate.opsForValue()
        .set(loginId, accessToken, REFRESH_TOKEN_TIME, TimeUnit.MILLISECONDS);
  }

  public void saveRefreshTokenByAccessToken(String accessToken, String refreshToken) {
    redisTemplate.opsForValue()
        .set(accessToken, refreshToken, REFRESH_TOKEN_TIME, TimeUnit.MILLISECONDS);
  }

  // 세션 쿠키
  public Cookie addJwtToCookie(String bearerAccessToken) {
    try {
      String spaceRemovedToken = URLEncoder.encode(bearerAccessToken, "utf-8")
          .replaceAll("\\+", "%20"); // 공백 제거

      Cookie cookie = new Cookie(AUTHORIZATION_HEADER, spaceRemovedToken);
      cookie.setPath("/");

      return cookie;
    } catch (UnsupportedEncodingException e) {
      throw new ApiException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  public String substringToken(String tokenValue) {
    if (StringUtils.hasText(tokenValue) && tokenValue.startsWith(
        BEARER_PREFIX)) {
      return tokenValue.substring(7);
    }
    throw new NullPointerException("Not Found Token");
  }

  // JWT 검증
  public boolean validateToken(String token) {
    try {
      Jwts.parserBuilder().setSigningKey(key).build()
          .parseClaimsJws(token);   // 토큰 검증 코드 (위변조 여부, 만료 여부 등 체크 가능)
      return true;  //문제가 없다면 true를 반환.
    } catch (SecurityException | MalformedJwtException | SignatureException e) {
    } catch (ExpiredJwtException e) {
    } catch (UnsupportedJwtException e) {
    } catch (IllegalArgumentException e) {
    }
    return false;  //문제가 있다면 false가 반환.
  }

  // JWT에서 사용자 정보 가져오기
  public Claims getUserInfoFromToken(String token) {
    return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    //getBody라고 하면 바디 부분에 들어있는 Claims를 가져올 수 있음. -> 여기에 사용자 정보가 들어있음 (JWT가 claim 기반 웹토큰임)
  }


  // HttpServletRequest 에서 Cookie Value : JWT 가져오는 메서드
  public String getTokenFromRequest(HttpServletRequest req) {
    Cookie[] cookies = req.getCookies();   //여러개 담겨있는 쿠키들을 배열로 가져옴
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if (cookie.getName().equals(AUTHORIZATION_HEADER)) {
          try {
            return URLDecoder.decode(cookie.getValue(),
                "UTF-8"); // Encode 되어 넘어간 Value를 다시 Decode 해줘야함
          } catch (UnsupportedEncodingException e) {
            return null;
          }
        }
      }
    }
    return null;
  }

  public Boolean checkIsLoggedIn(String loginId) {
    if (redisTemplate.hasKey(loginId)) {
      return true;
    }
    return false;
  }

  public String getAccessTokenByLoginId(String loginId) {
    return redisTemplate.opsForValue().get(loginId);
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
    String loginId = getUserInfoFromToken(refreshTokenValue).getSubject();
    return createAccessToken(loginId);
  }

  public void regenerateToken(String newAccessToken, String accessToken,
      String refreshTokenValue) {
    Claims info = getUserInfoFromToken(refreshTokenValue);
    String loginId = info.getSubject();

    Long expirationTime = info.getExpiration().getTime();

    // 새로 만든 AccessToken을 redis에 저장
    redisTemplate.opsForValue()
        .set(loginId, newAccessToken,
            expirationTime, TimeUnit.MILLISECONDS);

    // 새로 만든 AccessToken을 key로 refreshToken을 다시 DB에 저장
    redisTemplate.opsForValue().set(newAccessToken,
        BEARER_PREFIX + refreshTokenValue,
        expirationTime, TimeUnit.MILLISECONDS);

    // 만료된 token으로 저장되어있는 refreshToken은 삭제
    redisTemplate.delete(accessToken);
  }
}
