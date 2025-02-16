package com.illdangag.iricom.server.service.post;

import com.illdangag.iricom.server.data.request.PostInfoSearch;
import com.illdangag.iricom.server.data.response.PostInfo;
import com.illdangag.iricom.server.data.response.PostInfoList;
import com.illdangag.iricom.server.exception.IricomException;
import com.illdangag.iricom.server.service.PostService;
import com.illdangag.iricom.server.test.IricomTestSuite;
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
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@DisplayName("service: 게시물 - 목록 조회")
@Slf4j
@Transactional
public class PostServiceSearchTest extends IricomTestSuite {
    @Autowired
    private PostService postService;

    @Autowired
    public PostServiceSearchTest(ApplicationContext context) {
        super(context);
    }

    @Test
    @DisplayName("공개 게시판")
    public void getPostListDisclosedBoard() {
        // 계정 생성
        TestAccountInfo account = this.setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = this.setRandomBoard();
        // 게시물 생성
        this.setRandomPost(board, account, 13);

        PostInfoSearch postInfoSearch = PostInfoSearch.builder()
                .skip(0).limit(100)
                .build();

        PostInfoList postInfoList = postService.getPublishPostInfoList(board.getId(), postInfoSearch);
        Assertions.assertEquals(13, postInfoList.getTotal());
    }

    @Test
    @DisplayName("권한을 사용하지 않고 비공개 게시판")
    public void getPostListUndisclosedBoard() {
        // 계정 생성
        TestAccountInfo account = this.setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = this.setRandomBoard(true, true);
        // 계정 그룹 생성
        this.setRandomAccountGroup(Collections.singletonList(account), Collections.singletonList(board));
        // 게시물 생성
        this.setRandomPost(board, account, 13);

        PostInfoSearch postInfoSearch = PostInfoSearch.builder()
                .skip(0).limit(100)
                .build();

        IricomException iricomException = Assertions.assertThrows(IricomException.class, () -> {
            postService.getPublishPostInfoList(board.getId(), postInfoSearch);
        });

        Assertions.assertEquals("03000000", iricomException.getErrorCode());
        Assertions.assertEquals("Not exist board.", iricomException.getMessage());
    }

    @Test
    @DisplayName("계정 그룹에 포함된 비공개 게시판")
    public void getPostListUndisclosedBoardInAccountGroup() {
        // 계정 생성
        TestAccountInfo account = this.setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = this.setRandomBoard(true, true);
        // 계정 그룹 생성
        this.setRandomAccountGroup(Collections.singletonList(account), Collections.singletonList(board));
        // 게시물 생성
        this.setRandomPost(board, account, 13);

        PostInfoSearch postInfoSearch = PostInfoSearch.builder()
                .skip(0).limit(100)
                .build();

        PostInfoList postInfoList = postService.getPublishPostInfoList(account.getId(), board.getId(), postInfoSearch);

        Assertions.assertEquals(13, postInfoList.getTotal());
    }

    @Test
    @DisplayName("계정이 작성한 게시물 조회")
    public void getAccountCreatedPost() {
        // 계정 생성
        TestAccountInfo account = this.setRandomAccount();
        TestAccountInfo otherAccount = this.setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = this.setRandomBoard();
        // 게시물 생성
        List<TestPostInfo> postList = this.setRandomPost(board, account, 13);
        List<TestPostInfo> otherPostList = this.setRandomPost(board, otherAccount, 4);

        List<String> postIdList = postList.stream()
                .map(TestPostInfo::getId)
                .sorted()
                .collect(Collectors.toList());
        List<String> otherPostIdList = otherPostList.stream()
                .map(TestPostInfo::getId)
                .sorted()
                .collect(Collectors.toList());

        PostInfoSearch postInfoSearch = PostInfoSearch.builder()
                .skip(0).limit(100)
                .build();
        PostInfoList postInfoList = postService.getPostInfoList(account.getId(), postInfoSearch);
        List<String> postIdResultList = postInfoList.getPostInfoList().stream()
                .map(PostInfo::getId)
                .sorted()
                .collect(Collectors.toList());
        Assertions.assertArrayEquals(postIdList.toArray(), postIdResultList.toArray());


        PostInfoList otherPostInfoList = postService.getPostInfoList(otherAccount.getId(), postInfoSearch);
        List<String> otherPostIdResultList = otherPostInfoList.getPostInfoList().stream()
                .map(PostInfo::getId)
                .sorted()
                .collect(Collectors.toList());
        Assertions.assertArrayEquals(otherPostIdList.toArray(), otherPostIdResultList.toArray());
    }
}
