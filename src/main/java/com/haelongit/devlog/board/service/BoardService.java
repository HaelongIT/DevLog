package com.haelongit.devlog.board.service;

import com.haelongit.devlog.board.entity.Board;
import com.haelongit.devlog.board.repository.BoardRepsitory;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepsitory boardRepository;

    /**
     * 모든 게시글을 조회하는 메서드
     * @return 게시글 목록
     */
    public List<Board> findAll() {
        return boardRepository.findAll();
    }

    /**
     * 애플리케이션 시작 시 테스트용 데이터 삽입
     */
    @PostConstruct
    public void init() {
        boardRepository.save(new Board("첫 번째 게시글", "이것은 첫 번째 게시글의 내용입니다.", "작성자A"));
        boardRepository.save(new Board("두 번째 게시글", "두 번째 게시글의 흥미로운 이야기", "작성자B"));
        boardRepository.save(new Board("세 번째 게시글", "세 번째 게시글에 대한 안내", "작성자C"));
    }
}
