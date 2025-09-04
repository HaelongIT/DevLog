package com.haelongit.devlog.board.controller;

import com.haelongit.devlog.board.entity.Board;
import com.haelongit.devlog.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    /**
     * 게시글 목록 조회 API
     * @return 게시글 목록 JSON
     */
    @GetMapping("/api/boards")
    public List<Board> getBoards() {
        return boardService.findAll();
    }
}
