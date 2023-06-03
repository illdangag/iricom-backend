package com.illdangag.iricom.server.repository;

import com.illdangag.iricom.server.data.entity.Board;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface BoardRepository {
    List<Board> getBoardList(long id);

    List<Board> getBoardList(String id);

    long getBoardCount();

    List<Board> getBoardList(String containTitle, int offset, int limit);

    long getBoardCount(String containTitle);

    List<Board> getBoardList(String likeTitle, boolean enabled, int offset, int limit);

    long getBoardCount(String containTitle, boolean enabled);

    Optional<Board> getBoard(String id);

    Optional<Board> getBoard(long id);

    void save(Board board);

    void saveAll(Collection<Board> boards);
}
