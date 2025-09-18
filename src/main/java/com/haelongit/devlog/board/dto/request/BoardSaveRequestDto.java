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

    // Service 레이어에서 author 정보를 받아 Entity를 생성하도록 변경
    public Board toEntity(String author) {
        return new Board(title, content, author);
    }
}
