package com.evergreen.evergreenserver.domain.praytitle.repository;

import com.evergreen.evergreenserver.domain.praytitle.entity.PrayTitle;
import com.evergreen.evergreenserver.domain.user.entity.User;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PrayTitleRepository extends JpaRepository<PrayTitle, Long> {

  @Query("SELECT prayTitle FROM PrayTitle prayTitle JOIN FETCH prayTitle.user WHERE prayTitle.user = :user")
  Optional<PrayTitle> findByUserWithFetchJoin(@Param("user") User user);
}
