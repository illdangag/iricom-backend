package com.illdangag.iricom.server.repository;

import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.data.entity.Board;

import java.util.List;
import java.util.Optional;

public interface BoardRepository {
    Optional<Board> getBoard(long id);

    Optional<Board> getDisclosedBoard(long id);

    Optional<Board> getDisclosedBoard(Account account, long id);

    List<Board> getBoardList(String title, int offset, int limit);

    long getBoardCount(String title);

    List<Board> getBoardList(String title, boolean enabled, int offset, int limit);

    long getBoardCount(String title, boolean enabled);

    List<Board> getBoardList(Account account, String title, boolean enabled, int offset, int limit);

    long getBoardCount(Account account, String title, boolean enabled);

    List<Board> getBoardList(Account account, String title, int offset, int limit);

    long getBoardCount(Account account, String title);

    void save(Board board);
}
