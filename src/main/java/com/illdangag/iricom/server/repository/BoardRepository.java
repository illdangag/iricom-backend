package com.illdangag.iricom.server.repository;

import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.data.entity.Board;

import java.util.List;
import java.util.Optional;

public interface BoardRepository {
    /**
     * 게시판 상태와 무관하게 게시판을 조회
     */
    Optional<Board> getBoard(long id);

    /**
     * 공개 게시판 기준으로 게시판 조회
     */
    Optional<Board> getDisclosedBoard(long id);

    /**
     * 공개 게시판과 사용자가 접근 가능한 게시판 조회
     */
    Optional<Board> getDisclosedBoard(Account account, long id);

    List<Board> getBoardList(String title, int offset, int limit);

    long getBoardCount(String title);

    List<Board> getBoardList(String title, boolean enabled, int offset, int limit);

    long getBoardCount(String title, boolean enabled);

    List<Board> getBoardList(Account account, String title, boolean enabled, int offset, int limit);

    long getBoardCount(Account account, String title, boolean enabled);

    List<Board> getBoardList(Account account, String title, int offset, int limit);

    long getBoardCount(Account account, String title);

    boolean existBoard(List<Long> boardIdList);

    void save(Board board);
}
