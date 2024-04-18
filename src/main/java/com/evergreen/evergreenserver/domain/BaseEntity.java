package com.evergreen.evergreenserver.domain;

import jakarta.persistence.Column;
import java.sql.Timestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

public abstract class BaseEntity {
  @CreatedDate
  @Column(updatable = false)
  private Timestamp createdAt;

  @LastModifiedDate
  private Timestamp modifiedAt;
}
