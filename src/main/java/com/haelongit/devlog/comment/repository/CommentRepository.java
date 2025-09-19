package com.haelongit.devlog.comment.repository;

import com.haelongit.devlog.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    /**
     * 특정 게시글(Board)에 속한 모든 댓글을 조회합니다.
     * 게시글 ID와 함께 댓글 생성 시간(createdAt)을 기준으로 오름차순 정렬하여 반환합니다.
     *
     * @param boardId 댓글을 조회할 게시글의 ID
     * @return 해당 게시글의 댓글 목록 (생성 시간 오름차순)
     */
    List<Comment> findByBoardIdOrderByCreatedAtAsc(Long boardId);

    /**
     * 특정 댓글 ID와 사용자 ID로 댓글을 찾아, 해당 댓글이 특정 사용자가 작성한 것인지 확인합니다.
     * (댓글 수정/삭제 시 권한 확인용)
     *
     * @param commentId 찾을 댓글의 ID
     * @param userId 댓글 작성자의 ID
     * @return 해당 조건을 만족하는 댓글 (Optional)
     */
    Optional<Comment> findByIdAndUserId(Long commentId, Long userId);
}
