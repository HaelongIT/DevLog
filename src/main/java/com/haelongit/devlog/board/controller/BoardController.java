package com.haelongit.devlog.board.controller;

import com.haelongit.devlog.board.dto.request.BoardSaveRequestDto;
import com.haelongit.devlog.board.dto.request.BoardUpdateRequestDto;
import com.haelongit.devlog.board.dto.response.BoardResponseDto;
import com.haelongit.devlog.board.entity.Board;
import com.haelongit.devlog.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    // 새 게시글 저장 (POST)
    @PostMapping("/api/boards")
    public ResponseEntity<Board> save(@RequestBody BoardSaveRequestDto requestDto) {
//        return boardService.save(requestDto);
        Board savedBoard = boardService.save(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedBoard);
    }

    // 전체 목록 조회 (GET)
    @GetMapping("/api/boards")
    public List<BoardResponseDto> findAll() {
        return boardService.findAll();
    }

    // 상세 내용 조회 (GET)
    @GetMapping("/api/boards/{id}")
    public BoardResponseDto findById(@PathVariable Long id) {
        return boardService.findById(id);
    }

    // 게시글 수정 (PUT)
    @PutMapping("/api/boards/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody BoardUpdateRequestDto requestDto) {
//        return boardService.update(id, requestDto);
        boardService.update(id, requestDto);
        return ResponseEntity.ok().build();
    }

    // 게시글 삭제 (DELETE)
    @DeleteMapping("/api/boards/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boardService.delete(id);
//        return id;
        return ResponseEntity.ok().build();
    }
}
