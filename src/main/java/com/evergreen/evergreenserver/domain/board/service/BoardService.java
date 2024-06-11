package com.evergreen.evergreenserver.domain.board.service;

import com.evergreen.evergreenserver.domain.board.dto.PostBoardDto;
import com.evergreen.evergreenserver.domain.board.entity.Board;
import com.evergreen.evergreenserver.domain.board.repository.BoardRepository;
import com.evergreen.evergreenserver.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BoardService {

  private final BoardRepository boardRepository;


  public void postBoard(PostBoardDto postBoardDto, User user) {
    boardRepository.save(new Board(postBoardDto, user));
  }
}
