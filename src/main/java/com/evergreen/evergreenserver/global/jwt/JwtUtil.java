package com.evergreen.evergreenserver.global.jwt;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtUtil {

  // Header KEY 값
  public static final String AUTHORIZATION_HEADER = "Authorization";

  // Token 식별자
  public static final String BEARER_PREFIX = "Bearer ";

  public static final long ACCESS_TOKEN_TIME = 15 * 60 * 1000;  // 15분

  public static final long REFRESH_TOKEN_TIME = 7 * 24 * 60 * 60 * 1000;  // 7일

  @Value("${jwt.secret.key}") // Base64 Encode 한 SecretKey
  private String secretKey;

  private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

  private Key key;


  @PostConstruct
  public void init() {
    byte[] bytes = Base64.getDecoder().decode(secretKey);
    key = Keys.hmacShaKeyFor(bytes);
  }




}
