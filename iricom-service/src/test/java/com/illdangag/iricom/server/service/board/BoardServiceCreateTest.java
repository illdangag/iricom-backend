package com.illdangag.iricom.server.service.board;

import com.illdangag.iricom.server.data.request.BoardInfoCreate;
import com.illdangag.iricom.server.data.response.BoardInfo;
import com.illdangag.iricom.server.service.BoardService;
import com.illdangag.iricom.server.test.IricomTestSuite;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javax.validation.ConstraintViolationException;

@DisplayName("service: 게시판 생성")
@Slf4j
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
        BoardInfoCreate boardInfoCreate = BoardInfoCreate.builder()
                .title("new board")
                .description("description")
                .build();

        BoardInfo boardInfo = boardService.createBoardInfo(boardInfoCreate);

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

        ConstraintViolationException exception = Assertions.assertThrows(ConstraintViolationException.class, () -> {
            boardService.createBoardInfo(boardInfoCreate);
        });

        Assertions.assertNotNull(exception);
    }

    @Test
    @DisplayName("제목을 긴 문자열로 설정")
    public void overflowTitle() throws Exception {
        BoardInfoCreate boardInfoCreate = BoardInfoCreate.builder()
                .title("012345678901234567890123456789012345678901234567890")
                .description("description")
                .build();

        ConstraintViolationException exception = Assertions.assertThrows(ConstraintViolationException.class, () -> {
            boardService.createBoardInfo(boardInfoCreate);
        });

        Assertions.assertNotNull(exception);
    }

    @Test
    @DisplayName("설명을 빈 문자열로 설정")
    public void emptyDescription() throws Exception {
        BoardInfoCreate boardInfoCreate = BoardInfoCreate.builder()
                .title("new board")
                .description("")
                .build();

        BoardInfo boardInfo = boardService.createBoardInfo(boardInfoCreate);

        Assertions.assertEquals("", boardInfo.getDescription());
    }

    @Test
    @DisplayName("설명을 긴 문자열로 설정")
    public void overflowDescription() throws Exception {
        BoardInfoCreate boardInfoCreate = BoardInfoCreate.builder()
                .title("01234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890")
                .description("")
                .build();

        ConstraintViolationException exception = Assertions.assertThrows(ConstraintViolationException.class, () -> {
            boardService.createBoardInfo(boardInfoCreate);
        });

        Assertions.assertNotNull(exception);
    }

    @Test
    @DisplayName("비활성화")
    public void disabled() throws Exception {
        BoardInfoCreate boardInfoCreate = BoardInfoCreate.builder()
                .title("new create")
                .description("")
                .enabled(false)
                .build();

        BoardInfo boardInfo = boardService.createBoardInfo(boardInfoCreate);

        Assertions.assertFalse(boardInfo.getEnabled());
    }

    @Test
    @DisplayName("비공개")
    public void undisclosed() throws Exception {
        BoardInfoCreate boardInfoCreate = BoardInfoCreate.builder()
                .title("new create")
                .description("")
                .undisclosed(true)
                .build();

        BoardInfo boardInfo = boardService.createBoardInfo(boardInfoCreate);

        Assertions.assertTrue(boardInfo.getUnDisclosed());
    }

    @Test
    @DisplayName("공지 사항 전용")
    public void notificationOnly() throws Exception {
        BoardInfoCreate boardInfoCreate = BoardInfoCreate.builder()
                .title("new create")
                .description("")
                .notificationOnly(true)
                .build();

        BoardInfo boardInfo = boardService.createBoardInfo(boardInfoCreate);

        Assertions.assertTrue(boardInfo.getNotificationOnly());
    }
}
