package com.illdangag.iricom.server.service.block.comment;

import com.illdangag.iricom.server.data.entity.type.PostState;
import com.illdangag.iricom.server.data.entity.type.PostType;
import com.illdangag.iricom.server.data.request.CommentBlockInfoCreate;
import com.illdangag.iricom.server.data.request.PostBlockInfoCreate;
import com.illdangag.iricom.server.data.response.CommentBlockInfo;
import com.illdangag.iricom.server.data.response.CommentInfo;
import com.illdangag.iricom.server.exception.IricomException;
import com.illdangag.iricom.server.service.BlockService;
import com.illdangag.iricom.server.service.CommentService;
import com.illdangag.iricom.server.test.IricomTestSuite;
import com.illdangag.iricom.server.test.data.wrapper.TestAccountInfo;
import com.illdangag.iricom.server.test.data.wrapper.TestBoardInfo;
import com.illdangag.iricom.server.test.data.wrapper.TestCommentInfo;
import com.illdangag.iricom.server.test.data.wrapper.TestPostInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javax.transaction.Transactional;
import java.util.Collections;

@DisplayName("service: 차단 - 댓글 차단")
@Slf4j
@Transactional
public class BlockServiceCommentBlockTest extends IricomTestSuite {
    @Autowired
    private BlockService blockService;
    @Autowired
    private CommentService commentService;

    public BlockServiceCommentBlockTest(ApplicationContext context) {
        super(context);
    }

    @Test
    @DisplayName("시스템 관리자")
    void blockSystemAdmin() {
        // 계정 생성
        TestAccountInfo account = this.setRandomAccount(1).get(0);
        // 게시판 생성
        TestBoardInfo board = this.setRandomBoard(1).get(0);
        // 게시물 생성
        TestPostInfo post = TestPostInfo.builder()
                .title("post title").content("content").isAllowComment(true)
                .postType(PostType.POST).postState(PostState.PUBLISH)
                .creator(account).board(board)
                .build();
        this.setPost(post);
        // 댓글 생성
        TestCommentInfo comment = TestCommentInfo.builder()
                .content("comment").creator(account).post(post)
                .build();
        this.setComment(comment);

        // 댓글 차단
        CommentBlockInfoCreate commentBlockInfoCreate = CommentBlockInfoCreate.builder()
                .reason("block reason")
                .build();
        CommentBlockInfo commentBlockInfo = this.blockService.blockComment(systemAdmin.getId(), board.getId(), post.getId(), comment.getId(), commentBlockInfoCreate);

        Assertions.assertEquals("block reason", commentBlockInfo.getReason());
        Assertions.assertNotNull(commentBlockInfo.getCommentInfo());
        Assertions.assertEquals(comment.getId(), commentBlockInfo.getCommentInfo().getId());
        Assertions.assertEquals("comment", commentBlockInfo.getCommentInfo().getContent());
    }

    @Test
    @DisplayName("게시판 관리자")
    void blockBoardAdmin() {
        // 계정 생성
        TestAccountInfo boardAdmin = this.setRandomAccount();
        TestAccountInfo account = this.setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = TestBoardInfo.builder()
                .title("board").isEnabled(true).adminList(Collections.singletonList(boardAdmin)).build();
        this.setBoard(board);
        // 게시물 생성
        TestPostInfo post = TestPostInfo.builder()
                .title("post title").content("content").isAllowComment(true)
                .postType(PostType.POST).postState(PostState.PUBLISH)
                .creator(account).board(board)
                .build();
        this.setPost(post);
        // 댓글 생성
        TestCommentInfo comment = TestCommentInfo.builder()
                .content("comment").creator(account).post(post)
                .build();
        this.setComment(comment);

        // 댓글 차단
        CommentBlockInfoCreate commentBlockInfoCreate = CommentBlockInfoCreate.builder()
                .reason("block reason")
                .build();

        CommentBlockInfo commentBlockInfo = this.blockService.blockComment(boardAdmin.getId(), board.getId(), post.getId(), comment.getId(), commentBlockInfoCreate);

        Assertions.assertEquals("block reason", commentBlockInfo.getReason());
        Assertions.assertNotNull(commentBlockInfo.getCommentInfo());
        Assertions.assertEquals(comment.getId(), commentBlockInfo.getCommentInfo().getId());
        Assertions.assertEquals("comment", commentBlockInfo.getCommentInfo().getContent());
    }

    @Test
    @DisplayName("다른 게시판 관리자")
    void blockOtherBoardAdmin() {
        // 계정 생성
        TestAccountInfo boardAdmin00 = this.setRandomAccount();
        TestAccountInfo boardAdmin01 = this.setRandomAccount();
        TestAccountInfo account = this.setRandomAccount();
        // 게시판 생성
        TestBoardInfo board00 = TestBoardInfo.builder()
                .title("board").isEnabled(true).adminList(Collections.singletonList(boardAdmin00)).build();
        this.setBoard(board00);
        TestBoardInfo board01 = TestBoardInfo.builder()
                .title("board").isEnabled(true).adminList(Collections.singletonList(boardAdmin01)).build();
        this.setBoard(board01);
        // 게시물 생성
        TestPostInfo post = TestPostInfo.builder()
                .title("post title").content("content").isAllowComment(true)
                .postType(PostType.POST).postState(PostState.PUBLISH)
                .creator(account).board(board00)
                .build();
        this.setPost(post);
        // 댓글 생성
        TestCommentInfo comment = TestCommentInfo.builder()
                .content("comment").creator(account).post(post)
                .build();
        this.setComment(comment);

        CommentBlockInfoCreate commentBlockInfoCreate = CommentBlockInfoCreate.builder()
                .reason("block reason")
                .build();

        IricomException exception = Assertions.assertThrows(IricomException.class, () -> {
            this.blockService.blockComment(boardAdmin01.getId(), board00.getId(), post.getId(), comment.getId(), commentBlockInfoCreate);
        });

        Assertions.assertEquals("05000009", exception.getErrorCode());
    }

    @Test
    @DisplayName("일반 사용자")
    void blockAccount() {
        // 계정 생성
        TestAccountInfo account = this.setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = this.setRandomBoard();
        // 게시물 생성
        TestPostInfo post = TestPostInfo.builder()
                .title("post title").content("content").isAllowComment(true)
                .postType(PostType.POST).postState(PostState.PUBLISH)
                .creator(account).board(board)
                .build();
        this.setPost(post);
        // 댓글 생성
        TestCommentInfo comment = TestCommentInfo.builder()
                .content("comment").creator(account).post(post)
                .build();
        this.setComment(comment);

        CommentBlockInfoCreate commentBlockInfoCreate = CommentBlockInfoCreate.builder()
                .reason("block reason")
                .build();

        IricomException exception = Assertions.assertThrows(IricomException.class, () -> {
            this.blockService.blockComment(account.getId(), board.getId(), post.getId(), comment.getId(), commentBlockInfoCreate);
        });

        Assertions.assertEquals("05000009", exception.getErrorCode());
    }

    @Test
    @DisplayName("등록되지 않은 사용자")
    void blockUnknown() {
        // 계정 생성
        TestAccountInfo account = this.setRandomAccount();
        TestAccountInfo unregisteredAccount = this.setRandomUnregisteredAccount();
        // 게시판 생성
        TestBoardInfo board = this.setRandomBoard();
        // 게시물 생성
        TestPostInfo post = TestPostInfo.builder()
                .title("post title").content("content").isAllowComment(true)
                .postType(PostType.POST).postState(PostState.PUBLISH)
                .creator(account).board(board)
                .build();
        this.setPost(post);
        // 댓글 생성
        TestCommentInfo comment = TestCommentInfo.builder()
                .content("comment").creator(account).post(post)
                .build();
        this.setComment(comment);

        // 댓글 차단
        CommentBlockInfoCreate commentBlockInfoCreate = CommentBlockInfoCreate.builder()
                .reason("block reason")
                .build();

        IricomException exception = Assertions.assertThrows(IricomException.class, () -> {
            this.blockService.blockComment(unregisteredAccount.getId(), board.getId(), post.getId(), comment.getId(), commentBlockInfoCreate);
        });

        Assertions.assertEquals("05000009", exception.getErrorCode());
    }

    @Test
    @DisplayName("이미 차단된 게시물의 댓글")
    void alreadyBlockPost() {
        // 계정 생성
        TestAccountInfo account = this.setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = this.setRandomBoard();
        // 게시물 생성
        TestPostInfo post = TestPostInfo.builder()
                .title("post title").content("content").isAllowComment(true)
                .postType(PostType.POST).postState(PostState.PUBLISH)
                .creator(account).board(board)
                .build();
        this.setPost(post);
        // 댓글 생성
        TestCommentInfo comment = TestCommentInfo.builder()
                .content("comment").creator(account).post(post)
                .build();
        this.setComment(comment);

        // 게시물 차단
        PostBlockInfoCreate postBlockInfoCreate = PostBlockInfoCreate.builder()
                .reason("block reason")
                .build();
        this.blockService.blockPost(systemAdmin.getId(), board.getId(), post.getId(), postBlockInfoCreate);

        // 댓글 차단
        CommentBlockInfoCreate commentBlockInfoCreate = CommentBlockInfoCreate.builder()
                .reason("block reason")
                .build();
        IricomException exception = Assertions.assertThrows(IricomException.class, () -> {
            this.blockService.blockComment(systemAdmin.getId(), board.getId(), post.getId(), comment.getId(), commentBlockInfoCreate);
        });

        Assertions.assertEquals("04000010", exception.getErrorCode());
    }

    @Test
    @DisplayName("이미 차단된 댓글")
    void alreadyBlockComment() {
        // 계정 생성
        TestAccountInfo account = this.setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = this.setRandomBoard();
        // 게시물 생성
        TestPostInfo post = TestPostInfo.builder()
                .title("post title").content("content").isAllowComment(true)
                .postType(PostType.POST).postState(PostState.PUBLISH)
                .creator(account).board(board)
                .build();
        this.setPost(post);
        // 댓글 생성
        TestCommentInfo comment = TestCommentInfo.builder()
                .content("comment").creator(account).post(post)
                .build();
        this.setComment(comment);

        CommentBlockInfoCreate commentBlockInfoCreate = CommentBlockInfoCreate.builder()
                .reason("block reason")
                .build();
        // 첫번째 차단 시도
        Assertions.assertDoesNotThrow(() -> {
            this.blockService.blockComment(systemAdmin.getId(), board.getId(), post.getId(), comment.getId(), commentBlockInfoCreate);
        });
        // 두번째 차단 시도
        IricomException exception = Assertions.assertThrows(IricomException.class, () -> {
            this.blockService.blockComment(systemAdmin.getId(), board.getId(), post.getId(), comment.getId(), commentBlockInfoCreate);
        });

        Assertions.assertEquals("05000008", exception.getErrorCode());
    }

    @Test
    @DisplayName("차단된 댓글 조회")
    void getBlockComment() {
        // 계정 생성
        TestAccountInfo account = this.setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = this.setRandomBoard();
        // 게시물 생성
        TestPostInfo post = TestPostInfo.builder()
                .title("post title").content("content").isAllowComment(true)
                .postType(PostType.POST).postState(PostState.PUBLISH)
                .creator(account).board(board)
                .build();
        this.setPost(post);
        // 댓글 생성
        TestCommentInfo comment = TestCommentInfo.builder()
                .content("comment").creator(account).post(post)
                .build();
        this.setComment(comment);

        // 댓글 차단
        CommentBlockInfoCreate commentBlockInfoCreate = CommentBlockInfoCreate.builder()
                .reason("block reason")
                .build();
        this.blockService.blockComment(systemAdmin.getId(), board.getId(), post.getId(), comment.getId(), commentBlockInfoCreate);

        // 차단된 댓글 조회
        CommentInfo commentInfo = this.commentService.getComment(board.getId(), post.getId(), comment.getId());
        Assertions.assertNotNull(commentInfo);
        Assertions.assertEquals(comment.getId(), commentInfo.getId());
        // TODO 차단된 댓글은 댓글 자체는 조회가 되나 내용이 나타나지 않아야 함
    }
}
