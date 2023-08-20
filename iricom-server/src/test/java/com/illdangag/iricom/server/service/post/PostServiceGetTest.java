package com.illdangag.iricom.server.service.post;

import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.data.entity.type.PostState;
import com.illdangag.iricom.server.data.entity.type.PostType;
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

import java.util.Collections;

@DisplayName("service: 게시물 - 조회")
@Slf4j
public class PostServiceGetTest extends IricomTestSuite {
    @Autowired
    private PostService postService;

    @Autowired
    public PostServiceGetTest(ApplicationContext context) {
        super(context);
    }

    @Test
    @DisplayName("공개된 게시판의 게시물을 권한 없이 조회")
    public void getDisclosedBoardPost() {
        TestBoardInfo testBoardInfo = TestBoardInfo.builder()
                .title("testBoardInfo").isEnabled(true)
                .adminList(Collections.singletonList(allBoardAdmin)).build();

        TestPostInfo testPostInfo = TestPostInfo.builder()
                .title("testPostInfo").content("content").isAllowComment(true)
                .postType(PostType.POST).postState(PostState.PUBLISH)
                .creator(common00).board(testBoardInfo).build();

        addTestBoardInfo(testBoardInfo);
        addTestPostInfo(testPostInfo);
        init();

        String postId = getPostId(testPostInfo);
        PostInfo postInfo = postService.getPostInfo(postId, PostState.PUBLISH, true);

        Assertions.assertNotNull(postInfo);
    }

    @Test
    @DisplayName("비공개 게시판의 게시물 조회")
    public void getUndisclosedBoardPost() {
        TestBoardInfo testBoardInfo = TestBoardInfo.builder()
                .title("testBoardInfo").isEnabled(true).undisclosed(true)
                .adminList(Collections.singletonList(allBoardAdmin)).build();

        TestPostInfo testPostInfo = TestPostInfo.builder()
                .title("testPostInfo").content("content").isAllowComment(true)
                .postType(PostType.POST).postState(PostState.PUBLISH)
                .creator(common00).board(testBoardInfo).build();

        TestAccountGroupInfo testAccountGroupInfo = TestAccountGroupInfo.builder()
                .title("testAccountGroupInfo").description("description")
                .accountList(Collections.singletonList(common00))
                .boardList(Collections.singletonList(testBoardInfo))
                .build();

        addTestBoardInfo(testBoardInfo);
        addTestPostInfo(testPostInfo);
        addTestAccountGroupInfo(testAccountGroupInfo);
        init();

        String postId = getPostId(testPostInfo);

        Assertions.assertThrows(IricomException.class, () -> {
            postService.getPostInfo(postId, PostState.PUBLISH, true);
        });
    }

    @Test
    @DisplayName("계정 그룹에 포함된 게시판의 게시물을 조회")
    public void getPostInAccountGroup() {
        TestBoardInfo testBoardInfo = TestBoardInfo.builder()
                .title("testBoardInfo").isEnabled(true).undisclosed(true)
                .adminList(Collections.singletonList(allBoardAdmin)).build();

        TestPostInfo testPostInfo = TestPostInfo.builder()
                .title("testPostInfo").content("content").isAllowComment(true)
                .postType(PostType.POST).postState(PostState.PUBLISH)
                .creator(common00).board(testBoardInfo).build();

        TestAccountGroupInfo testAccountGroupInfo = TestAccountGroupInfo.builder()
                .title("testAccountGroupInfo").description("description")
                .accountList(Collections.singletonList(common00))
                .boardList(Collections.singletonList(testBoardInfo))
                .build();

        addTestBoardInfo(testBoardInfo);
        addTestPostInfo(testPostInfo);
        addTestAccountGroupInfo(testAccountGroupInfo);
        init();

        Account account = getAccount(common00);
        String postId = getPostId(testPostInfo);

        PostInfo postInfo = postService.getPostInfo(account, postId, PostState.PUBLISH, true);

        Assertions.assertNotNull(postInfo);
        Assertions.assertEquals(postId, postInfo.getId());
    }

    @Test
    @DisplayName("삭제된 계정 그룹에 포함된 게시판")
    public void getUndisclosedBoardPostInDeletedAccountGroup() {
        TestBoardInfo testBoardInfo = TestBoardInfo.builder()
                .title("testBoardInfo").isEnabled(true).undisclosed(true)
                .adminList(Collections.singletonList(allBoardAdmin)).build();

        TestPostInfo testPostInfo = TestPostInfo.builder()
                .title("testPostInfo").content("content").isAllowComment(true)
                .postType(PostType.POST).postState(PostState.PUBLISH)
                .creator(common00).board(testBoardInfo).build();

        TestAccountGroupInfo testAccountGroupInfo = TestAccountGroupInfo.builder()
                .title("testAccountGroupInfo").description("description").deleted(true)
                .accountList(Collections.singletonList(common00))
                .boardList(Collections.singletonList(testBoardInfo))
                .build();

        addTestBoardInfo(testBoardInfo);
        addTestPostInfo(testPostInfo);
        addTestAccountGroupInfo(testAccountGroupInfo);
        init();

        Account account = getAccount(common00);
        String postId = getPostId(testPostInfo);

        IricomException iricomException = Assertions.assertThrows(IricomException.class, () -> {
            postService.getPostInfo(account, postId, PostState.PUBLISH, true);
        });

        Assertions.assertEquals("03000000", iricomException.getErrorCode());
        Assertions.assertEquals("Not exist board.", iricomException.getMessage());
    }
}
