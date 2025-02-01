package com.illdangag.iricom.server.service.block.comment;

import com.illdangag.iricom.server.data.entity.type.PostState;
import com.illdangag.iricom.server.data.entity.type.PostType;
import com.illdangag.iricom.server.data.request.CommentBlockInfoCreate;
import com.illdangag.iricom.server.data.response.CommentBlockInfo;
import com.illdangag.iricom.server.exception.IricomException;
import com.illdangag.iricom.server.service.BlockService;
import com.illdangag.iricom.server.test.IricomTestSuite;
import com.illdangag.iricom.server.test.data.wrapper.TestAccountInfo;
import com.illdangag.iricom.server.test.data.wrapper.TestBoardInfo;
import com.illdangag.iricom.server.test.data.wrapper.TestCommentInfo;
import com.illdangag.iricom.server.test.data.wrapper.TestPostInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javax.transaction.Transactional;
import java.util.Collections;

@DisplayName("service: 차단 - 댓글 차단 해제")
@Transactional
public class BlockServiceCommentUnblockTest extends IricomTestSuite {
    @Autowired
    private BlockService blockService;

    public BlockServiceCommentUnblockTest(ApplicationContext context) {
        super(context);
    }

    @Test
    @DisplayName("시스템 관리자")
    void unblockSystemAdmin() {
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
        this.blockService.blockComment(systemAdmin.getId(), board.getId(), post.getId(), comment.getId(), commentBlockInfoCreate);

        // 댓글 차단 해제
        CommentBlockInfo commentBlockInfo = this.blockService.unblockComment(systemAdmin.getId(), board.getId(), post.getId(), comment.getId());
        Assertions.assertNotNull(commentBlockInfo);
    }

    @Test
    @DisplayName("게시판 관리자")
    void unblockBoardAdmin() {
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
        this.blockService.blockComment(boardAdmin.getId(), board.getId(), post.getId(), comment.getId(), commentBlockInfoCreate);

        // 댓글 차단 해제
        CommentBlockInfo commentBlockInfo = this.blockService.unblockComment(boardAdmin.getId(), board.getId(), post.getId(), comment.getId());
        Assertions.assertNotNull(commentBlockInfo);
    }

    @Test
    @DisplayName("다른 게시판 관리자")
    void unblockOtherBoardAdmin() {
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

        // 댓글 차단
        CommentBlockInfoCreate commentBlockInfoCreate = CommentBlockInfoCreate.builder()
                .reason("block reason")
                .build();
        this.blockService.blockComment(boardAdmin00.getId(), board00.getId(), post.getId(), comment.getId(), commentBlockInfoCreate);

        IricomException exception = Assertions.assertThrows(IricomException.class, () -> {
            this.blockService.blockComment(boardAdmin01.getId(), board00.getId(), post.getId(), comment.getId(), commentBlockInfoCreate);
        });

        Assertions.assertEquals("05000009", exception.getErrorCode());
    }

    @Test
    @DisplayName("일반 사용자")
    void unblockAccount() {
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
        this.blockService.blockComment(boardAdmin.getId(), board.getId(), post.getId(), comment.getId(), commentBlockInfoCreate);

        // 댓글 차단 해제
        IricomException exception = Assertions.assertThrows(IricomException.class, () -> {
            this.blockService.unblockComment(account.getId(), board.getId(), post.getId(), comment.getId());
        });

        Assertions.assertEquals("05000009", exception.getErrorCode());
    }

    @Test
    @DisplayName("등록되지 않은 사용자")
    void unblockUnknown() {
        // 계정 생성
        TestAccountInfo boardAdmin = this.setRandomAccount();
        TestAccountInfo account = this.setRandomAccount();
        TestAccountInfo unregisteredAccount = this.setRandomUnregisteredAccount();
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
        this.blockService.blockComment(boardAdmin.getId(), board.getId(), post.getId(), comment.getId(), commentBlockInfoCreate);

        // 댓글 차단 해제
        IricomException exception = Assertions.assertThrows(IricomException.class, () -> {
            this.blockService.unblockComment(unregisteredAccount.getId(), board.getId(), post.getId(), comment.getId());
        });

        Assertions.assertEquals("05000009", exception.getErrorCode());
    }

    @Test
    @DisplayName("차단 해제 된 댓글 조회")
    void getUnblockComment() {
        // TODO
    }
}
