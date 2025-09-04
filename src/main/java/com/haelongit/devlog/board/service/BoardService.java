package com.haelongit.devlog.board.service;

import com.haelongit.devlog.board.dto.request.BoardSaveRequestDto;
import com.haelongit.devlog.board.dto.response.BoardResponseDto;
import com.haelongit.devlog.board.entity.Board;
import com.haelongit.devlog.board.repository.BoardRepsitory;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepsitory boardRepository;

    @Transactional
    public Long save(BoardSaveRequestDto requestDto) {
        return boardRepository.save(requestDto.toEntity()).getId();
    }

    @Transactional(readOnly = true)
    public BoardResponseDto findById(Long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다. id=" + id));
        return new BoardResponseDto(board);
    }

    @Transactional(readOnly = true)
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
