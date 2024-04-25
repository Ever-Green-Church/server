package com.evergreen.evergreenserver.global.jwt;

import com.evergreen.evergreenserver.domain.user.entity.UserRoleEnum;
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
import jakarta.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class JwtUtil {

  public static final String AUTHORIZATION_HEADER = "Authorization";
  public static final String AUTHORIZATION_KEY = "auth";
  public static final String BEARER_PREFIX = "Bearer ";
  private final long TOKEN_TIME = 60 * 60 * 1000L;

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

  // JWT 생성
  public String createToken(String username, UserRoleEnum role) {
    Date date = new Date();

    return BEARER_PREFIX +
        Jwts.builder()
            .setSubject(username) // 사용자 식별자값(ID)
            .claim(AUTHORIZATION_KEY, role) // 사용자 권한 (key, value) 형태
            .setExpiration(new Date(date.getTime() + TOKEN_TIME)) // 만료 시간 (현재시간+만료시킬 시간)
            .setIssuedAt(date) // 발급일
            .signWith(key, signatureAlgorithm) // 암호화 알고리즘 (secretKey, 선택한 알고리즘)
            .compact();
  }

  // 생성된 JWT를 Cookie에 저장
  public void addJwtToCookie(String token, HttpServletResponse res) {
    try {
      token = URLEncoder.encode(token, "utf-8")
          .replaceAll("\\+", "%20"); // Cookie Value 에는 공백이 불가능해서 encoding 진행

      Cookie cookie = new Cookie(AUTHORIZATION_HEADER, token); // Name-Value
      cookie.setPath("/");

      // Response 객체에 Cookie 추가
      res.addCookie(cookie);
    } catch (UnsupportedEncodingException e) {
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


}
