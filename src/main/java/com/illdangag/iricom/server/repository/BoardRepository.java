package com.illdangag.iricom.server.repository;

import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.data.entity.Board;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface BoardRepository {
    List<Board> getBoardList(long id);

    List<Board> getBoardList(String id);

    long getBoardCount();

    List<Board> getBoardList(String title, int offset, int limit);

    long getBoardCount(String title);

    List<Board> getBoardList(String title, boolean enabled, int offset, int limit);

    long getBoardCount(String title, boolean enabled);

    List<Board> getBoardList(Account account, String title, boolean enabled, int offset, int limit);

    long getBoardCount(Account account, String title, boolean enabled);

    List<Board> getBoardList(Account account, String title, int offset, int limit);

    long getBoardCount(Account account, String title);

    Optional<Board> getBoard(String id);

    Optional<Board> getBoard(long id);

    void save(Board board);
}
