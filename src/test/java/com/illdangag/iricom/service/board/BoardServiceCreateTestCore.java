package com.illdangag.iricom.service.board;

import com.illdangag.iricom.core.data.request.BoardInfoCreate;
import com.illdangag.iricom.core.data.response.BoardInfo;
import com.illdangag.iricom.core.exception.IricomException;
import com.illdangag.iricom.core.service.BoardService;
import com.illdangag.iricom.core.test.data.wrapper.TestAccountInfo;
import com.illdangag.iricom.IricomTestServiceSuite;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;
import java.util.Arrays;

@DisplayName("service: 게시판 - 생성")
@Transactional
public class BoardServiceCreateTestCore extends IricomTestServiceSuite {
    @Autowired
    private BoardService boardService;

    @Autowired
    public BoardServiceCreateTestCore(ApplicationContext context) {
        super(context);
    }

    @Test
    @DisplayName("생성")
    public void createBoard() throws Exception {
        BoardInfoCreate boardInfoCreate = BoardInfoCreate.builder()
                .title("new board")
                .description("description")
                .build();

        BoardInfo boardInfo = boardService.createBoardInfo(systemAdmin.getId(), boardInfoCreate);
        Assertions.assertNotNull(boardInfo);
        Assertions.assertEquals("new board", boardInfo.getTitle());
        Assertions.assertEquals("description", boardInfo.getDescription());
        Assertions.assertTrue(boardInfo.getEnabled());
    }

    @Test
    @DisplayName("제목을 빈 문자열로 설정")
    public void emptyTitle() throws Exception {
        BoardInfoCreate boardInfoCreate = BoardInfoCreate.builder()
                .title("")
                .description("description")
                .build();

        Assertions.assertThrows(ConstraintViolationException.class, () -> {
            boardService.createBoardInfo(systemAdmin.getId(), boardInfoCreate);
        });
    }

    @Test
    @DisplayName("제목을 긴 문자열로 설정")
    public void overflowTitle() throws Exception {
        BoardInfoCreate boardInfoCreate = BoardInfoCreate.builder()
                .title(TEXT_50 + "0")
                .description("description")
                .build();

        Assertions.assertThrows(ConstraintViolationException.class, () -> {
            boardService.createBoardInfo(systemAdmin.getId(), boardInfoCreate);
        });
    }

    @Test
    @DisplayName("설명을 빈 문자열로 설정")
    public void emptyDescription() throws Exception {
        BoardInfoCreate boardInfoCreate = BoardInfoCreate.builder()
                .title("new board")
                .description("")
                .build();

        BoardInfo boardInfo = boardService.createBoardInfo(systemAdmin.getId(), boardInfoCreate);
        Assertions.assertEquals("", boardInfo.getDescription());
    }

    @Test
    @DisplayName("설명을 긴 문자열로 설정")
    public void overflowDescription() throws Exception {
        BoardInfoCreate boardInfoCreate = BoardInfoCreate.builder()
                .title(TEXT_100 + "0")
                .description("")
                .build();

        Assertions.assertThrows(ConstraintViolationException.class, () -> {
            boardService.createBoardInfo(systemAdmin.getId(), boardInfoCreate);
        });
    }

    @Test
    @DisplayName("비활성화")
    public void disabledBoard() throws Exception {
        BoardInfoCreate boardInfoCreate = BoardInfoCreate.builder()
                .title("new create")
                .description("")
                .enabled(false)
                .build();

        BoardInfo boardInfo = boardService.createBoardInfo(systemAdmin.getId(), boardInfoCreate);
        Assertions.assertFalse(boardInfo.getEnabled());
    }

    @Test
    @DisplayName("비공개")
    public void undisclosedBoard() throws Exception {
        BoardInfoCreate boardInfoCreate = BoardInfoCreate.builder()
                .title("new create")
                .description("")
                .undisclosed(true)
                .build();

        BoardInfo boardInfo = boardService.createBoardInfo(systemAdmin.getId(), boardInfoCreate);
        Assertions.assertTrue(boardInfo.getUnDisclosed());
    }

    @Test
    @DisplayName("공지 사항 전용")
    public void notificationOnlyBoard() throws Exception {
        BoardInfoCreate boardInfoCreate = BoardInfoCreate.builder()
                .title("new create")
                .description("")
                .notificationOnly(true)
                .build();

        BoardInfo boardInfo = boardService.createBoardInfo(systemAdmin.getId(), boardInfoCreate);
        Assertions.assertTrue(boardInfo.getNotificationOnly());
    }

    @Test
    @DisplayName("게시판 관리자 권한으로 게시판 생성")
    public void createBoardByBoardAdmin() throws Exception {
        // 계정 생성
        TestAccountInfo account = setRandomAccount();
        // 게시판 생성
        setRandomBoard(Arrays.asList(account));

        BoardInfoCreate boardInfoCreate = BoardInfoCreate.builder()
                .title("new create")
                .description("")
                .notificationOnly(true)
                .build();

        IricomException exception = Assertions.assertThrows(IricomException.class, () -> {
            boardService.createBoardInfo(account.getId(), boardInfoCreate);
        });
        Assertions.assertEquals("03000002", exception.getErrorCode());
    }
}
