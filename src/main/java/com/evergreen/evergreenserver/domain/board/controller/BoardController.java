package com.evergreen.evergreenserver.domain.board.controller;

import com.evergreen.evergreenserver.domain.board.dto.BoardResponseDto;
import com.evergreen.evergreenserver.domain.board.dto.PagedBoardsResponseDto;
import com.evergreen.evergreenserver.domain.board.dto.PostBoardDto;
import com.evergreen.evergreenserver.domain.board.service.BoardService;
import com.evergreen.evergreenserver.global.filter.UserDetailsImpl;
import com.evergreen.evergreenserver.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/boards")
public class BoardController {

  private final BoardService boardService;

  @PostMapping()
  public ResponseEntity<ApiResponse> postBoard(@RequestBody PostBoardDto postBoardDto,
      @AuthenticationPrincipal
      UserDetailsImpl userDetails) {
    boardService.postBoard(postBoardDto, userDetails.getUser());

    return ResponseEntity.ok().body(new ApiResponse<>("게시물 작성 완료", HttpStatus.OK.value()));
  }

  @GetMapping()
  public ResponseEntity<ApiResponse> getPagedBoards(@PageableDefault Pageable pageable) {
    Page<PagedBoardsResponseDto> page = boardService.getPagedBoards(pageable);

    return ResponseEntity.ok().body(new ApiResponse("게시물 목록 조회 완료", HttpStatus.OK.value(), page));
  }

  @GetMapping("/{boardId}")
  public ResponseEntity<ApiResponse> getBoard(@PathVariable Long boardId) {
    BoardResponseDto boardResponseDto = boardService.getBoard(boardId);

    return ResponseEntity.ok()
        .body(new ApiResponse("게시물 조회 완료", HttpStatus.OK.value(), boardResponseDto));
  }

  @PutMapping("/{boardId}")
  public ResponseEntity<ApiResponse> updateBoard(@PathVariable Long boardId,
      @RequestBody PostBoardDto postBoardDto,
      @AuthenticationPrincipal UserDetailsImpl userDetails) {
    BoardResponseDto boardResponseDto = boardService.updateBoard(boardId, postBoardDto,
        userDetails.getUser());

    return ResponseEntity.ok()
        .body(new ApiResponse("게시물 수정 완료", HttpStatus.OK.value(), boardResponseDto));
  }

  @DeleteMapping("/{boardId}")
  public ResponseEntity<ApiResponse> deleteBoard(@PathVariable Long boardId,
      @AuthenticationPrincipal UserDetailsImpl userDetails) {
    boardService.deleteBoard(boardId, userDetails.getUser());

    return ResponseEntity.ok().body(new ApiResponse("게시물 삭제 완료", HttpStatus.OK.value()));
  }

}
