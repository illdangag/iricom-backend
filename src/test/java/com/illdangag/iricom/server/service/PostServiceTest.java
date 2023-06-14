package com.illdangag.iricom.server.service;

import com.illdangag.iricom.server.data.entity.*;
import com.illdangag.iricom.server.data.request.PostInfoCreate;
import com.illdangag.iricom.server.data.response.PostInfo;
import com.illdangag.iricom.server.exception.IricomException;
import com.illdangag.iricom.server.test.IricomTestSuite;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

@Slf4j
public class PostServiceTest extends IricomTestSuite {
    @Autowired
    private PostService postService;

    @Autowired
    public PostServiceTest(ApplicationContext context) {
        super(context);
    }

    @Nested
    @DisplayName("게시물 신고")
    class ReportedPostTest {
        @Test
        @DisplayName("신고된 게시물 조회")
        public void testCase00() throws Exception {
            Account account = getAccount(common00);
            Post post = getPost(reportedPost00);
            Board board = post.getBoard();

            PostInfo postInfo = postService.getPostInfo(account, board, post, PostState.PUBLISH);
            Assertions.assertTrue(postInfo.getIsReport());
            Assertions.assertNull(postInfo.getContent());
        }
    }

    @Nested
    @DisplayName("게시물 생성")
    class CreatePostTest {
        @Test
        @DisplayName("닉네임이 등록되지 않은 사용자")
        public void postUnregisteredAccount() throws Exception {
            Account account = getAccount(unknown00);
            Board board = getBoard(createBoard);

            PostInfoCreate postInfoCreate = PostInfoCreate.builder()
                    .title("Unregistered account post title")
                    .content("contents...")
                    .type(PostType.POST)
                    .build();

            Assertions.assertThrows(IricomException.class, () -> {
                postService.createPostInfo(account, board, postInfoCreate);
            });
        }
    }
}
