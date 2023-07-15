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

    Optional<BoardAdmin> getBoardAdmin(Board board, Account account);

    void save(BoardAdmin boardAdmin);
}
