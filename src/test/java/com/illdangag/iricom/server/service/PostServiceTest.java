package com.illdangag.iricom.server.service;

import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.data.entity.Board;
import com.illdangag.iricom.server.data.entity.Post;
import com.illdangag.iricom.server.data.entity.PostState;
import com.illdangag.iricom.server.data.response.PostInfo;
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
    class ReportedPostTest {

        @Test
        @DisplayName("신고된 게시물 조회")
        public void testCase00() throws Exception {
            Account account = getAccount(common00);
            Post post = getPost(reportedPost00);
            Board board = post.getBoard();

            PostInfo postInfo = postService.getPostInfo(account, board, post, PostState.PUBLISH);
            Assertions.assertTrue(postInfo.getIsReport());
            Assertions.assertEquals("", postInfo.getContent());
        }
    }
}
