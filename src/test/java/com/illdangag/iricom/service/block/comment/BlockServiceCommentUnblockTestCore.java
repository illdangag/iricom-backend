package com.illdangag.iricom.service.block.comment;

import com.illdangag.iricom.core.data.request.CommentBlockInfoCreate;
import com.illdangag.iricom.core.data.response.CommentBlockInfo;
import com.illdangag.iricom.core.exception.IricomException;
import com.illdangag.iricom.core.service.BlockService;
import com.illdangag.iricom.core.test.data.wrapper.TestAccountInfo;
import com.illdangag.iricom.core.test.data.wrapper.TestBoardInfo;
import com.illdangag.iricom.core.test.data.wrapper.TestCommentInfo;
import com.illdangag.iricom.core.test.data.wrapper.TestPostInfo;
import com.illdangag.iricom.IricomTestServiceSuite;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javax.transaction.Transactional;
import java.util.Collections;

@DisplayName("service: 차단 - 댓글 차단 해제")
@Transactional
public class BlockServiceCommentUnblockTestCore extends IricomTestServiceSuite {
    @Autowired
    private BlockService blockService;

    public BlockServiceCommentUnblockTestCore(ApplicationContext context) {
        super(context);
    }

    @Test
    @DisplayName("시스템 관리자")
    void unblockSystemAdmin() {
        // 계정 생성
        TestAccountInfo account = setRandomAccount(1).get(0);
        // 게시판 생성
        TestBoardInfo board = setRandomBoard(1).get(0);
        // 게시물 생성
        TestPostInfo post = setRandomPost(board, account);
        // 댓글 생성
        TestCommentInfo comment = setRandomComment(post, account);

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
        TestAccountInfo boardAdmin = setRandomAccount();
        TestAccountInfo account = setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = setRandomBoard(Collections.singletonList(boardAdmin));
        // 게시물 생성
        TestPostInfo post = setRandomPost(board, account);
        // 댓글 생성
        TestCommentInfo comment = setRandomComment(post, account);

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
        TestAccountInfo boardAdmin00 = setRandomAccount();
        TestAccountInfo boardAdmin01 = setRandomAccount();
        TestAccountInfo account = setRandomAccount();
        // 게시판 생성
        TestBoardInfo board00 = setRandomBoard(Collections.singletonList(boardAdmin00));
        setRandomBoard(Collections.singletonList(boardAdmin01));
        // 게시물 생성
        TestPostInfo post = setRandomPost(board00, account);
        // 댓글 생성
        TestCommentInfo comment = setRandomComment(post, account);

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
        TestAccountInfo boardAdmin = setRandomAccount();
        TestAccountInfo account = setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = setRandomBoard(Collections.singletonList(boardAdmin));
        // 게시물 생성
        TestPostInfo post = setRandomPost(board, account);
        // 댓글 생성
        TestCommentInfo comment = setRandomComment(post, account);

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
        TestAccountInfo boardAdmin = setRandomAccount();
        TestAccountInfo account = setRandomAccount();
        TestAccountInfo unregisteredAccount = setRandomAccount(true);
        // 게시판 생성
        TestBoardInfo board = setRandomBoard(Collections.singletonList(boardAdmin));
        // 게시물 생성
        TestPostInfo post = setRandomPost(board, account);
        // 댓글 생성
        TestCommentInfo comment = setRandomComment(post, account);

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
