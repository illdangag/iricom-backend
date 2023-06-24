package com.illdangag.iricom.server.service;

import com.illdangag.iricom.server.data.entity.*;
import com.illdangag.iricom.server.data.request.PostBanInfoCreate;
import com.illdangag.iricom.server.data.request.PostBanInfoSearch;
import com.illdangag.iricom.server.data.request.PostBanInfoUpdate;
import com.illdangag.iricom.server.data.response.PostBanInfo;
import com.illdangag.iricom.server.data.response.PostBanInfoList;
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
    protected static final TestBoardInfo boardInfo01 = TestBoardInfo.builder()
            .title("boardInfo01").isEnabled(true).adminList(Collections.singletonList(allBoardAdmin)).build();
    protected static final TestBoardInfo boardInfo02 = TestBoardInfo.builder()
            .title("boardInfo02").isEnabled(true).adminList(Collections.singletonList(allBoardAdmin)).build();

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
    protected static final TestPostInfo toBanPostInfo04 = TestPostInfo.builder()
            .title("toBanPostInfo04").content("toBanPostInfo04").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .creator(allBoardAdmin).board(boardInfo01)
            .build();
    protected static final TestPostInfo alreadyBanPostInfo00 = TestPostInfo.builder()
            .title("alreadyBanPostInfo00").content("alreadyBanPostInfo00").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .creator(allBoardAdmin).board(boardInfo01)
            .build();
    protected static final TestPostInfo alreadyBanPostInfo01 = TestPostInfo.builder()
            .title("alreadyBanPostInfo01").content("alreadyBanPostInfo01").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .creator(allBoardAdmin).board(boardInfo01)
            .build();
    protected static final TestPostInfo alreadyBanPostInfo02 = TestPostInfo.builder()
            .title("alreadyBanPostInfo02").content("alreadyBanPostInfo02").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .creator(allBoardAdmin).board(boardInfo02)
            .build();
    protected static final TestPostInfo alreadyBanPostInfo03 = TestPostInfo.builder()
            .title("alreadyBanPostInfo03").content("alreadyBanPostInfo03").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .creator(allBoardAdmin).board(boardInfo02)
            .build();
    protected static final TestPostInfo alreadyBanPostInfo04 = TestPostInfo.builder()
            .title("alreadyBanPostInfo04").content("alreadyBanPostInfo04").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .creator(allBoardAdmin).board(boardInfo02)
            .build();

    protected static final TestPostBanInfo postBanInfo00 = TestPostBanInfo.builder()
            .banAccount(systemAdmin).post(alreadyBanPostInfo00).reason("BAN")
            .build();
    protected static final TestPostBanInfo postBanInfo01 = TestPostBanInfo.builder()
            .banAccount(systemAdmin).post(alreadyBanPostInfo01).reason("BAN")
            .build();
    protected static final TestPostBanInfo postBanInfo02 = TestPostBanInfo.builder()
            .banAccount(systemAdmin).post(alreadyBanPostInfo02).reason("BAN")
            .build();
    protected static final TestPostBanInfo postBanInfo03 = TestPostBanInfo.builder()
            .banAccount(systemAdmin).post(alreadyBanPostInfo03).reason("BAN")
            .build();
    protected static final TestPostBanInfo postBanInfo04 = TestPostBanInfo.builder()
            .banAccount(systemAdmin).post(alreadyBanPostInfo04).reason("BAN")
            .build();


    @Autowired
    public BanServiceTest(ApplicationContext context) {
        super(context);

        List<TestBoardInfo> testBoardInfoList = Arrays.asList(boardInfo00, boardInfo01, boardInfo02);
        List<TestPostInfo> testPostInfoList = Arrays.asList(toBanPostInfo00, toBanPostInfo01, toBanPostInfo02, toBanPostInfo03,
                toBanPostInfo04,
                alreadyBanPostInfo00, alreadyBanPostInfo01, alreadyBanPostInfo02, alreadyBanPostInfo03, alreadyBanPostInfo04);
        List<TestPostBanInfo> testPostBanInfoList = Arrays.asList(postBanInfo00, postBanInfo01, postBanInfo02, postBanInfo03,
                postBanInfo04);

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
                Assertions.assertTrue(postBanInfo.getEnabled());
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
                Assertions.assertTrue(postBanInfo.getEnabled());
            }

            @Test
            @DisplayName("다른 게시판 관리자")
            public void banOtherBoardAdmin() throws Exception {
                Account account = getAccount(enableBoardAdmin);
                Post post = getPost(toBanPostInfo01);
                Board board = post.getBoard();

                PostBanInfoCreate postBanInfoCreate = PostBanInfoCreate.builder()
                        .reason("BAN")
                        .build();

                Assertions.assertThrows(IricomException.class, () -> {
                    banService.banPost(account, board, post, postBanInfoCreate);
                });
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

        @Nested
        @DisplayName("차단된 게시물")
        class AlreadyBan {

            @Test
            @DisplayName("차단")
            public void banAlreadyBanPost() {
                Account account = getAccount(systemAdmin);
                Post post = getPost(alreadyBanPostInfo01);
                Board board = post.getBoard();

                PostBanInfoCreate postBanInfoCreate = PostBanInfoCreate.builder()
                        .reason("Already ban")
                        .build();
                Assertions.assertThrows(IricomException.class, () -> {
                    banService.banPost(account, board, post, postBanInfoCreate);
                });
            }
        }
    }

    @Nested
    @DisplayName("목록 조회")
    class Search {

        @Nested
        @DisplayName("권한별")
        class Auth {

            @Test
            @DisplayName("시스템 관리자")
            public void searchSystemAdmin() throws Exception {
                Account account = getAccount(systemAdmin);
                Board board = getBoard(boardInfo01);

                PostBanInfoSearch postBanInfoSearch = PostBanInfoSearch.builder()
                        .reason("")
                        .skip(0)
                        .limit(10)
                        .build();

                PostBanInfoList postBanInfoList = banService.getPostBanInfoList(account, board, postBanInfoSearch);

                Assertions.assertEquals(0, postBanInfoList.getSkip());
                Assertions.assertEquals(10, postBanInfoList.getLimit());
                Assertions.assertEquals(2, postBanInfoList.getPostBanInfoList().size());
            }

            @Test
            @DisplayName("게시판 관리자")
            public void searchBoardAdmin() throws Exception {
                Account account = getAccount(allBoardAdmin);
                Board board = getBoard(boardInfo01);

                PostBanInfoSearch postBanInfoSearch = PostBanInfoSearch.builder()
                        .reason("")
                        .skip(0)
                        .limit(10)
                        .build();

                PostBanInfoList postBanInfoList = banService.getPostBanInfoList(account, board, postBanInfoSearch);

                Assertions.assertEquals(0, postBanInfoList.getSkip());
                Assertions.assertEquals(10, postBanInfoList.getLimit());
                Assertions.assertEquals(2, postBanInfoList.getPostBanInfoList().size());
            }

            @Test
            @DisplayName("다른 게시판 관리자")
            public void searchOtherBoardAdmin() throws Exception {
                Account account = getAccount(enableBoardAdmin);
                Board board = getBoard(boardInfo01);

                PostBanInfoSearch postBanInfoSearch = PostBanInfoSearch.builder()
                        .reason("")
                        .skip(0)
                        .limit(10)
                        .build();

                Assertions.assertThrows(IricomException.class, () -> {
                    banService.getPostBanInfoList(account, board, postBanInfoSearch);
                });
            }

            @Test
            @DisplayName("일반 사용자")
            public void searchAccount() throws Exception {
                Account account = getAccount(common00);
                Board board = getBoard(boardInfo01);

                PostBanInfoSearch postBanInfoSearch = PostBanInfoSearch.builder()
                        .reason("")
                        .skip(0)
                        .limit(10)
                        .build();

                Assertions.assertThrows(IricomException.class, () -> {
                    banService.getPostBanInfoList(account, board, postBanInfoSearch);
                });
            }

            @Test
            @DisplayName("등록되지 않은 사용자")
            public void searchUnknown() throws Exception {
                Account account = getAccount(unknown00);
                Board board = getBoard(boardInfo01);

                PostBanInfoSearch postBanInfoSearch = PostBanInfoSearch.builder()
                        .reason("")
                        .skip(0)
                        .limit(10)
                        .build();

                Assertions.assertThrows(IricomException.class, () -> {
                    banService.getPostBanInfoList(account, board, postBanInfoSearch);
                });
            }
        }
    }

    @Nested
    @DisplayName("조회")
    class Get {

        @Nested
        @DisplayName("권한별")
        class Auth {

            @Test
            @DisplayName("시스템 관리자")
            public void getPostBanInfoSystemAdmin() throws Exception {
                Account account = getAccount(systemAdmin);
                Post post = getPost(alreadyBanPostInfo00);
                Board board = post.getBoard();

                PostBanInfo postBanInfo = banService.getPostBanInfo(account, board, post);
                Assertions.assertTrue(postBanInfo.getEnabled());
            }

            @Test
            @DisplayName("게시판 관리자")
            public void getPostBanInfoBoardAdmin() throws Exception {
                Account account = getAccount(allBoardAdmin);
                Post post = getPost(alreadyBanPostInfo00);
                Board board = post.getBoard();

                PostBanInfo postBanInfo = banService.getPostBanInfo(account, board, post);
                Assertions.assertTrue(postBanInfo.getEnabled());
            }

            @Test
            @DisplayName("다른 게시판 관리자")
            public void getPostBanInfoOtherBoardAdmin() throws Exception {
                Account account = getAccount(enableBoardAdmin);
                Post post = getPost(alreadyBanPostInfo00);
                Board board = post.getBoard();

                Assertions.assertThrows(IricomException.class, () -> {
                    banService.getPostBanInfo(account, board, post);
                });
            }

            @Test
            @DisplayName("일반 사용자")
            public void getPostBanInfoAccount() throws Exception {
                Account account = getAccount(common00);
                Post post = getPost(alreadyBanPostInfo00);
                Board board = post.getBoard();

                Assertions.assertThrows(IricomException.class, () -> {
                    banService.getPostBanInfo(account, board, post);
                });
            }

            @Test
            @DisplayName("등록되지 않은 사용자")
            public void getPostBanInfoUnknown() throws Exception {
                Account account = getAccount(unknown00);
                Post post = getPost(alreadyBanPostInfo00);
                Board board = post.getBoard();

                Assertions.assertThrows(IricomException.class, () -> {
                    banService.getPostBanInfo(account, board, post);
                });
            }
        }
    }

    @Nested
    @DisplayName("수정")
    class Update {

        @Nested
        @DisplayName("권한별")
        class Auth {

            @Test
            @DisplayName("시스템 관리자")
            public void updateSystemAdmin() throws Exception {
                Account account = getAccount(systemAdmin);
                Post post = getPost(alreadyBanPostInfo01);
                Board board = post.getBoard();

                PostBanInfoUpdate postBanInfoUpdate = PostBanInfoUpdate.builder()
                        .reason("UPDATE_SYSTEM_ADMIN")
                        .build();

                PostBanInfo postBanInfo = banService.updatePostBanInfo(account, board, post, postBanInfoUpdate);

                Assertions.assertEquals("UPDATE_SYSTEM_ADMIN", postBanInfo.getReason());
            }

            @Test
            @DisplayName("게시판 관리자")
            public void updateBoardAdmin() throws Exception {
                Account account = getAccount(allBoardAdmin);
                Post post = getPost(alreadyBanPostInfo01);
                Board board = post.getBoard();

                PostBanInfoUpdate postBanInfoUpdate = PostBanInfoUpdate.builder()
                        .reason("UPDATE_BOARD_ADMIN")
                        .build();

                PostBanInfo postBanInfo = banService.updatePostBanInfo(account, board, post, postBanInfoUpdate);

                Assertions.assertEquals("UPDATE_BOARD_ADMIN", postBanInfo.getReason());
            }

            @Test
            @DisplayName("다른 게시판 관리자")
            public void updateOtherBoardAdmin() throws Exception {
                Account account = getAccount(enableBoardAdmin);
                Post post = getPost(alreadyBanPostInfo01);
                Board board = post.getBoard();

                PostBanInfoUpdate postBanInfoUpdate = PostBanInfoUpdate.builder()
                        .reason("UPDATE_OTHER_BOARD_ADMIN")
                        .build();

                Assertions.assertThrows(IricomException.class, () -> {
                    banService.updatePostBanInfo(account, board, post, postBanInfoUpdate);
                });
            }

            @Test
            @DisplayName("일반 사용자")
            public void updateAccount() throws Exception {
                Account account = getAccount(common00);
                Post post = getPost(alreadyBanPostInfo01);
                Board board = post.getBoard();

                PostBanInfoUpdate postBanInfoUpdate = PostBanInfoUpdate.builder()
                        .reason("UPDATE_ACCOUNT")
                        .build();

                Assertions.assertThrows(IricomException.class, () -> {
                    banService.updatePostBanInfo(account, board, post, postBanInfoUpdate);
                });
            }

            @Test
            @DisplayName("등록되지 않은 사용자")
            public void updateUnknown() throws Exception {
                Account account = getAccount(unknown00);
                Post post = getPost(alreadyBanPostInfo01);
                Board board = post.getBoard();

                PostBanInfoUpdate postBanInfoUpdate = PostBanInfoUpdate.builder()
                        .reason("UPDATE_UNKNOWN")
                        .build();

                Assertions.assertThrows(IricomException.class, () -> {
                    banService.updatePostBanInfo(account, board, post, postBanInfoUpdate);
                });
            }
        }
    }

    @Nested
    @DisplayName("차단 해제")
    class UnBan {

        @Nested
        @DisplayName("권한별")
        class Auth {

            @Test
            @DisplayName("시스템 관리자")
            public void unbanSystemAdmin() throws Exception {
                Account account = getAccount(systemAdmin);
                Post post = getPost(alreadyBanPostInfo02);
                Board board = post.getBoard();

                PostBanInfo postBanInfo = banService.unbanPost(account, board, post);

                Assertions.assertFalse(postBanInfo.getEnabled());
            }

            @Test
            @DisplayName("게시판 관리자")
            public void unbanBoardAdmin() throws Exception {
                Account account = getAccount(allBoardAdmin);
                Post post = getPost(alreadyBanPostInfo03);
                Board board = post.getBoard();

                PostBanInfo postBanInfo = banService.unbanPost(account, board, post);

                Assertions.assertFalse(postBanInfo.getEnabled());
            }

            @Test
            @DisplayName("다른 게시판 관리자")
            public void unbanOtherBoardAdmin() throws Exception {
                Account account = getAccount(enableBoardAdmin);
                Post post = getPost(alreadyBanPostInfo04);
                Board board = post.getBoard();

                Assertions.assertThrows(IricomException.class, () -> {
                    banService.unbanPost(account, board, post);
                });
            }

            @Test
            @DisplayName("일반 사용자")
            public void unbanAccount() throws Exception {
                Account account = getAccount(common00);
                Post post = getPost(alreadyBanPostInfo04);
                Board board = post.getBoard();

                Assertions.assertThrows(IricomException.class, () -> {
                    banService.unbanPost(account, board, post);
                });
            }

            @Test
            @DisplayName("등록되지 않은 사용자")
            public void unbanUnknown() throws Exception {
                Account account = getAccount(unknown00);
                Post post = getPost(alreadyBanPostInfo04);
                Board board = post.getBoard();

                Assertions.assertThrows(IricomException.class, () -> {
                    banService.unbanPost(account, board, post);
                });
            }
        }
    }

    @Nested
    @DisplayName("다른 게시판의 게시물")
    class OtherBoardPost {

        @Test
        @DisplayName("차단")
        public void ban() throws Exception {
            Account account = getAccount(systemAdmin);
            Board board = getBoard(boardInfo00);
            Post post = getPost(toBanPostInfo04);

            PostBanInfoCreate postBanInfoCreate = PostBanInfoCreate.builder()
                    .reason("not exist post")
                    .build();
            Assertions.assertThrows(IricomException.class, () -> {
                banService.banPost(account, board, post, postBanInfoCreate);
            });
        }

        @Test
        @DisplayName("차단 해제")
        public void unban() throws Exception {
            Account account = getAccount(systemAdmin);
            Board board = getBoard(boardInfo00);
            Post post = getPost(alreadyBanPostInfo01);

            Assertions.assertThrows(IricomException.class, () -> {
                banService.unbanPost(account, board, post);
            });
        }

        @Test
        @DisplayName("시스템 관리자")
        public void get() throws Exception {
            Account account = getAccount(systemAdmin);
            Board board = getBoard(boardInfo00);
            Post post = getPost(alreadyBanPostInfo00);

            Assertions.assertThrows(IricomException.class, () -> {
                banService.getPostBanInfo(account, board, post);
            });
        }
    }
}
