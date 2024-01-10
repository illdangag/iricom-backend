package com.illdangag.iricom.server.service.board;

import com.illdangag.iricom.server.data.request.BoardInfoCreate;
import com.illdangag.iricom.server.data.response.BoardInfo;
import com.illdangag.iricom.server.exception.IricomException;
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

@DisplayName("service: 게시판 - 생성")
@Slf4j
@Transactional
public class BoardServiceCreateTest extends IricomTestSuite {
    @Autowired
    private BoardService boardService;

    @Autowired
    public BoardServiceCreateTest(ApplicationContext context) {
        super(context);

        init();
    }

    @Test
    @DisplayName("생성")
    public void createBoard() throws Exception {
        String accountId = getAccountId(systemAdmin);
        BoardInfoCreate boardInfoCreate = BoardInfoCreate.builder()
                .title("new board")
                .description("description")
                .build();

        BoardInfo boardInfo = boardService.createBoardInfo(accountId, boardInfoCreate);

        Assertions.assertEquals("new board", boardInfo.getTitle());
        Assertions.assertEquals("description", boardInfo.getDescription());
        Assertions.assertTrue(boardInfo.getEnabled());
    }

    @Test
    @DisplayName("제목을 빈 문자열로 설정")
    public void emptyTitle() throws Exception {
        String accountId = getAccountId(systemAdmin);
        BoardInfoCreate boardInfoCreate = BoardInfoCreate.builder()
                .title("")
                .description("description")
                .build();

        ConstraintViolationException exception = Assertions.assertThrows(ConstraintViolationException.class, () -> {
            boardService.createBoardInfo(accountId, boardInfoCreate);
        });

        Assertions.assertNotNull(exception);
    }

    @Test
    @DisplayName("제목을 긴 문자열로 설정")
    public void overflowTitle() throws Exception {
        String accountId = getAccountId(systemAdmin);
        BoardInfoCreate boardInfoCreate = BoardInfoCreate.builder()
                .title("012345678901234567890123456789012345678901234567890")
                .description("description")
                .build();

        ConstraintViolationException exception = Assertions.assertThrows(ConstraintViolationException.class, () -> {
            boardService.createBoardInfo(accountId, boardInfoCreate);
        });

        Assertions.assertNotNull(exception);
    }

    @Test
    @DisplayName("설명을 빈 문자열로 설정")
    public void emptyDescription() throws Exception {
        String accountId = getAccountId(systemAdmin);
        BoardInfoCreate boardInfoCreate = BoardInfoCreate.builder()
                .title("new board")
                .description("")
                .build();

        BoardInfo boardInfo = boardService.createBoardInfo(accountId, boardInfoCreate);

        Assertions.assertEquals("", boardInfo.getDescription());
    }

    @Test
    @DisplayName("설명을 긴 문자열로 설정")
    public void overflowDescription() throws Exception {
        String accountId = getAccountId(systemAdmin);
        BoardInfoCreate boardInfoCreate = BoardInfoCreate.builder()
                .title("01234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890")
                .description("")
                .build();

        ConstraintViolationException exception = Assertions.assertThrows(ConstraintViolationException.class, () -> {
            boardService.createBoardInfo(accountId, boardInfoCreate);
        });

        Assertions.assertNotNull(exception);
    }

    @Test
    @DisplayName("비활성화")
    public void disabledBoard() throws Exception {
        String accountId = getAccountId(systemAdmin);
        BoardInfoCreate boardInfoCreate = BoardInfoCreate.builder()
                .title("new create")
                .description("")
                .enabled(false)
                .build();

        BoardInfo boardInfo = boardService.createBoardInfo(accountId, boardInfoCreate);

        Assertions.assertFalse(boardInfo.getEnabled());
    }

    @Test
    @DisplayName("비공개")
    public void undisclosedBoard() throws Exception {
        String accountId = getAccountId(systemAdmin);
        BoardInfoCreate boardInfoCreate = BoardInfoCreate.builder()
                .title("new create")
                .description("")
                .undisclosed(true)
                .build();

        BoardInfo boardInfo = boardService.createBoardInfo(accountId, boardInfoCreate);

        Assertions.assertTrue(boardInfo.getUnDisclosed());
    }

    @Test
    @DisplayName("공지 사항 전용")
    public void notificationOnlyBoard() throws Exception {
        String accountId = getAccountId(systemAdmin);
        BoardInfoCreate boardInfoCreate = BoardInfoCreate.builder()
                .title("new create")
                .description("")
                .notificationOnly(true)
                .build();

        BoardInfo boardInfo = boardService.createBoardInfo(accountId, boardInfoCreate);

        Assertions.assertTrue(boardInfo.getNotificationOnly());
    }

    @Test
    @DisplayName("게시판 관리자 권한으로 게시판 생성")
    public void createBoardByBoardAdmin() throws Exception {
        TestBoardInfo testBoardInfo = TestBoardInfo.builder()
                .title("testBoardInfo").isEnabled(true)
                .adminList(Collections.singletonList(common00))
                .build();

        addTestBoardInfo(testBoardInfo);
        init();

        String accountId = getAccountId(common00);
        BoardInfoCreate boardInfoCreate = BoardInfoCreate.builder()
                .title("new create")
                .description("")
                .notificationOnly(true)
                .build();

        Assertions.assertThrows(IricomException.class, () -> {
            boardService.createBoardInfo(accountId, boardInfoCreate);
        });
    }
}
