package com.illdangag.iricom.server.repository;

import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.data.entity.Board;

import java.util.List;
import java.util.Optional;

public interface BoardRepository {
    /**
     * 게시판 조회
     */
    Optional<Board> getBoard(Long id);

    List<Board> getBoardList(List<Long> idList);

    /**
     * 게시판 저장
     */
    void save(Board board);

    /**
     * 게시판 목록 조회
     */
    List<Board> getBoardList(Account account, String title, Boolean enabled, Integer offset, Integer limit);

    /**
     * 게시판 목록 수
     */
    long getBoardCount(Account account, String title, Boolean enabled);
}
