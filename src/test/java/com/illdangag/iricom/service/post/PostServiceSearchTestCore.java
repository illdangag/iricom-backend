package com.illdangag.iricom.service.post;

import com.illdangag.iricom.core.data.request.PostInfoSearch;
import com.illdangag.iricom.core.data.response.PostInfo;
import com.illdangag.iricom.core.data.response.PostInfoList;
import com.illdangag.iricom.core.exception.IricomException;
import com.illdangag.iricom.core.service.PostService;
import com.illdangag.iricom.core.test.data.wrapper.TestAccountInfo;
import com.illdangag.iricom.core.test.data.wrapper.TestBoardInfo;
import com.illdangag.iricom.core.test.data.wrapper.TestPostInfo;
import com.illdangag.iricom.IricomTestServiceSuite;
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
public class PostServiceSearchTestCore extends IricomTestServiceSuite {
    @Autowired
    private PostService postService;

    @Autowired
    public PostServiceSearchTestCore(ApplicationContext context) {
        super(context);
    }

    @Test
    @DisplayName("공개 게시판")
    public void getPostListDisclosedBoard() {
        // 계정 생성
        TestAccountInfo account = setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = setRandomBoard();
        // 게시물 생성
        setRandomPost(board, account, 13);

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
        TestAccountInfo account = setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = setRandomBoard(true, true);
        // 계정 그룹 생성
        setRandomAccountGroup(Collections.singletonList(account), Collections.singletonList(board));
        // 게시물 생성
        setRandomPost(board, account, 13);

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
        TestAccountInfo account = setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = setRandomBoard(true, true);
        // 계정 그룹 생성
        setRandomAccountGroup(Collections.singletonList(account), Collections.singletonList(board));
        // 게시물 생성
        setRandomPost(board, account, 13);

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
        TestAccountInfo account = setRandomAccount();
        TestAccountInfo otherAccount = setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = setRandomBoard();
        // 게시물 생성
        List<TestPostInfo> postList = setRandomPost(board, account, 13);
        List<TestPostInfo> otherPostList = setRandomPost(board, otherAccount, 4);

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
