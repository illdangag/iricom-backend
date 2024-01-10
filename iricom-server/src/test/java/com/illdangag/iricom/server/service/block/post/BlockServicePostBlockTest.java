package com.illdangag.iricom.server.service.block.post;

import com.illdangag.iricom.server.data.entity.type.PostState;
import com.illdangag.iricom.server.data.entity.type.PostType;
import com.illdangag.iricom.server.data.request.PostBlockInfoCreate;
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

@DisplayName("service: 차단 - 게시물 차단")
@Slf4j
@Transactional
public class BlockServicePostBlockTest extends IricomTestSuite {
    @Autowired
    private BlockService blockService;

    private final TestBoardInfo boardInfo00 = TestBoardInfo.builder()
            .title("boardInfo00").isEnabled(true).adminList(Collections.singletonList(allBoardAdmin)).build();
    private final TestBoardInfo boardInfo01 = TestBoardInfo.builder()
            .title("boardInfo01").isEnabled(true).adminList(Collections.singletonList(allBoardAdmin)).build();

    private final TestPostInfo toBlockPostInfo00 = TestPostInfo.builder()
            .title("toBlockPostInfo00").content("toBlockPostInfo00").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .creator(allBoardAdmin).board(boardInfo00)
            .build();
    private final TestPostInfo toBlockPostInfo01 = TestPostInfo.builder()
            .title("toBlockPostInfo01").content("toBlockPostInfo01").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .creator(allBoardAdmin).board(boardInfo00)
            .build();
    private final TestPostInfo toBlockPostInfo02 = TestPostInfo.builder()
            .title("toBlockPostInfo02").content("toBlockPostInfo02").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .creator(allBoardAdmin).board(boardInfo00)
            .build();
    private final TestPostInfo toBlockPostInfo04 = TestPostInfo.builder()
            .title("toBlockPostInfo04").content("toBlockPostInfo04").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .creator(allBoardAdmin).board(boardInfo01)
            .build();
    private final TestPostInfo alreadyBlockPostInfo00 = TestPostInfo.builder()
            .title("alreadyBlockPostInfo00").content("alreadyBlockPostInfo00").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .creator(allBoardAdmin).board(boardInfo01)
            .build();
    private final TestPostInfo alreadyBlockPostInfo01 = TestPostInfo.builder()
            .title("alreadyBlockPostInfo01").content("alreadyBlockPostInfo01").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .creator(allBoardAdmin).board(boardInfo01)
            .build();

    private final TestPostBlockInfo postBlockInfo00 = TestPostBlockInfo.builder()
            .account(systemAdmin).post(alreadyBlockPostInfo00).reason("Block")
            .build();
    private final TestPostBlockInfo postBlockInfo01 = TestPostBlockInfo.builder()
            .account(systemAdmin).post(alreadyBlockPostInfo01).reason("Block")
            .build();

    public BlockServicePostBlockTest(ApplicationContext context) {
        super(context);

        addTestBoardInfo(boardInfo00, boardInfo01);
        addTestPostInfo(toBlockPostInfo00, toBlockPostInfo01, toBlockPostInfo02, toBlockPostInfo04, alreadyBlockPostInfo00, alreadyBlockPostInfo01);
        addTestPostBlockInfo(postBlockInfo00, postBlockInfo01);
        init();
    }

    @Test
    @DisplayName("시스템 관리자")
    public void blockSystemAdmin() throws Exception {
        String accountId = getAccountId(systemAdmin);
        String boardId = getBoardId(toBlockPostInfo00.getBoard());
        String postId = getPostId(toBlockPostInfo00);

        PostBlockInfoCreate postBlockInfoCreate = PostBlockInfoCreate.builder()
                .reason("block")
                .build();
        PostBlockInfo postBlockInfo = blockService.blockPost(accountId, boardId, postId, postBlockInfoCreate);

        Assertions.assertNotNull(postBlockInfo);
        Assertions.assertEquals("block", postBlockInfo.getReason());
        Assertions.assertEquals(String.valueOf(postId), postBlockInfo.getPostInfo().getId());
        Assertions.assertTrue(postBlockInfo.getPostInfo().getBlocked());
        Assertions.assertTrue(postBlockInfo.getEnabled());
    }

    @Test
    @DisplayName("게시판 관리자")
    public void blockBoardAdmin() throws Exception {
        String accountId = getAccountId(allBoardAdmin);
        String boardId = getBoardId(toBlockPostInfo01.getBoard());
        String postId = getPostId(toBlockPostInfo01);

        PostBlockInfoCreate postBlockInfoCreate = PostBlockInfoCreate.builder()
                .reason("block")
                .build();
        PostBlockInfo postBlockInfo = blockService.blockPost(accountId, boardId, postId, postBlockInfoCreate);

        Assertions.assertNotNull(postBlockInfo);
        Assertions.assertEquals("block", postBlockInfo.getReason());
        Assertions.assertEquals(String.valueOf(postId), postBlockInfo.getPostInfo().getId());
        Assertions.assertTrue(postBlockInfo.getPostInfo().getBlocked());
        Assertions.assertTrue(postBlockInfo.getEnabled());
    }

    @Test
    @DisplayName("다른 게시판 관리자")
    public void blockOtherBoardAdmin() throws Exception {
        String accountId = getAccountId(enableBoardAdmin);
        String boardId = getBoardId(toBlockPostInfo01.getBoard());
        String postId = getPostId(toBlockPostInfo01);

        PostBlockInfoCreate postBlockInfoCreate = PostBlockInfoCreate.builder()
                .reason("block")
                .build();

        Assertions.assertThrows(IricomException.class, () -> {
            blockService.blockPost(accountId, boardId, postId, postBlockInfoCreate);
        });
    }

    @Test
    @DisplayName("일반 사용자")
    public void blockAccount() throws Exception {
        String accountId = getAccountId(common00);
        String boardId = getBoardId(toBlockPostInfo02.getBoard());
        String postId = getPostId(toBlockPostInfo02);

        PostBlockInfoCreate postBlockInfoCreate = PostBlockInfoCreate.builder()
                .reason("block")
                .build();

        Assertions.assertThrows(IricomException.class, () -> {
            blockService.blockPost(accountId, boardId, postId, postBlockInfoCreate);
        });
    }

    @Test
    @DisplayName("등록되지 않은 사용자")
    public void blockUnknown() throws Exception {
        String accountId = getAccountId(unknown00);
        String boardId = getBoardId(toBlockPostInfo02.getBoard());
        String postId = getPostId(toBlockPostInfo02);

        PostBlockInfoCreate postBlockInfoCreate = PostBlockInfoCreate.builder()
                .reason("block")
                .build();

        IricomException exception = Assertions.assertThrows(IricomException.class, () -> {
            blockService.blockPost(accountId, boardId, postId, postBlockInfoCreate);
        });

        Assertions.assertEquals("04000009", exception.getErrorCode());
    }

    @Test
    @DisplayName("이미 차단된 게시물")
    public void alreadyBlockPost() throws Exception {
        String accountId = getAccountId(systemAdmin);
        String boardId = getBoardId(alreadyBlockPostInfo00.getBoard());
        String postId = getPostId(alreadyBlockPostInfo00);

        PostBlockInfoCreate postBlockInfoCreate = PostBlockInfoCreate.builder()
                .reason("block")
                .build();

        Assertions.assertThrows(IricomException.class, () -> {
            blockService.blockPost(accountId, boardId, postId, postBlockInfoCreate);
        });
    }

    @Test
    @DisplayName("이미 차단한 게시물")
    public void blockAlreadyBlockPost() {
        String accountId = getAccountId(systemAdmin);
        String boardId = getBoardId(alreadyBlockPostInfo01.getBoard());
        String postId = getPostId(alreadyBlockPostInfo01);

        PostBlockInfoCreate postBlockInfoCreate = PostBlockInfoCreate.builder()
                .reason("Already block")
                .build();
        Assertions.assertThrows(IricomException.class, () -> {
            blockService.blockPost(accountId, boardId, postId, postBlockInfoCreate);
        });
    }

    @Test
    @DisplayName("다른 게시판에 존재하는 게시물")
    public void blockPostInOtherBoard() throws Exception {
        String accountId = getAccountId(systemAdmin);
        String boardId = getBoardId(boardInfo00);
        String postId = getPostId(toBlockPostInfo04);

        PostBlockInfoCreate postBlockInfoCreate = PostBlockInfoCreate.builder()
                .reason("not exist post")
                .build();
        Assertions.assertThrows(IricomException.class, () -> {
            blockService.blockPost(accountId, boardId, postId, postBlockInfoCreate);
        });
    }
}
