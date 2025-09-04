package com.haelongit.devlog.board.dto.request;

import com.haelongit.devlog.board.entity.Board;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BoardSaveRequestDto {
    private String title;
    private String content;
    private String author;

    public Board toEntity() {
        return new Board(title, content, author);
    }
}
