package com.haelongit.devlog.comment.dto.response;

import com.haelongit.devlog.comment.entity.Comment;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 댓글 조회/응답 시 사용되는 DTO
 * 백엔드에서 프론트엔드로 댓글 정보를 보낼 때 사용됩니다.
 */
@Getter
@Builder // 빌더 패턴으로 객체 생성
public class CommentResponseDto {

    private Long id; // 댓글 ID
    private String content; // 댓글 내용
    private String authorUsername; // 댓글 작성자 유저네임
    private LocalDateTime createdAt; // 댓글 작성 시간
    private Long boardId; // 댓글이 속한 게시글 ID

    /**
     * Comment 엔티티를 CommentResponseDto로 변환하는 정적 팩토리 메서드
     *
     * @param comment 변환할 Comment 엔티티
     * @return 변환된 CommentResponseDto
     */
    public static CommentResponseDto fromEntity(Comment comment) {
        return CommentResponseDto.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .authorUsername(comment.getUser().getUsername()) // 작성자의 유저네임만 반환
                .createdAt(comment.getCreatedAt())
                .boardId(comment.getBoard().getId())
                .build();
    }
}
