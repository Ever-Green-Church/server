package com.evergreen.evergreenserver.domain.user.service;

import com.evergreen.evergreenserver.domain.user.dto.SignupRequestDto;
import com.evergreen.evergreenserver.domain.user.entity.User;
import com.evergreen.evergreenserver.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public void signup(SignupRequestDto signupResponseDto) {

    String loginId = signupResponseDto.getLoginId();
    String password = passwordEncoder.encode(signupResponseDto.getPassword());

    User user = new User(loginId, password);

    userRepository.save(user);

  }
}
