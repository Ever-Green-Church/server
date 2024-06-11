package com.evergreen.evergreenserver.domain.board.entity;

import com.evergreen.evergreenserver.domain.board.dto.PostBoardDto;
import com.evergreen.evergreenserver.domain.user.entity.User;
import com.evergreen.evergreenserver.global.entity.Timestamped;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "board")
public class Board extends Timestamped {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column
  private String title;

  @Column
  private String contents;

  @ManyToOne
  @JoinColumn(name = "user")
  private User user;

  public Board(PostBoardDto postBoardDto, User user) {
    this.title = postBoardDto.getTitle();
    this.contents = postBoardDto.getContents();
    this.user = user;
  }
}
