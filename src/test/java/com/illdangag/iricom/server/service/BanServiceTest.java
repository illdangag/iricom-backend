package com.illdangag.iricom.server.service;

import com.illdangag.iricom.server.data.entity.*;
import com.illdangag.iricom.server.data.request.PostBanInfoCreate;
import com.illdangag.iricom.server.data.response.PostBanInfo;
import com.illdangag.iricom.server.exception.IricomException;
import com.illdangag.iricom.server.test.IricomTestSuite;
import com.illdangag.iricom.server.test.data.TestBoardInfo;
import com.illdangag.iricom.server.test.data.TestPostBanInfo;
import com.illdangag.iricom.server.test.data.TestPostInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
public class BanServiceTest extends IricomTestSuite {
    @Autowired
    private BanService banService;

    protected static final TestBoardInfo boardInfo00 = TestBoardInfo.builder()
            .title("boardInfo00").isEnabled(true).adminList(Collections.singletonList(allBoardAdmin)).build();

    protected static final TestPostInfo toBanPostInfo00 = TestPostInfo.builder()
            .title("toBanPostInfo00").content("toBanPostInfo00").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .creator(allBoardAdmin).board(boardInfo00)
            .build();
    protected static final TestPostInfo toBanPostInfo01 = TestPostInfo.builder()
            .title("toBanPostInfo01").content("toBanPostInfo01").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .creator(allBoardAdmin).board(boardInfo00)
            .build();
    protected static final TestPostInfo toBanPostInfo02 = TestPostInfo.builder()
            .title("toBanPostInfo02").content("toBanPostInfo02").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .creator(allBoardAdmin).board(boardInfo00)
            .build();
    protected static final TestPostInfo toBanPostInfo03 = TestPostInfo.builder()
            .title("toBanPostInfo03").content("toBanPostInfo03").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .creator(allBoardAdmin).board(boardInfo00)
            .build();

    protected static final TestPostInfo alreadyBanPostInfo00 = TestPostInfo.builder()
            .title("alreadyBanPostInfo00").content("alreadyBanPostInfo00").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .creator(allBoardAdmin).board(boardInfo00)
            .build();

    protected static final TestPostBanInfo postBanInfo00 = TestPostBanInfo.builder()
            .banAccount(systemAdmin).post(alreadyBanPostInfo00)
            .build();

    @Autowired
    public BanServiceTest(ApplicationContext context) {
        super(context);

        List<TestBoardInfo> testBoardInfoList = Arrays.asList(boardInfo00);
        List<TestPostInfo> testPostInfoList = Arrays.asList(toBanPostInfo00, toBanPostInfo01, toBanPostInfo02, toBanPostInfo03,
                alreadyBanPostInfo00);
        List<TestPostBanInfo> testPostBanInfoList = Arrays.asList(postBanInfo00);

        super.setBoard(testBoardInfoList);
        super.setPost(testPostInfoList);
        super.setBanPost(testPostBanInfoList);
    }

    @Nested
    @DisplayName("차단")
    class Ban {

        @Nested
        @DisplayName("권한별")
        class Auth {

            @Test
            @DisplayName("시스템 관리자")
            public void banSystemAdmin() throws Exception {
                Account account = getAccount(systemAdmin);
                Post post = getPost(toBanPostInfo00);
                Board board = post.getBoard();

                PostBanInfoCreate postBanInfoCreate = PostBanInfoCreate.builder()
                        .reason("BAN")
                        .build();
                PostBanInfo postBanInfo = banService.banPost(account, board, post, postBanInfoCreate);

                Assertions.assertNotNull(postBanInfo);
                Assertions.assertEquals("BAN", postBanInfo.getReason());
                Assertions.assertEquals(String.valueOf(post.getId()), postBanInfo.getPostInfo().getId());
                Assertions.assertTrue(postBanInfo.getPostInfo().getIsBan());
            }

            @Test
            @DisplayName("게시판 관리자")
            public void banBoardAdmin() throws Exception {
                Account account = getAccount(allBoardAdmin);
                Post post = getPost(toBanPostInfo01);
                Board board = post.getBoard();

                PostBanInfoCreate postBanInfoCreate = PostBanInfoCreate.builder()
                        .reason("BAN")
                        .build();
                PostBanInfo postBanInfo = banService.banPost(account, board, post, postBanInfoCreate);

                Assertions.assertNotNull(postBanInfo);
                Assertions.assertEquals("BAN", postBanInfo.getReason());
                Assertions.assertEquals(String.valueOf(post.getId()), postBanInfo.getPostInfo().getId());
                Assertions.assertTrue(postBanInfo.getPostInfo().getIsBan());
            }

            @Test
            @DisplayName("일반 사용자")
            public void banAccount() throws Exception {
                Account account = getAccount(common00);
                Post post = getPost(toBanPostInfo02);
                Board board = post.getBoard();

                PostBanInfoCreate postBanInfoCreate = PostBanInfoCreate.builder()
                        .reason("BAN")
                        .build();

                Assertions.assertThrows(IricomException.class, () -> {
                    banService.banPost(account, board, post, postBanInfoCreate);
                });
            }

            @Test
            @DisplayName("등록되지 않은 사용자")
            public void banUnknown() throws Exception {
                Account account = getAccount(unknown00);
                Post post = getPost(toBanPostInfo02);
                Board board = post.getBoard();

                PostBanInfoCreate postBanInfoCreate = PostBanInfoCreate.builder()
                        .reason("BAN")
                        .build();

                Assertions.assertThrows(IricomException.class, () -> {
                    banService.banPost(account, board, post, postBanInfoCreate);
                });
            }
        }

        @Test
        @DisplayName("이미 차단된 게시물")
        public void alreadyBanPost() throws Exception {
            Account account = getAccount(systemAdmin);
            Post post = getPost(alreadyBanPostInfo00);
            Board board = post.getBoard();

            PostBanInfoCreate postBanInfoCreate = PostBanInfoCreate.builder()
                    .reason("BAN")
                    .build();

            Assertions.assertThrows(IricomException.class, () -> {
                banService.banPost(account, board, post, postBanInfoCreate);
            });
        }
    }
}
