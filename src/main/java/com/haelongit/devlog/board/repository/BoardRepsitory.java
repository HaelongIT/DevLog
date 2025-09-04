package com.haelongit.devlog.board.repository;

import com.haelongit.devlog.board.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardRepsitory extends JpaRepository<Board, Long> {
}
