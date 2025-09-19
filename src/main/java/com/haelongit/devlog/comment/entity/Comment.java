package com.haelongit.devlog.comment.entity;

import com.haelongit.devlog.board.entity.Board;
import com.haelongit.devlog.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA는 기본 생성자가 필요하지만, PROTECTED로 설정하여 외부에서의 무분별한 객체 생성을 방지합니다.
@AllArgsConstructor(access = AccessLevel.PRIVATE) // 빌더 패턴을 위한 전체 생성자 (PRIVATE으로 설정하여 직접 사용 방지)
@Builder // 빌더 패턴 사용을 위한 어노테이션
@EntityListeners(AuditingEntityListener.class) // @CreatedDate 사용을 위한 리스너
@Table(name = "comments") // 데이터베이스 테이블 이름을 "comments"로 명시합니다.
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // N:1 관계 (여러 댓글이 하나의 게시글에 속함)
    @JoinColumn(name = "board_id", nullable = false) // 외래 키는 "board_id" 컬럼에 매핑됩니다. (NULL 불가능)
    private Board board; // 해당 댓글이 속한 게시글

    @ManyToOne(fetch = FetchType.LAZY) // N:1 관계 (여러 댓글이 하나의 사용자에 의해 작성될 수 있음)
    @JoinColumn(name = "user_id", nullable = false) // 외래 키는 "user_id" 컬럼에 매핑됩니다. (NULL 불가능)
    private User user; // 댓글 작성자

    @Column(nullable = false, length = 500) // 댓글 내용은 NULL 불가능하며, 최대 500자까지 허용합니다.
    private String content; // 댓글 내용

    @CreatedDate // 엔티티가 생성될 때 자동으로 현재 시간을 저장합니다.
    @Column(name = "created_at", updatable = false, nullable = false) // 한 번 생성되면 수정될 수 없으며, NULL 불가능합니다.
    private LocalDateTime createdAt; // 댓글 생성 시간

    @LastModifiedDate // <<< 이 라인을 추가합니다.
    @Column(name = "updated_at", nullable = false) // <<< nullable = false 확인
    private LocalDateTime updatedAt;

    // 댓글 내용 수정 메서드
    public void updateContent(String content) {
        this.content = content;
    }

    // TODO: 추후 대댓글 기능 추가 시 parent_comment_id 필드 추가 고려
    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "parent_comment_id")
    // private Comment parentComment;
}
