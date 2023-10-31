package com.illdangag.iricom.server.repository;

import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.data.entity.Board;
import com.illdangag.iricom.server.data.entity.BoardAdmin;

import java.util.List;
import java.util.Optional;

public interface BoardRepository {
    /**
     * 게시판 조회
     */
    Optional<Board> getBoard(Long id);

    /**
     * 게시판 목록 조회
     */
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

    /**
     * 게시판 목록에 대해서 게시판 관리자 조회
     */
    List<BoardAdmin> getBoardAdminList(List<Board> boardList);

    /**
     * 계정이 게시판 관리자로 등록된 게시판 관리자 조회
     */
    List<BoardAdmin> getBoardAdminList(Account account);

    /**
     * 게시판에 계정이 게시판 관리자 여부 조회
     */
    List<BoardAdmin> getBoardAdminList(Board board, Account account);

    /**
     * 게시판 관리자 정보 저장
     */
    void save(BoardAdmin boardAdmin);

    /**
     * 게시판 관리자 정보 삭제
     */
    void delete(BoardAdmin boardAdmin);
}
