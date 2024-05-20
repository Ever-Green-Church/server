package com.evergreen.evergreenserver.domain.user.repository;

import com.evergreen.evergreenserver.domain.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> findByKakaoId(Long kakoId);

  Optional<User> findByEmail(String email);
}
