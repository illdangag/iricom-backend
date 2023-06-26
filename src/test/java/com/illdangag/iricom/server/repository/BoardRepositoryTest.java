package com.illdangag.iricom.server.repository;

import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.data.entity.Board;
import com.illdangag.iricom.server.test.IricomTestSuite;
import com.illdangag.iricom.server.test.data.TestBoardInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
@DisplayName("BoardRepository")
public class BoardRepositoryTest extends IricomTestSuite {
    @Autowired
    private BoardRepository boardRepository;

    private TestBoardInfo testBoardInfo00 = TestBoardInfo.builder()
            .title("testBoardInfo00").adminList(Collections.singletonList(allBoardAdmin))
            .isEnabled(true).undisclosed(true)
            .build();

    @Autowired
    public BoardRepositoryTest(ApplicationContext context) {
        super(context);

        super.setBoard(Arrays.asList(testBoardInfo00));
    }

    @Test
    @DisplayName("비공개 게시판 목록 조회")
    public void getUndisclosedBoardList() throws Exception {
        Account account00 = getAccount(common00);
        Account account01 = getAccount(common01);

        Board board = getBoard(testBoardInfo00);

        List<Board> account00boardList = boardRepository.getBoardList(account00, "", true, 0, Integer.MAX_VALUE);
        List<Board> account01BoardList = boardRepository.getBoardList(account01, "", true, 0, Integer.MAX_VALUE);

        Assertions.assertTrue(account00boardList.contains(board));
        Assertions.assertFalse(account01BoardList.contains(board));
    }
}
