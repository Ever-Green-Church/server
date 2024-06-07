package com.evergreen.evergreenserver.global.filter;

import com.evergreen.evergreenserver.global.jwt.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j(topic = "JwtAuthorizationFilter")
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

  private final JwtUtil jwtUtil;
  private final UserDetailsServiceImpl userDetailsService;
  private final FilterUtil filterUtil;

  @Override
  protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res,
      FilterChain filterChain) throws ServletException, IOException {

    String accessToken = jwtUtil.getTokenFromRequest(req);

    if (StringUtils.hasText(accessToken)) {

      // accessToken이 만료되었는지 확인
      if (jwtUtil.shouldAccessTokenBeRefreshed(accessToken.substring(7))) {
        String refreshTokenValue = jwtUtil.getRefreshtokenByAccessToken(accessToken).substring(7);
        // refreshtoken이 유효한지 확인
        if (jwtUtil.validateToken(refreshTokenValue)) {
          // accessToken 재발급
          String newAccessToken = jwtUtil.createAccessTokenByRefreshToken(refreshTokenValue);
          Cookie cookie = jwtUtil.addJwtToCookie(newAccessToken);
          res.addCookie(cookie);

          // DB 토큰도 새로고침
          jwtUtil.regenerateToken(newAccessToken, accessToken, refreshTokenValue);

          // 재발급된 토큰으로 검증 진행하도록 대입
          accessToken = newAccessToken;
        }
        // 유효하지 않다면 재발급 없이 만료된 상태로 진행
      }

      String accessTokenValue = accessToken.substring(7);

      if (jwtUtil.validateToken(accessTokenValue)) {
        Claims info = jwtUtil.getUserInfoFromToken(accessTokenValue);

        try {
          setAuthentication(info.getSubject());
        } catch (Exception e) {
          log.error(e.getMessage());
          return;
        }
      } else {
        // 인증정보가 존재하지 않을때
        filterUtil.setMassageToResponse("인가 불가: 토큰이 유효하지 않습니다.", res, HttpStatus.UNAUTHORIZED);
        return;
      }

    }

    filterChain.doFilter(req, res);
  }

  // 인증 처리
  public void setAuthentication(String email) {
    SecurityContext context = SecurityContextHolder.createEmptyContext();
    Authentication authentication = createAuthentication(email);
    context.setAuthentication(authentication);

    SecurityContextHolder.setContext(context);
  }

  // 인증 객체 생성
  private Authentication createAuthentication(String email) {
    UserDetails userDetails = userDetailsService.loadUserByUsername(email);
    return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
  }

}