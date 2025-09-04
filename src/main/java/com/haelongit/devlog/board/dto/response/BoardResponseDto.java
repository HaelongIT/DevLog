package com.haelongit.devlog.board.dto.response;

import com.haelongit.devlog.board.entity.Board;
import lombok.Getter;

@Getter
public class BoardResponseDto {
    private final Long id;
    private final String title;
    private final String content;
    private final String author;

    public BoardResponseDto(Board entity) {
        this.id = entity.getId();
        this.title = entity.getTitle();
        this.content = entity.getContent();
        this.author = entity.getAuthor();
    }
}
