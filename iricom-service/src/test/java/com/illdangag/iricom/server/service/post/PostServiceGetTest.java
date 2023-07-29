package com.illdangag.iricom.server.service.post;

import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.data.entity.PostState;
import com.illdangag.iricom.server.data.entity.PostType;
import com.illdangag.iricom.server.data.response.PostInfo;
import com.illdangag.iricom.server.exception.IricomException;
import com.illdangag.iricom.server.service.PostService;
import com.illdangag.iricom.server.test.IricomTestSuite;
import com.illdangag.iricom.server.test.data.wrapper.TestAccountGroupInfo;
import com.illdangag.iricom.server.test.data.wrapper.TestBoardInfo;
import com.illdangag.iricom.server.test.data.wrapper.TestPostInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;
import java.util.Collections;

@DisplayName("service: 게시물 - 조회")
@Slf4j
public class PostServiceGetTest extends IricomTestSuite {
    @Autowired
    private PostService postService;

    // 공개 게시판
    private final TestBoardInfo disclosedBoardInfo00 = TestBoardInfo.builder()
            .title("disclosedBoardInfo00").isEnabled(true)
            .adminList(Collections.singletonList(allBoardAdmin)).build();
    // 비공개 게시판
    private final TestBoardInfo undisclosedBoardInfo00 = TestBoardInfo.builder()
            .title("undisclosedBoardInfo00").isEnabled(true).undisclosed(true)
            .adminList(Collections.singletonList(allBoardAdmin)).build();
    private final TestBoardInfo undisclosedBoardInfo01 = TestBoardInfo.builder()
            .title("undisclosedBoardInfo00").isEnabled(true).undisclosed(true)
            .adminList(Collections.singletonList(allBoardAdmin)).build();

    // 계정 그룹
    private final TestAccountGroupInfo accountGroupInfo00 = TestAccountGroupInfo.builder()
            .title("accountGroupInfo00").description("description")
            .accountList(Arrays.asList(common00)).boardList(Arrays.asList(undisclosedBoardInfo00))
            .build();
    private final TestAccountGroupInfo accountGroupInfo01 = TestAccountGroupInfo.builder()
            .title("accountGroupInfo01").description("description").deleted(true)
            .accountList(Arrays.asList(common00)).boardList(Arrays.asList(undisclosedBoardInfo01))
            .build();

    // 게시물
    private final TestPostInfo postInfo00 = TestPostInfo.builder()
            .title("postInfo00").content("content").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .creator(common00).board(disclosedBoardInfo00).build();
    private final TestPostInfo undisclosedPost00 = TestPostInfo.builder()
            .title("undisclosedPost00").content("content").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .creator(common00).board(undisclosedBoardInfo00).build();
    private final TestPostInfo undisclosedPost01 = TestPostInfo.builder()
            .title("undisclosedPost01").content("content").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .creator(common00).board(undisclosedBoardInfo01).build();

    @Autowired
    public PostServiceGetTest(ApplicationContext context) {
        super(context);

        addTestBoardInfo(disclosedBoardInfo00, undisclosedBoardInfo00, undisclosedBoardInfo01);
        addTestPostInfo(postInfo00, undisclosedPost00, undisclosedPost01);
        addTestAccountGroupInfo(accountGroupInfo00, accountGroupInfo01);

        init();
    }

    @Test
    @DisplayName("공개된 게시판의 게시물을 권한 없이 조회")
    public void getDisclosedBoardPost() throws Exception {
        String postId = getPostId(postInfo00);

        PostInfo postInfo = postService.getPostInfo(postId, PostState.PUBLISH, true);

        Assertions.assertNotNull(postInfo);
    }

    @Test
    @DisplayName("비공개 게시판의 게시물 조회")
    public void getUndisclosedBoardPost() throws Exception {
        String postId = getPostId(undisclosedPost00);

        Assertions.assertThrows(IricomException.class, () -> {
            postService.getPostInfo(postId, PostState.PUBLISH, true);
        });
    }

    @Test
    @DisplayName("계정 그룹에 포함된 게시판의 게시물을 조회")
    public void getPostInAccountGroup() throws Exception {
        Account account = getAccount(common00);
        String postId = getPostId(undisclosedPost00);

        PostInfo postInfo = postService.getPostInfo(account, postId, PostState.PUBLISH, true);

        Assertions.assertNotNull(postInfo);
        Assertions.assertEquals(postId, postInfo.getId());
    }

    @Test
    @DisplayName("삭제된 계정 그룹에 포함된 게시판")
    public void getUndisclosedBoardPostInDeletedAccountGroup() throws Exception {
        Account account = getAccount(common00);
        String postId = getPostId(undisclosedPost01);

        IricomException iricomException = Assertions.assertThrows(IricomException.class, () -> {
            postService.getPostInfo(account, postId, PostState.PUBLISH, true);
        });

        Assertions.assertEquals("03000000", iricomException.getErrorCode());
        Assertions.assertEquals("Not exist board.", iricomException.getMessage());
    }
}
