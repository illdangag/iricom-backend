package com.illdangag.iricom.server.service.post;

import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.data.entity.PostState;
import com.illdangag.iricom.server.data.entity.PostType;
import com.illdangag.iricom.server.data.request.PostInfoSearch;
import com.illdangag.iricom.server.data.response.PostInfo;
import com.illdangag.iricom.server.data.response.PostInfoList;
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
import java.util.List;
import java.util.stream.Collectors;

@DisplayName("service: 게시물 - 목록 조회")
@Slf4j
public class PostServiceSearchTest extends IricomTestSuite {
    @Autowired
    private PostService postService;

    // 공개 게시판
    private final TestBoardInfo boardInfo00 = TestBoardInfo.builder()
            .title("boardInfo00").isEnabled(true).adminList(Collections.singletonList(allBoardAdmin)).build();
    // 비공개 게시판
    private final TestBoardInfo undisclosedBoardInfo00 = TestBoardInfo.builder()
            .title("undisclosedBoardInfo00").isEnabled(true).undisclosed(true).adminList(Collections.singletonList(allBoardAdmin)).build();
    // 계정 그룹
    private final TestAccountGroupInfo testAccountGroupInfo00 = TestAccountGroupInfo.builder()
            .title("testAccountGroupInfo00").description("description").deleted(false)
            .accountList(Collections.singletonList(common00)).boardList(Collections.singletonList(undisclosedBoardInfo00))
            .build();
    // 게시물
    private final TestPostInfo testPostInfo00 = TestPostInfo.builder()
            .title("post00").content("content").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .creator(common00).board(boardInfo00).build();
    private final TestPostInfo undisclosedPost01 = TestPostInfo.builder()
            .title("undisclosedPost01").content("content").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .creator(common00).board(undisclosedBoardInfo00).build();

    @Autowired
    public PostServiceSearchTest(ApplicationContext context) {
        super(context);

        addTestBoardInfo(boardInfo00, undisclosedBoardInfo00);
        addTestPostInfo(testPostInfo00, undisclosedPost01);
        addTestAccountGroupInfo(testAccountGroupInfo00);

        init();
    }

    @Test
    @DisplayName("공개 게시판")
    public void getPostListDisclosedBoard() throws Exception {
        String boardId = getBoardId(boardInfo00);
        String postId = getPostId(testPostInfo00);

        PostInfoSearch postInfoSearch = PostInfoSearch.builder()
                .skip(0).limit(100)
                .build();
        PostInfoList postInfoList = postService.getPublishPostInfoList(boardId, postInfoSearch);
        List<String> postIdList = postInfoList.getPostInfoList().stream()
                .map(item -> item.getId())
                .collect(Collectors.toList());

        Assertions.assertTrue(postIdList.contains(postId));
    }

    @Test
    @DisplayName("권한을 사용하지 않고 비공개 게시판")
    public void getPostListUndisclosedBoard() throws Exception {
        String boardId = getBoardId(undisclosedBoardInfo00);

        PostInfoSearch postInfoSearch = PostInfoSearch.builder()
                .skip(0).limit(100)
                .build();

        IricomException iricomException = Assertions.assertThrows(IricomException.class, () -> {
            postService.getPublishPostInfoList(boardId, postInfoSearch);
        });

        Assertions.assertEquals("03000000", iricomException.getErrorCode());
        Assertions.assertEquals("Not exist board.", iricomException.getMessage());
    }

    @Test
    @DisplayName("계정 그룹에 포함된 비공개 게시판")
    public void getPostListUndisclosedBoardInAccountGroup() throws Exception {
        Account account = getAccount(common00);
        String boardId = getBoardId(undisclosedBoardInfo00);
        String postId = getPostId(undisclosedPost01);

        PostInfoSearch postInfoSearch = PostInfoSearch.builder()
                .skip(0).limit(100)
                .build();

        PostInfoList postInfoList = postService.getPublishPostInfoList(account, boardId, postInfoSearch);
        List<String> postIdList = postInfoList.getPostInfoList().stream()
                .map(PostInfo::getId)
                .collect(Collectors.toList());

        Assertions.assertTrue(postIdList.contains(postId));
    }

    @Test
    @DisplayName("계정이 작성한 게시물 조회")
    public void getAccountCreatedPost() throws Exception {
        Account account = getAccount(common00);
        String postId00 = getPostId(testPostInfo00);
        String postId01 = getPostId(undisclosedPost01);

        List<String> list = getAllList(PostInfoSearch.builder().build(), searchRequest -> {
            PostInfoSearch postInfoSearch = (PostInfoSearch) searchRequest;
            PostInfoList postInfoList = postService.getPostInfoList(account, postInfoSearch);
            return postInfoList.getPostInfoList().stream()
                    .map(PostInfo::getId)
                    .collect(Collectors.toList());
        });

        Assertions.assertTrue(list.contains(postId00));
        Assertions.assertTrue(list.contains(postId01));
    }
}
