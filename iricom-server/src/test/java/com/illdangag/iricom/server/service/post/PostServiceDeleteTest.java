package com.illdangag.iricom.server.service.post;

import com.illdangag.iricom.server.data.entity.type.PostState;
import com.illdangag.iricom.server.data.entity.type.PostType;
import com.illdangag.iricom.server.data.response.PostInfo;
import com.illdangag.iricom.server.exception.IricomException;
import com.illdangag.iricom.server.service.PostService;
import com.illdangag.iricom.server.test.IricomTestSuite;
import com.illdangag.iricom.server.test.data.wrapper.TestAccountGroupInfo;
import com.illdangag.iricom.server.test.data.wrapper.TestAccountInfo;
import com.illdangag.iricom.server.test.data.wrapper.TestBoardInfo;
import com.illdangag.iricom.server.test.data.wrapper.TestPostInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.Collections;

@DisplayName("service: 게시물 - 삭제")
@Slf4j
@Transactional
public class PostServiceDeleteTest extends IricomTestSuite {
    @Autowired
    private PostService postService;

    // 게시판
    private final TestBoardInfo testBoardInfo00 = TestBoardInfo.builder()
            .title("testBoardInfo00").isEnabled(true).adminList(Arrays.asList(allBoardAdmin, common01))
            .removeAdminList(Collections.singletonList(common01)).build();
    private final TestBoardInfo testBoardInfo01 = TestBoardInfo.builder()
            .title("testBoardInfo01").isEnabled(false).adminList(Collections.singletonList(allBoardAdmin)).build();
    private final TestBoardInfo testBoardInfo02 = TestBoardInfo.builder()
            .title("testBoardInfo02").isEnabled(true).undisclosed(true).adminList(Collections.singletonList(allBoardAdmin)).build();
    private final TestBoardInfo testBoardInfo03 = TestBoardInfo.builder()
            .title("testBoardInfo03").isEnabled(true).undisclosed(true).adminList(Collections.singletonList(allBoardAdmin)).build();
    // 게시물
    private final TestPostInfo testPostInfo00 = TestPostInfo.builder()
            .title("testPostInfo00").content("testPostInfo00").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .creator(common00).board(testBoardInfo00)
            .build();
    private final TestPostInfo testPostInfo01 = TestPostInfo.builder()
            .title("testPostInfo01").content("testPostInfo01").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .creator(common00).board(testBoardInfo00)
            .build();
    private final TestPostInfo testPostInfo02 = TestPostInfo.builder()
            .title("testPostInfo02").content("testPostInfo02").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .creator(common00).board(testBoardInfo01)
            .build();
    private final TestPostInfo testPostInfo03 = TestPostInfo.builder()
            .title("testPostInfo03").content("testPostInfo03").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .creator(common00).board(testBoardInfo02)
            .build();
    private final TestPostInfo testPostInfo04 = TestPostInfo.builder()
            .title("testPostInfo04").content("testPostInfo04").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .creator(common00).board(testBoardInfo03)
            .build();
    private final TestPostInfo testPostInfo05 = TestPostInfo.builder()
            .title("testPostInfo05").content("testPostInfo05").isAllowComment(true)
            .postType(PostType.NOTIFICATION).postState(PostState.PUBLISH)
            .creator(allBoardAdmin).board(testBoardInfo00)
            .build();
    private final TestPostInfo testPostInfo06 = TestPostInfo.builder()
            .title("testPostInfo06").content("testPostInfo05").isAllowComment(true)
            .postType(PostType.NOTIFICATION).postState(PostState.PUBLISH)
            .creator(common01).board(testBoardInfo00)
            .build();
    // 계정 그룹
    private final TestAccountGroupInfo testAccountGroupInfo00 = TestAccountGroupInfo.builder()
            .title("testAccountGroupInfo00").description("description")
            .accountList(Collections.singletonList(common00)).boardList(Collections.singletonList(testBoardInfo02))
            .deleted(true)
            .build();
    private final TestAccountGroupInfo testAccountGroupInfo01 = TestAccountGroupInfo.builder()
            .title("testAccountGroupInfo01").description("description")
            .accountList(Collections.singletonList(common00)).boardList(Collections.singletonList(testBoardInfo03))
            .build();

    @Autowired
    public PostServiceDeleteTest(ApplicationContext context) {
        super(context);

        addTestBoardInfo(testBoardInfo00, testBoardInfo01, testBoardInfo02, testBoardInfo03);
        addTestPostInfo(testPostInfo00, testPostInfo01, testPostInfo02, testPostInfo03, testPostInfo04, testPostInfo05,
                testPostInfo06);
        addTestAccountGroupInfo(testAccountGroupInfo00, testAccountGroupInfo01);

        init();
    }

    @Test
    @DisplayName("게시물 삭제")
    public void deletePost() {
        // 계정 생성
        TestAccountInfo account = this.setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = this.setRandomBoard();
        // 게시물 생성
        TestPostInfo post = this.setRandomPost(board, account);

        PostInfo postInfo = this.postService.deletePostInfo(account.getId(), board.getId(), post.getId());

        Assertions.assertEquals(post.getId(), postInfo.getId());
        Assertions.assertTrue(postInfo.getDeleted());
        Assertions.assertNull(postInfo.getContent());
    }

    @Test
    @DisplayName("자신이 작성하지 않은 게시물 삭제")
    public void notCreator() {
        // 계정 생성
        TestAccountInfo account = this.setRandomAccount();
        TestAccountInfo other = this.setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = this.setRandomBoard();
        // 게시물 생성
        TestPostInfo post = this.setRandomPost(board, account);

        IricomException iricomException = Assertions.assertThrows(IricomException.class, () -> {
            this.postService.deletePostInfo(other.getId(), board.getId(), post.getId());
        });

        Assertions.assertNotEquals(account.getId(), other.getId());
        Assertions.assertEquals("04000002", iricomException.getErrorCode());
        Assertions.assertEquals("Invalid authorization.", iricomException.getMessage());
    }

    @Test
    @DisplayName("비활성화 게시판의 게시물 삭제")
    public void disabledBoard() {
        // 계정 생성
        TestAccountInfo account = this.setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = this.setRandomBoard();
        // 게시물 생성
        TestPostInfo post = this.setRandomPost(board, account);
        // 게시판 비활성화
        this.setDisabledBoard(Collections.singletonList(board));

        IricomException iricomException = Assertions.assertThrows(IricomException.class, () -> {
            this.postService.deletePostInfo(account.getId(), board.getId(), post.getId());
        });

        Assertions.assertEquals("03000001", iricomException.getErrorCode());
        Assertions.assertEquals("Board is disabled.", iricomException.getMessage());
    }

    @Test
    @DisplayName("권한 없는 비공개 게시판의 게시물 삭제")
    public void noAuthUndisclosedBoard() {
        // 계정 생성
        TestAccountInfo account = this.setRandomAccount();
        TestAccountInfo other = this.setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = this.setRandomBoard(true, true);
        // 계정 그룹 생성
        this.setRandomAccountGroup(Collections.singletonList(account), Collections.singletonList(board));
        // 게시물 생성
        TestPostInfo post = this.setRandomPost(board, account);

        IricomException iricomException = Assertions.assertThrows(IricomException.class, () -> {
            this.postService.deletePostInfo(other.getId(), board.getId(), post.getId());
        });

        Assertions.assertEquals("03000000", iricomException.getErrorCode());
        Assertions.assertEquals("Not exist board.", iricomException.getMessage());
    }

    @Test
    @DisplayName("권한이 있는 비공게 게시판의 게시물 삭제")
    public void undisclosedBoard() {
        // 계정 생성
        TestAccountInfo account = this.setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = this.setRandomBoard(true, true);
        // 계정 그룹 생성
        this.setRandomAccountGroup(Collections.singletonList(account), Collections.singletonList(board));
        // 게시물 생성
        TestPostInfo post = this.setRandomPost(board, account);

        PostInfo postInfo = this.postService.deletePostInfo(account.getId(), board.getId(), post.getId());

        Assertions.assertEquals(post.getId(), postInfo.getId());
        Assertions.assertNull(postInfo.getContent());
    }

    @Test
    @DisplayName("공지 사항 삭제")
    public void deleteNotification() {
        // 계정 생성
        TestAccountInfo account = this.setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = this.setRandomBoard(Collections.singletonList(account));
        // 게시물 생성
        TestPostInfo post = this.setRandomPost(board, account, PostType.NOTIFICATION, PostState.PUBLISH);

        PostInfo postInfo = this.postService.deletePostInfo(account.getId(), board.getId(), post.getId());

        Assertions.assertEquals(post.getId(), postInfo.getId());
        Assertions.assertNull(postInfo.getContent());
    }

    @Test
    @DisplayName("게시판 관리자에서 삭제된 계정이 공지 사항 게시물 삭제")
    public void deleteNoAuthNotification() {
        // 계정 생성
        TestAccountInfo account = this.setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = this.setRandomBoard(Collections.singletonList(account));
        // 게시물 생성
        TestPostInfo post = this.setRandomPost(board, account, PostType.NOTIFICATION, PostState.PUBLISH);
        // 게시판 관리자 삭제
        this.deleteBoardAdmin(board, Collections.singletonList(account));

        IricomException iricomException = Assertions.assertThrows(IricomException.class, () -> {
            this.postService.deletePostInfo(account.getId(), board.getId(), post.getId());
        });

        Assertions.assertEquals("04000001", iricomException.getErrorCode());
        Assertions.assertEquals("Invalid authorization.", iricomException.getMessage());
    }
}
