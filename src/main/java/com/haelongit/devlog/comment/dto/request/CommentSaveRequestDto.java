package com.haelongit.devlog.comment.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 댓글 생성 요청 시 사용되는 DTO
 * 프론트엔드에서 댓글 내용을 백엔드로 보낼 때 사용됩니다.
 */
@Getter
@Setter
@NoArgsConstructor
public class CommentSaveRequestDto {

    @NotBlank(message = "댓글 내용은 필수입니다.") // 댓글 내용은 NULL, 빈 문자열, 공백만 있는 문자열을 허용하지 않습니다.
    @Size(max = 500, message = "댓글은 500자를 초과할 수 없습니다.") // 댓글 최대 길이 제한 (DB의 Column 길이와 일치)
    private String content; // 댓글 내용
}
