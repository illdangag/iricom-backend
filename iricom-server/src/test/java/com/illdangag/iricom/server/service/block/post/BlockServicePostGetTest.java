package com.illdangag.iricom.server.service.block.post;

import com.illdangag.iricom.server.data.entity.type.PostState;
import com.illdangag.iricom.server.data.entity.type.PostType;
import com.illdangag.iricom.server.data.response.PostBlockInfo;
import com.illdangag.iricom.server.exception.IricomException;
import com.illdangag.iricom.server.service.BlockService;
import com.illdangag.iricom.server.test.IricomTestSuite;
import com.illdangag.iricom.server.test.data.wrapper.TestBoardInfo;
import com.illdangag.iricom.server.test.data.wrapper.TestPostBlockInfo;
import com.illdangag.iricom.server.test.data.wrapper.TestPostInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javax.transaction.Transactional;
import java.util.Collections;

@DisplayName("service: 차단 - 조회")
@Slf4j
@Transactional
public class BlockServicePostGetTest extends IricomTestSuite {
    @Autowired
    private BlockService blockService;

    private final TestBoardInfo boardInfo00 = TestBoardInfo.builder()
            .title("boardInfo00").isEnabled(true).adminList(Collections.singletonList(allBoardAdmin)).build();
    private final TestBoardInfo boardInfo01 = TestBoardInfo.builder()
            .title("boardInfo01").isEnabled(true).adminList(Collections.singletonList(allBoardAdmin)).build();

    private final TestPostInfo alreadyBlockPostInfo00 = TestPostInfo.builder()
            .title("alreadyBlockPostInfo00").content("alreadyBlockPostInfo00").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .creator(allBoardAdmin).board(boardInfo00)
            .build();

    private final TestPostBlockInfo postBlockInfo00 = TestPostBlockInfo.builder()
            .account(systemAdmin).post(alreadyBlockPostInfo00).reason("block")
            .build();

    public BlockServicePostGetTest(ApplicationContext context) {
        super(context);

        addTestBoardInfo(boardInfo00, boardInfo01);
        addTestPostInfo(alreadyBlockPostInfo00);
        addTestPostBlockInfo(postBlockInfo00);

        init();
    }

    @Test
    @DisplayName("시스템 관리자")
    public void getPostBlockInfoSystemAdmin() throws Exception {
        String accountId = getAccountId(systemAdmin);
        String boardId = getBoardId(alreadyBlockPostInfo00.getBoard());
        String postId = getPostId(alreadyBlockPostInfo00);

        PostBlockInfo postBlockInfo = blockService.getPostBlockInfo(accountId, boardId, postId);
        Assertions.assertNotNull(postBlockInfo);
    }

    @Test
    @DisplayName("게시판 관리자")
    public void getPostBlockInfoBoardAdmin() throws Exception {
        String accountId = getAccountId(allBoardAdmin);
        String boardId = getBoardId(alreadyBlockPostInfo00.getBoard());
        String postId = getPostId(alreadyBlockPostInfo00);

        PostBlockInfo postBlockInfo = blockService.getPostBlockInfo(accountId, boardId, postId);
        Assertions.assertNotNull(postBlockInfo);
    }

    @Test
    @DisplayName("다른 게시판 관리자")
    public void getPostBlockInfoOtherBoardAdmin() throws Exception {
        String accountId = getAccountId(enableBoardAdmin);
        String boardId = getBoardId(alreadyBlockPostInfo00.getBoard());
        String postId = getPostId(alreadyBlockPostInfo00);

        Assertions.assertThrows(IricomException.class, () -> {
            blockService.getPostBlockInfo(accountId, boardId, postId);
        });
    }

    @Test
    @DisplayName("일반 사용자")
    public void getPostBlockInfoAccount() throws Exception {
        String accountId = getAccountId(common00);
        String boardId = getBoardId(alreadyBlockPostInfo00.getBoard());
        String postId = getPostId(alreadyBlockPostInfo00);

        Assertions.assertThrows(IricomException.class, () -> {
            blockService.getPostBlockInfo(accountId, boardId, postId);
        });
    }

    @Test
    @DisplayName("등록되지 않은 사용자")
    public void getPostBlockInfoUnknown() throws Exception {
        String accountId = getAccountId(unknown00);
        String boardId = getBoardId(alreadyBlockPostInfo00.getBoard());
        String postId = getPostId(alreadyBlockPostInfo00);

        Assertions.assertThrows(IricomException.class, () -> {
            blockService.getPostBlockInfo(accountId, boardId, postId);
        });
    }

    @Test
    @DisplayName("시스템 관리자")
    public void getPostInOtherBoard() throws Exception {
        String accountId = getAccountId(systemAdmin);
        String boardId = getBoardId(boardInfo01);
        String postId = getPostId(alreadyBlockPostInfo00);

        Assertions.assertThrows(IricomException.class, () -> {
            blockService.getPostBlockInfo(accountId, boardId, postId);
        });
    }
}
