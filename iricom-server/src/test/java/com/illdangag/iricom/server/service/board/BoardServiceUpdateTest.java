package com.illdangag.iricom.server.service.board;

import com.illdangag.iricom.server.data.request.BoardInfoUpdate;
import com.illdangag.iricom.server.data.response.BoardInfo;
import com.illdangag.iricom.server.service.BoardService;
import com.illdangag.iricom.server.test.IricomTestSuite;
import com.illdangag.iricom.server.test.data.wrapper.TestBoardInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;
import java.util.Collections;

@DisplayName("service: 게시판 수정")
@Transactional
public class BoardServiceUpdateTest extends IricomTestSuite {
    @Autowired
    private BoardService boardService;

    @Autowired
    public BoardServiceUpdateTest(ApplicationContext context) {
        super(context);
    }

    @Test
    @DisplayName("수정")
    public void updateBoard() throws Exception {
        // 게시판 생성
        TestBoardInfo board = setRandomBoard();

        // 게시판 수정
        BoardInfoUpdate boardInfoUpdate = BoardInfoUpdate.builder()
                .title("update title")
                .description("update description")
                .build();
        BoardInfo boardInfo = this.boardService.updateBoardInfo(systemAdmin.getId(), board.getId(), boardInfoUpdate);

        Assertions.assertEquals(board.getId(), boardInfo.getId());
        Assertions.assertEquals("update title", boardInfo.getTitle());
        Assertions.assertEquals("update description", boardInfo.getDescription());
        Assertions.assertTrue(boardInfo.getEnabled());
        Assertions.assertFalse(boardInfo.getUnDisclosed());
        Assertions.assertFalse(boardInfo.getNotificationOnly());
    }

    @Test
    @DisplayName("제목을 빈 문자열로 설정")
    public void emptyTitle() throws Exception {
        // 게시판 생성
        TestBoardInfo board = setRandomBoard();

        // 게시판 수정
        BoardInfoUpdate boardInfoUpdate = BoardInfoUpdate.builder()
                .title("")
                .build();

        Assertions.assertThrows(ConstraintViolationException.class, () -> {
            this.boardService.updateBoardInfo(systemAdmin.getId(), board.getId(), boardInfoUpdate);
        });
    }

    @Test
    @DisplayName("제목을 긴 문자열로 설정")
    public void overflowTitle() throws Exception {
        // 게시판 생성
        TestBoardInfo board = setRandomBoard();

        // 게시판 수정
        BoardInfoUpdate boardInfoUpdate = BoardInfoUpdate.builder()
                .title(TEXT_10 + TEXT_10 + "0")
                .build();
        Assertions.assertThrows(ConstraintViolationException.class, () -> {
            this.boardService.updateBoardInfo(systemAdmin.getId(), board.getId(), boardInfoUpdate);
        });
    }

    @Test
    @DisplayName("설명을 빈 문자열로 설정")
    public void emptyDescription() throws Exception {
        // 게시판 생성
        TestBoardInfo board = setRandomBoard();

        // 게시판 수정
        BoardInfoUpdate boardInfoUpdate = BoardInfoUpdate.builder()
                .description("")
                .build();
        BoardInfo boardInfo = this.boardService.updateBoardInfo(systemAdmin.getId(), board.getId(), boardInfoUpdate);
        Assertions.assertEquals("", boardInfo.getDescription());
    }

    @Test
    @DisplayName("설명을 긴 문자열로 설정")
    public void overflowDescription() throws Exception {
        // 게시판 생성
        TestBoardInfo board = setRandomBoard();

        // 게시판 수정
        BoardInfoUpdate boardInfoUpdate = BoardInfoUpdate.builder()
                .description(TEXT_100 + "0")
                .build();
        Assertions.assertThrows(ConstraintViolationException.class, () -> {
            this.boardService.updateBoardInfo(systemAdmin.getId(), board.getId(), boardInfoUpdate);
        });
    }

    @Test
    @DisplayName("비활성화")
    public void disabledBoard() throws Exception {
        // 게시판 생성
        TestBoardInfo board = setRandomBoard(Collections.emptyList(), true, false);

        // 게시판 수정
        BoardInfoUpdate boardInfoUpdate = BoardInfoUpdate.builder()
                .enabled(false)
                .build();
        BoardInfo boardInfo = this.boardService.updateBoardInfo(systemAdmin.getId(), board.getId(), boardInfoUpdate);
        Assertions.assertFalse(boardInfo.getEnabled());
    }

    @Test
    @DisplayName("비공개")
    public void undisclosedBoard() throws Exception {
        // 게시판 생성
        TestBoardInfo board = setRandomBoard(Collections.emptyList(), true, false);

        // 게시판 수정
        BoardInfoUpdate boardInfoUpdate = BoardInfoUpdate.builder()
                .undisclosed(true)
                .build();
        BoardInfo boardInfo = this.boardService.updateBoardInfo(systemAdmin.getId(), board.getId(), boardInfoUpdate);
        Assertions.assertTrue(boardInfo.getUnDisclosed());
    }

    @Test
    @DisplayName("공지 사항 전용")
    public void notificationOnlyBoard() throws Exception {
        // 게시판 생성
        TestBoardInfo board = setRandomBoard(Collections.emptyList(), true, false);

        // 게시판 수정
        BoardInfoUpdate boardInfoUpdate = BoardInfoUpdate.builder()
                .notificationOnly(true)
                .build();
        BoardInfo boardInfo = this.boardService.updateBoardInfo(systemAdmin.getId(), board.getId(), boardInfoUpdate);
        Assertions.assertTrue(boardInfo.getNotificationOnly());
    }
}
