package com.evergreen.evergreenserver.domain.praytitle.repository;

import com.evergreen.evergreenserver.domain.praytitle.entity.PrayTitle;
import com.evergreen.evergreenserver.domain.user.entity.User;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PrayTitleRepository extends JpaRepository<PrayTitle, Long> {

  @EntityGraph(attributePaths = {"user"})
  Optional<PrayTitle> findByUser(@Param("user") User user);
}