package com.evergreen.evergreenserver.domain.board.dto;

import com.evergreen.evergreenserver.domain.board.entity.Board;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BoardResponseDto {

  private Long id;

  private String writer;

  private String title;

  private String contents;

  private LocalDateTime createdAt;

  private LocalDateTime modifiedAt;

  public BoardResponseDto(Board board) {
    this.id = board.getId();
    this.writer = board.getUser().getNickname();
    this.title = board.getTitle();
    this.contents = board.getContents();
    this.createdAt = board.getCreatedAt();
    this.modifiedAt = board.getModifiedAt();
  }
}
