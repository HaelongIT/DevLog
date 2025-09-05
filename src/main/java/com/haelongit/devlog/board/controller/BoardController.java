package com.haelongit.devlog.board.controller;

import com.haelongit.devlog.board.dto.request.BoardSaveRequestDto;
import com.haelongit.devlog.board.dto.request.BoardUpdateRequestDto;
import com.haelongit.devlog.board.dto.response.BoardResponseDto;
import com.haelongit.devlog.board.entity.Board;
import com.haelongit.devlog.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @DeleteMapping("/api/boards/{id}")
    public Long delete(@PathVariable Long id) {
        boardService.delete(id);
        return id;
    }

    @PutMapping("/api/boards/{id}")
    public Long update(@PathVariable Long id, @RequestBody BoardUpdateRequestDto requestDto) {
        return boardService.update(id, requestDto);
    }

    @PostMapping("/api/boards")
    public Long save(@RequestBody BoardSaveRequestDto requestDto) {
        return boardService.save(requestDto);
    }

    @GetMapping("/api/boards/{id}")
    public BoardResponseDto findById(@PathVariable Long id) {
        return boardService.findById(id);
    }

    @GetMapping("/api/boards")
    public List<Board> getBoards() {
        return boardService.findAll();
    }
}
