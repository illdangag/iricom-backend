package com.illdangag.iricom.server.service;

import com.illdangag.iricom.server.data.entity.Board;
import com.illdangag.iricom.server.data.entity.Comment;
import com.illdangag.iricom.server.data.entity.Post;
import com.illdangag.iricom.server.data.response.CommentInfo;
import com.illdangag.iricom.server.test.IricomTestSuite;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

@Slf4j
public class CommentServiceTest extends IricomTestSuite {
    @Autowired
    private CommentService commentService;

    @Autowired
    public CommentServiceTest(ApplicationContext context) {
        super(context);
    }

    @Nested
    class ReportCommentTest {

        @Test
        @DisplayName("신고된 댓글 조회")
        public void testCase00() throws Exception {
            Comment comment = getComment(reportedComment00);
            Post post = comment.getPost();
            Board board = post.getBoard();

            CommentInfo commentInfo = commentService.getComment(board, post, comment);
            Assertions.assertTrue(commentInfo.getIsReport());
            Assertions.assertNull(commentInfo.getContent());
        }
    }
}
