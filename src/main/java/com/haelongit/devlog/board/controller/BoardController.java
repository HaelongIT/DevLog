package com.haelongit.devlog.board.controller;

import com.haelongit.devlog.board.dto.request.BoardSaveRequestDto;
import com.haelongit.devlog.board.dto.request.BoardUpdateRequestDto;
import com.haelongit.devlog.board.dto.response.BoardResponseDto;
import com.haelongit.devlog.board.entity.Board;
import com.haelongit.devlog.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
// 공통 URL 경로를 클래스 레벨에서 설정
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    // 새 게시글 저장 (POST)
    @PostMapping
    public ResponseEntity<BoardResponseDto> save(
            @RequestBody BoardSaveRequestDto requestDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Board savedBoard = boardService.save(requestDto, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(new BoardResponseDto(savedBoard));
    }

    // 전체 목록 조회 (GET) - 변경 없음
    @GetMapping
    public List<BoardResponseDto> findAll() {
        return boardService.findAll();
    }

    // 상세 내용 조회 (GET) - 변경 없음
    @GetMapping("/{id}")
    public BoardResponseDto findById(@PathVariable Long id) {
        return boardService.findById(id);
    }

    // 게시글 수정 (PUT)
    @PutMapping("/{id}")
    public ResponseEntity<Void> update(
            @PathVariable Long id,
            @RequestBody BoardUpdateRequestDto requestDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        try {
            boardService.update(id, requestDto, userDetails.getUsername());
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            // 소유권이 없을 경우 서비스에서 던진 예외를 처리하여 403 Forbidden 응답
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    // 게시글 삭제 (DELETE)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        try {
            boardService.delete(id, userDetails.getUsername());
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            // 소유권이 없을 경우 서비스에서 던진 예외를 처리하여 403 Forbidden 응답
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}
