package com.illdangag.iricom.server.service.implement;

import com.illdangag.iricom.server.data.entity.*;
import com.illdangag.iricom.server.data.entity.type.AccountAuth;
import com.illdangag.iricom.server.exception.IricomErrorCode;
import com.illdangag.iricom.server.exception.IricomException;
import com.illdangag.iricom.server.repository.BoardAdminRepository;
import com.illdangag.iricom.server.repository.BoardRepository;
import com.illdangag.iricom.server.repository.CommentRepository;
import com.illdangag.iricom.server.repository.PostRepository;

import java.util.List;
import java.util.Optional;

public abstract class IricomService {
    protected final BoardRepository boardRepository;
    protected final PostRepository postRepository;
    protected final CommentRepository commentRepository;
    protected final BoardAdminRepository boardAdminRepository;

    public IricomService(BoardRepository boardRepository, PostRepository postRepository, CommentRepository commentRepository,
                         BoardAdminRepository boardAdminRepository) {
        this.boardRepository = boardRepository;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.boardAdminRepository = boardAdminRepository;
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

            Optional<BoardAdmin> boardAdminOptional = this.boardAdminRepository.getEnableBoardAdmin(board, account);
            if (boardAdminOptional.isPresent()) {
                return;
            }

            // 계정이 접근 가능한 게시판 목록을 조회
            List<Long> accessibleBoardIdList = this.boardRepository.getAccessibleBoardIdList(account);
            if (!accessibleBoardIdList.contains(board.getId())) {
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
