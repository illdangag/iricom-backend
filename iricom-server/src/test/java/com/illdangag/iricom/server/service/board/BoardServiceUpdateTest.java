package com.illdangag.iricom.server.service.board;

import com.illdangag.iricom.server.data.request.BoardInfoUpdate;
import com.illdangag.iricom.server.data.response.BoardInfo;
import com.illdangag.iricom.server.service.BoardService;
import com.illdangag.iricom.server.test.IricomTestSuite;
import com.illdangag.iricom.server.test.data.wrapper.TestBoardInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;
import java.util.Collections;

@DisplayName("service: 게시판 수정")
@Slf4j
@Transactional
public class BoardServiceUpdateTest extends IricomTestSuite {
    @Autowired
    private BoardService boardService;

    private final TestBoardInfo testBoardInfo00 = TestBoardInfo.builder()
            .title("testBoardInfo00").isEnabled(true).undisclosed(false)
            .adminList(Collections.singletonList(allBoardAdmin)).build();

    @Autowired
    public BoardServiceUpdateTest(ApplicationContext context) {
        super(context);

        addTestBoardInfo(testBoardInfo00);

        init();
    }

    @Test
    @DisplayName("수정")
    public void updateBoard() throws Exception {
        String accountId = getAccountId(systemAdmin);
        String boardId = getBoardId(testBoardInfo00);

        BoardInfoUpdate boardInfoUpdate = BoardInfoUpdate.builder()
                .title("update title")
                .description("update description")
                .build();

        BoardInfo boardInfo = this.boardService.updateBoardInfo(accountId, boardId, boardInfoUpdate);

        Assertions.assertEquals(boardId, boardInfo.getId());
        Assertions.assertEquals("update title", boardInfo.getTitle());
        Assertions.assertEquals("update description", boardInfo.getDescription());
        Assertions.assertTrue(boardInfo.getEnabled());
        Assertions.assertFalse(boardInfo.getUnDisclosed());
        Assertions.assertFalse(boardInfo.getNotificationOnly());
    }

    @Test
    @DisplayName("제목을 빈 문자열로 설정")
    public void emptyTitle() throws Exception {
        String accountId = getAccountId(systemAdmin);
        String boardId = getBoardId(testBoardInfo00);

        BoardInfoUpdate boardInfoUpdate = BoardInfoUpdate.builder()
                .title("")
                .build();

        Assertions.assertThrows(ConstraintViolationException.class, () -> {
            this.boardService.updateBoardInfo(accountId, boardId, boardInfoUpdate);
        });
    }

    @Test
    @DisplayName("제목을 긴 문자열로 설정")
    public void overflowTitle() throws Exception {
        String accountId = getAccountId(systemAdmin);
        String boardId = getBoardId(testBoardInfo00);

        BoardInfoUpdate boardInfoUpdate = BoardInfoUpdate.builder()
                .title("012345678901234567890123456789012345678901234567890")
                .build();

        Assertions.assertThrows(ConstraintViolationException.class, () -> {
            this.boardService.updateBoardInfo(accountId, boardId, boardInfoUpdate);
        });
    }

    @Test
    @DisplayName("설명을 빈 문자열로 설정")
    public void emptyDescription() throws Exception {
        String accountId = getAccountId(systemAdmin);
        String boardId = getBoardId(testBoardInfo00);

        BoardInfoUpdate boardInfoUpdate = BoardInfoUpdate.builder()
                .description("")
                .build();

        BoardInfo boardInfo = this.boardService.updateBoardInfo(accountId, boardId, boardInfoUpdate);

        Assertions.assertEquals("", boardInfo.getDescription());
    }

    @Test
    @DisplayName("설명을 긴 문자열로 설정")
    public void overflowDescription() throws Exception {
        String accountId = getAccountId(systemAdmin);
        String boardId = getBoardId(testBoardInfo00);

        BoardInfoUpdate boardInfoUpdate = BoardInfoUpdate.builder()
                .description("01234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890")
                .build();

        Assertions.assertThrows(ConstraintViolationException.class, () -> {
            this.boardService.updateBoardInfo(accountId, boardId, boardInfoUpdate);
        });
    }

    @Test
    @DisplayName("비활성화")
    public void disabledBoard() throws Exception {
        String accountId = getAccountId(systemAdmin);
        String boardId = getBoardId(testBoardInfo00);

        BoardInfoUpdate boardInfoUpdate = BoardInfoUpdate.builder()
                .enabled(false)
                .build();

        BoardInfo boardInfo = this.boardService.updateBoardInfo(accountId, boardId, boardInfoUpdate);

        Assertions.assertFalse(boardInfo.getEnabled());
    }

    @Test
    @DisplayName("비공개")
    public void undisclosedBoard() throws Exception {
        String accountId = getAccountId(systemAdmin);
        String boardId = getBoardId(testBoardInfo00);

        BoardInfoUpdate boardInfoUpdate = BoardInfoUpdate.builder()
                .undisclosed(true)
                .build();

        BoardInfo boardInfo = this.boardService.updateBoardInfo(accountId, boardId, boardInfoUpdate);

        Assertions.assertTrue(boardInfo.getUnDisclosed());
    }

    @Test
    @DisplayName("공지 사항 전용")
    public void notificationOnlyBoard() throws Exception {
        String accountId = getAccountId(systemAdmin);
        String boardId = getBoardId(testBoardInfo00);

        BoardInfoUpdate boardInfoUpdate = BoardInfoUpdate.builder()
                .notificationOnly(true)
                .build();

        BoardInfo boardInfo = this.boardService.updateBoardInfo(accountId, boardId, boardInfoUpdate);

        Assertions.assertTrue(boardInfo.getNotificationOnly());
    }
}
