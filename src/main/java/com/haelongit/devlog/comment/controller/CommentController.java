package com.haelongit.devlog.comment.controller;

import com.haelongit.devlog.comment.dto.request.CommentSaveRequestDto;
import com.haelongit.devlog.comment.dto.request.CommentUpdateRequestDto;
import com.haelongit.devlog.comment.dto.response.CommentResponseDto;
import com.haelongit.devlog.comment.service.CommentService;
import com.haelongit.devlog.user.entity.User;
import com.haelongit.devlog.user.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/boards/{boardId}/comments") // 게시글 ID를 포함하는 URL 구조
public class CommentController {

    private final CommentService commentService;
    private final UserRepository userRepository;

    /**
     * 댓글 생성
     * POST /api/boards/{boardId}/comments
     *
     * @param boardId 댓글을 달 게시글 ID
     * @param requestDto 댓글 내용 (CommentSaveRequestDto)
     * @param userDetails 현재 로그인한 사용자 정보
     * @return 생성된 댓글 정보 (CommentResponseDto)
     */
    @PostMapping
    public ResponseEntity<CommentResponseDto> createComment(
            @PathVariable Long boardId,
            @Valid @RequestBody CommentSaveRequestDto requestDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        // userDetails의 username으로 실제 User 엔티티를 찾아 ID를 얻는 과정
        // 이 로직은 서비스 계층에서 User 엔티티를 직접 주입받아 처리하는 것이 더 일반적입니다.
        // 현재는 Controller에서 userId를 넘겨주는 방식으로 유지합니다.
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. Username: " + userDetails.getUsername()));
        Long userId = user.getId();

        CommentResponseDto responseDto = commentService.createComment(boardId, userId, requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED); // 201 Created
    }

    /**
     * 특정 게시글의 모든 댓글 조회
     * GET /api/boards/{boardId}/comments
     *
     * @param boardId 댓글을 조회할 게시글 ID
     * @return 해당 게시글의 댓글 목록 (List<CommentResponseDto>)
     */
    @GetMapping
    public ResponseEntity<List<CommentResponseDto>> getCommentsByBoardId(
            @PathVariable Long boardId
    ) {
        List<CommentResponseDto> comments = commentService.getCommentsByBoardId(boardId);
        return new ResponseEntity<>(comments, HttpStatus.OK); // 200 OK
    }

    /**
     * 댓글 수정
     * PUT /api/boards/{boardId}/comments/{commentId}
     *
     * @param boardId (URL 경로에 포함되지만, 서비스 로직에서는 직접 사용되지 않을 수 있음 - RESTful URL 일관성 유지)
     * @param commentId 수정할 댓글 ID
     * @param requestDto 수정할 댓글 내용 (CommentUpdateRequestDto)
     * @param userDetails 현재 로그인한 사용자 정보
     * @return 수정된 댓글 정보 (CommentResponseDto)
     */
    @PutMapping("/{commentId}")
    public ResponseEntity<CommentResponseDto> updateComment(
            @PathVariable Long boardId, // boardId는 RESTful URL 구조를 위해 받습니다.
            @PathVariable Long commentId,
            @Valid @RequestBody CommentUpdateRequestDto requestDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        // userDetails의 username으로 실제 User 엔티티를 찾아 ID를 얻는 과정
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. Username: " + userDetails.getUsername()));
        Long userId = user.getId();

        try {
            CommentResponseDto responseDto = commentService.updateComment(commentId, userId, requestDto);
            return new ResponseEntity<>(responseDto, HttpStatus.OK); // 200 OK
        } catch (IllegalArgumentException e) {
            // 게시글 컨트롤러와 동일하게 권한이 없을 경우 403 Forbidden 응답
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    /**
     * 댓글 삭제
     * DELETE /api/boards/{boardId}/comments/{commentId}
     *
     * @param boardId (URL 경로에 포함되지만, 서비스 로직에서는 직접 사용되지 않을 수 있음 - RESTful URL 일관성 유지)
     * @param commentId 삭제할 댓글 ID
     * @param userDetails 현재 로그인한 사용자 정보
     * @return 응답 없음 (ResponseEntity<Void>)
     */
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long boardId, // boardId는 RESTful URL 구조를 위해 받습니다.
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        // userDetails의 username으로 실제 User 엔티티를 찾아 ID를 얻는 과정
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. Username: " + userDetails.getUsername()));
        Long userId = user.getId();

        try {
            commentService.deleteComment(commentId, userId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content
        } catch (IllegalArgumentException e) {
            // 게시글 컨트롤러와 동일하게 권한이 없을 경우 403 Forbidden 응답
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}
