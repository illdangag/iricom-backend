package com.illdangag.iricom.server.service.board;

import com.illdangag.iricom.server.service.BoardService;
import com.illdangag.iricom.server.test.IricomTestSuite;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

@DisplayName("service: 게시판 수정")
@Slf4j
public class BoardServiceUpdateTest extends IricomTestSuite {
    @Autowired
    private BoardService boardService;

    @Autowired
    public BoardServiceUpdateTest(ApplicationContext context) {
        super(context);

        init();
    }
}
