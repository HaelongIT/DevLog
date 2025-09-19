package com.haelongit.devlog.comment.service;

import com.haelongit.devlog.board.entity.Board;
import com.haelongit.devlog.board.repository.BoardRepository;
import com.haelongit.devlog.comment.dto.request.CommentSaveRequestDto;
import com.haelongit.devlog.comment.dto.request.CommentUpdateRequestDto;
import com.haelongit.devlog.comment.dto.response.CommentResponseDto;
import com.haelongit.devlog.comment.entity.Comment;
import com.haelongit.devlog.comment.repository.CommentRepository;
import com.haelongit.devlog.user.entity.User;
import com.haelongit.devlog.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 읽기 전용 트랜잭션 기본 설정
public class CommentService {

    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository; // 게시글 존재 여부 확인용
    private final UserRepository userRepository; // 사용자 존재 여부 확인용

    /**
     * 댓글 생성
     *
     * @param boardId 댓글을 달 게시글 ID
     * @param userId 댓글 작성자 ID
     * @param requestDto 댓글 내용
     * @return 생성된 댓글의 응답 DTO
     */
    @Transactional // 쓰기 작업이므로 개별 트랜잭션 설정
    public CommentResponseDto createComment(Long boardId, Long userId, CommentSaveRequestDto requestDto) {
        // 1. 게시글 존재 여부 확인
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다. ID: " + boardId));

        // 2. 사용자 존재 여부 확인
        // SecurityContext에서 이미 인증된 사용자이므로, findById로 User 엔티티를 가져오는 것은 비즈니스 로직상 필요할 수 있지만,
        // 권한 체크 목적이라면 조금 더 가볍게 접근할 수도 있습니다.
        // 현재는 Controller에서 userId를 받아와 User 엔티티를 찾아 Comment에 연결합니다.
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. ID: " + userId));

        // 3. Comment 엔티티 생성 및 저장
        Comment comment = Comment.builder()
                .board(board)
                .user(user)
                .content(requestDto.getContent())
                .build();

        Comment savedComment = commentRepository.save(comment);

        return CommentResponseDto.fromEntity(savedComment);
    }

    /**
     * 특정 게시글의 모든 댓글 조회
     *
     * @param boardId 댓글을 조회할 게시글 ID
     * @return 해당 게시글의 댓글 목록 DTO
     */
    public List<CommentResponseDto> getCommentsByBoardId(Long boardId) {
        // 1. 게시글 존재 여부 확인
        boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다. ID: " + boardId));

        // 2. 댓글 조회 및 DTO 변환
        return commentRepository.findByBoardIdOrderByCreatedAtAsc(boardId)
                .stream()
                .map(CommentResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 댓글 수정
     *
     * @param commentId 수정할 댓글 ID
     * @param userId 댓글 작성자 ID (권한 확인용)
     * @param requestDto 수정할 댓글 내용
     * @return 수정된 댓글의 응답 DTO
     * @throws IllegalArgumentException 댓글을 찾을 수 없거나 수정 권한이 없을 경우
     */
    @Transactional
    public CommentResponseDto updateComment(Long commentId, Long userId, CommentUpdateRequestDto requestDto) {
        // 1. 댓글 존재 여부 및 소유권 확인 (findCommentAndCheckOwnership 헬퍼 메서드 사용)
        Comment comment = findCommentAndCheckOwnership(commentId, userId);

        // 2. 댓글 내용 업데이트 (더티 체킹 활용)
        // 빌더 패턴으로 새로운 객체를 생성하는 대신, 엔티티의 updateContent 메서드를 호출하여 변경 감지(더티 체킹)를 활용합니다.
        comment.updateContent(requestDto.getContent());

        // Comment 엔티티의 updateContent 메서드를 호출하면 @Transactional에 의해 변경 감지(더티 체킹)되어
        // 별도의 save() 호출 없이도 트랜잭션 종료 시점에 변경 사항이 DB에 반영됩니다.
        return CommentResponseDto.fromEntity(comment);
    }

    /**
     * 댓글 삭제
     *
     * @param commentId 삭제할 댓글 ID
     * @param userId 댓글 작성자 ID (권한 확인용)
     * @throws IllegalArgumentException 댓글을 찾을 수 없거나 삭제 권한이 없을 경우
     */
    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        // 1. 댓글 존재 여부 및 소유권 확인 (findCommentAndCheckOwnership 헬퍼 메서드 사용)
        Comment comment = findCommentAndCheckOwnership(commentId, userId);

        // 2. 댓글 삭제
        commentRepository.delete(comment);
    }

    /**
     * 댓글 존재 여부와 소유권 확인을 위한 헬퍼 메서드
     *
     * @param commentId 확인할 댓글 ID
     * @param userId 현재 로그인한 사용자 ID
     * @return 유효하며 소유권이 있는 댓글 엔티티
     * @throws IllegalArgumentException 댓글을 찾을 수 없거나 소유권이 없을 경우
     */
    private Comment findCommentAndCheckOwnership(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글을 찾을 수 없습니다. ID: " + commentId));

        // 댓글 작성자의 ID와 현재 로그인한 사용자의 ID를 비교하여 소유권 확인
        if (!comment.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("댓글에 대한 권한이 없습니다.");
        }
        return comment;
    }
}
