package com.evergreen.evergreenserver.domain.board.service;

import com.evergreen.evergreenserver.domain.board.dto.BoardResponseDto;
import com.evergreen.evergreenserver.domain.board.dto.PagedBoardsResponseDto;
import com.evergreen.evergreenserver.domain.board.dto.PostBoardDto;
import com.evergreen.evergreenserver.domain.board.entity.Board;
import com.evergreen.evergreenserver.domain.board.repository.BoardRepository;
import com.evergreen.evergreenserver.domain.user.entity.User;
import com.evergreen.evergreenserver.global.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BoardService {

  private final BoardRepository boardRepository;


  public void postBoard(PostBoardDto postBoardDto, User user) {
    boardRepository.save(new Board(postBoardDto, user));
  }

  public Page<PagedBoardsResponseDto> getPagedBoards(Pageable pageable) {
    pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
        pageable.getSort().and(Sort.by("id")));
    Page<Board> boardPage = boardRepository.findAll(pageable);

    return boardPage.map(PagedBoardsResponseDto::new);
  }

  public BoardResponseDto getBoard(Long boardId) {
    Board board = boardRepository.findById(boardId)
        .orElseThrow(() -> new ApiException("존재하지 않는 게시글 입니다.", HttpStatus.BAD_REQUEST));

    return new BoardResponseDto(board);
  }
}
