package com.haelongit.devlog.board.service;

import com.haelongit.devlog.board.dto.request.BoardSaveRequestDto;
import com.haelongit.devlog.board.dto.request.BoardUpdateRequestDto;
import com.haelongit.devlog.board.dto.response.BoardResponseDto;
import com.haelongit.devlog.board.entity.Board;
import com.haelongit.devlog.board.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;

    // Create
    @Transactional
    public Board save(BoardSaveRequestDto requestDto, String authorUsername) {
        // 수정된 DTO의 toEntity 메서드를 사용하여 author 정보를 주입합니다.
        return boardRepository.save(requestDto.toEntity(authorUsername));
    }

    // Read1 - findAll (변경 없음)
    @Transactional(readOnly = true)
    public List<BoardResponseDto> findAll() {
        return boardRepository.findAll().stream()
                .map(BoardResponseDto::new)
                .collect(Collectors.toList());
    }

    // Read2 - findById (변경 없음)
    @Transactional(readOnly = true)
    public BoardResponseDto findById(Long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다. id=" + id));
        return new BoardResponseDto(board);
    }

    // Update
    @Transactional
    public void update(Long id, BoardUpdateRequestDto requestDto, String authorUsername) {
        // 게시글을 찾으면서 소유권도 함께 확인합니다.
        Board board = findBoardAndCheckOwnership(id, authorUsername);
        board.update(requestDto.getTitle(), requestDto.getContent());
    }

    // Delete
    @Transactional
    public void delete(Long id, String authorUsername) {
        // 게시글을 찾으면서 소유권도 함께 확인합니다.
        Board board = findBoardAndCheckOwnership(id, authorUsername);
        boardRepository.delete(board);
    }

    // 소유권 확인을 위한 private 헬퍼 메서드
    private Board findBoardAndCheckOwnership(Long id, String username) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다. id=" + id));

        if (!board.getAuthor().equals(username)) {
            throw new IllegalArgumentException("게시글에 대한 권한이 없습니다.");
        }
        return board;
    }
}
