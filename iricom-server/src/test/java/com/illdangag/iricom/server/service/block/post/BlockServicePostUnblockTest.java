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

@DisplayName("service: 차단 - 차단 해제")
@Slf4j
@Transactional
public class BlockServicePostUnblockTest extends IricomTestSuite {
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
    private final TestPostInfo alreadyBlockPostInfo01 = TestPostInfo.builder()
            .title("alreadyBlockPostInfo01").content("alreadyBlockPostInfo01").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .creator(allBoardAdmin).board(boardInfo00)
            .build();
    private final TestPostInfo alreadyBlockPostInfo02 = TestPostInfo.builder()
            .title("alreadyBlockPostInfo02").content("alreadyBlockPostInfo02").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .creator(allBoardAdmin).board(boardInfo00)
            .build();
    private final TestPostInfo alreadyBlockPostInfo03 = TestPostInfo.builder()
            .title("alreadyBlockPostInfo03").content("alreadyBlockPostInfo03").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .creator(allBoardAdmin).board(boardInfo00)
            .build();

    private final TestPostBlockInfo postBlockInfo00 = TestPostBlockInfo.builder()
            .account(systemAdmin).post(alreadyBlockPostInfo00).reason("block")
            .build();
    private final TestPostBlockInfo postBlockInfo01 = TestPostBlockInfo.builder()
            .account(systemAdmin).post(alreadyBlockPostInfo01).reason("block")
            .build();
    private final TestPostBlockInfo postBlockInfo02 = TestPostBlockInfo.builder()
            .account(systemAdmin).post(alreadyBlockPostInfo02).reason("block")
            .build();
    private final TestPostBlockInfo postBlockInfo03 = TestPostBlockInfo.builder()
            .account(systemAdmin).post(alreadyBlockPostInfo03).reason("block")
            .build();

    public BlockServicePostUnblockTest(ApplicationContext context) {
        super(context);

        addTestBoardInfo(boardInfo00, boardInfo01);
        addTestPostInfo(alreadyBlockPostInfo00, alreadyBlockPostInfo01, alreadyBlockPostInfo02, alreadyBlockPostInfo03);
        addTestPostBlockInfo(postBlockInfo00, postBlockInfo01, postBlockInfo02, postBlockInfo03);

        init();
    }

    @Test
    @DisplayName("시스템 관리자")
    public void unblockSystemAdmin() throws Exception {
        String accountId = getAccountId(systemAdmin);
        String boardId = getBoardId(alreadyBlockPostInfo02.getBoard());
        String postId = getPostId(alreadyBlockPostInfo02);

        PostBlockInfo postBlockInfo = blockService.unblockPost(accountId, boardId, postId);
        Assertions.assertNotNull(postBlockInfo);
    }

    @Test
    @DisplayName("게시판 관리자")
    public void unblockBoardAdmin() throws Exception {
        String accountId = getAccountId(allBoardAdmin);
        String boardId = getBoardId(alreadyBlockPostInfo03.getBoard());
        String postId = getPostId(alreadyBlockPostInfo03);

        PostBlockInfo postBlockInfo = blockService.unblockPost(accountId, boardId, postId);
        Assertions.assertNotNull(postBlockInfo);
    }

    @Test
    @DisplayName("다른 게시판 관리자")
    public void unblockOtherBoardAdmin() throws Exception {
        String accountId = getAccountId(enableBoardAdmin);
        String boardId = getBoardId(alreadyBlockPostInfo01.getBoard());
        String postId = getPostId(alreadyBlockPostInfo01);

        IricomException iricomException = Assertions.assertThrows(IricomException.class, () -> {
            blockService.unblockPost(accountId, boardId, postId);
        });

        Assertions.assertEquals("04000009", iricomException.getErrorCode());
        Assertions.assertEquals("Invalid authorization.", iricomException.getMessage());
    }

    @Test
    @DisplayName("일반 사용자")
    public void unblockAccount() throws Exception {
        String accountId = getAccountId(common00);
        String boardId = getBoardId(alreadyBlockPostInfo01.getBoard());
        String postId = getPostId(alreadyBlockPostInfo01);

        IricomException iricomException = Assertions.assertThrows(IricomException.class, () -> {
            blockService.unblockPost(accountId, boardId, postId);
        });

        Assertions.assertEquals("04000009", iricomException.getErrorCode());
        Assertions.assertEquals("Invalid authorization.", iricomException.getMessage());
    }

    @Test
    @DisplayName("등록되지 않은 사용자")
    public void unblockUnknown() throws Exception {
        String accountId = getAccountId(unknown00);
        String boardId = getBoardId(alreadyBlockPostInfo01.getBoard());
        String postId = getPostId(alreadyBlockPostInfo01);

        IricomException iricomException = Assertions.assertThrows(IricomException.class, () -> {
            blockService.unblockPost(accountId, boardId, postId);
        });

        Assertions.assertEquals("04000009", iricomException.getErrorCode());
        Assertions.assertEquals("Invalid authorization.", iricomException.getMessage());
    }

    @Test
    @DisplayName("게시물이 포함된 게시판이 아닌 다른 게시판")
    public void unblockPostInOtherBoard() throws Exception {
        String accountId = getAccountId(systemAdmin);
        String boardId = getBoardId(boardInfo01);
        String postId = getPostId(alreadyBlockPostInfo01);

        IricomException iricomException = Assertions.assertThrows(IricomException.class, () -> {
            blockService.unblockPost(accountId, boardId, postId);
        });

        Assertions.assertEquals("04000000", iricomException.getErrorCode());
        Assertions.assertEquals("Not exist post.", iricomException.getMessage());
    }
}
