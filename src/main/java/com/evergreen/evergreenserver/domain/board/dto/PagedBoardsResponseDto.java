package com.evergreen.evergreenserver.domain.board.dto;

import com.evergreen.evergreenserver.domain.board.entity.Board;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PagedBoardsResponseDto {

  private Long id;

  private String writer;

  private String title;

  private LocalDateTime createdAt;

  public PagedBoardsResponseDto(Board board) {
    this.id = board.getId();
    this.writer = board.getUser().getNickname();
    this.title = board.getTitle();
    this.createdAt = board.getCreatedAt();
  }
}