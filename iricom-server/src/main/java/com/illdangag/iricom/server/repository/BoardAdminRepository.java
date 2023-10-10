package com.illdangag.iricom.server.repository;

import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.data.entity.Board;
import com.illdangag.iricom.server.data.entity.BoardAdmin;

import java.util.List;
import java.util.Optional;

public interface BoardAdminRepository {
    List<BoardAdmin> getBoardAdminList(List<Board> boardList);

    List<BoardAdmin> getBoardAdminList(Account account, boolean deleted);

    List<BoardAdmin> getBoardAdminList(Board board, Account account);

    Optional<BoardAdmin> getEnableBoardAdmin(Board board, Account account);

    List<BoardAdmin> getLastBoardAdminList(Account account);

    Optional<BoardAdmin> getBoardAdmin(Board board, Account account);

    Optional<BoardAdmin> getLastBoardAdmin(Account account, Board board, boolean deleted);

    List<BoardAdmin> getLastBoardAdminList(Account account, boolean deleted, int offset, int limit);

    long getLastBoardAdminCount(Account account, boolean deleted);

    void save(BoardAdmin boardAdmin);
}
