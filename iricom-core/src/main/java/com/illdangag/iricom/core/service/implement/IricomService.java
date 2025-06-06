package com.illdangag.iricom.core.service.implement;

import com.illdangag.iricom.core.data.entity.*;
import com.illdangag.iricom.core.data.entity.type.AccountAuth;
import com.illdangag.iricom.core.exception.IricomErrorCode;
import com.illdangag.iricom.core.exception.IricomException;
import com.illdangag.iricom.core.repository.AccountRepository;
import com.illdangag.iricom.core.repository.BoardRepository;
import com.illdangag.iricom.core.repository.CommentRepository;
import com.illdangag.iricom.core.repository.PostRepository;

import java.util.List;
import java.util.Optional;

public abstract class IricomService {
    protected final AccountRepository accountRepository;
    protected final BoardRepository boardRepository;
    protected final PostRepository postRepository;
    protected final CommentRepository commentRepository;

    public IricomService(AccountRepository accountRepository, BoardRepository boardRepository,
                         PostRepository postRepository, CommentRepository commentRepository) {
        this.accountRepository = accountRepository;
        this.boardRepository = boardRepository;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
    }

    protected Account getAccount(String id) {
        long accountId = -1;
        try {
            accountId = Long.parseLong(id);
        } catch (Exception exception) {
            throw new IricomException(IricomErrorCode.NOT_EXIST_ACCOUNT);
        }
        Optional<Account> accountOptional = this.accountRepository.getAccount(accountId);
        return accountOptional.orElseThrow(() -> new IricomException(IricomErrorCode.NOT_EXIST_ACCOUNT));
    }

    protected Board getBoard(String id) {
        long boardId = -1;
        try {
            boardId = Long.parseLong(id);
        } catch (Exception exception) {
            throw new IricomException(IricomErrorCode.NOT_EXIST_BOARD);
        }
        Optional<Board> boardOptional = this.boardRepository.getBoard(boardId);
        return boardOptional.orElseThrow(() -> new IricomException(IricomErrorCode.NOT_EXIST_BOARD));
    }

    protected Post getPost(String id) {
        long postId = -1;
        try {
            postId = Long.parseLong(id);
        } catch (Exception exception) {
            throw new IricomException(IricomErrorCode.NOT_EXIST_POST);
        }

        Optional<Post> postOptional = this.postRepository.getPost(postId);
        return postOptional.orElseThrow(() -> new IricomException(IricomErrorCode.NOT_EXIST_POST));
    }

    protected Comment getComment(String id) {
        long commentId = -1;
        try {
            commentId = Long.parseLong(id);
        } catch (Exception exception) {
            throw new IricomException(IricomErrorCode.NOT_EXIST_COMMENT);
        }

        Optional<Comment> commentOptional = this.commentRepository.getComment(commentId);
        return commentOptional.orElseThrow(() -> new IricomException(IricomErrorCode.NOT_EXIST_COMMENT));
    }

    /**
     * 계정이 게시판에 대한 접근 권한 여부 확인
     */
    protected void validate(Account account, Board board) {
        if (board.getUndisclosed()) { // 게시판이 비공개 게시판인 경우
            // 계정에 대해서 권한 확인
            if (account == null) { // 계정이 올바르지 않은 경우
                throw new IricomException(IricomErrorCode.NOT_EXIST_BOARD);
            }

            if (account.getAuth() == AccountAuth.SYSTEM_ADMIN) {
                return;
            }

            List<BoardAdmin> boardAdminList = this.boardRepository.getBoardAdminList(board, account);
            if (!boardAdminList.isEmpty()) {
                return;
            }

            // 계정이 접근 가능한 게시판 목록을 조회
            List<Board> accessibleBoardList = this.boardRepository.getBoardList(account, null, null, null, null);
            if (!accessibleBoardList.contains(board)) {
                throw new IricomException(IricomErrorCode.NOT_EXIST_BOARD);
            }
        }
    }

    /**
     * 계정이 게시판에 대한 접근 권한 여부
     * 게시판에서 작성된 게시물인지 확인
     */
    protected void validate(Account account, Board board, Post post) {
        this.validate(account, board);

        if (!post.getBoard().equals(board)) { // 해당 개시판에서 발행되지 않은 게시물
            throw new IricomException(IricomErrorCode.NOT_EXIST_POST);
        }
    }

    /**
     * 계정이 게시판에 대한 접근 권한 여부
     * 게시판에서 작성된 게시물인지 확인
     * 게시물에서 작성된 댓글인지 확인
     */
    protected void validate(Account account, Board board, Post post, Comment comment) {
        this.validate(account, board, post);

        if (!comment.getPost().equals(post)) { // 해당 게시물에서 작성되지 않은 댓글
            throw new IricomException(IricomErrorCode.NOT_EXIST_COMMENT);
        }
    }
}
