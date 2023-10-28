package com.illdangag.iricom.server.service.implement;

import com.illdangag.iricom.server.data.entity.Board;
import com.illdangag.iricom.server.data.entity.Comment;
import com.illdangag.iricom.server.data.entity.Post;
import com.illdangag.iricom.server.exception.IricomErrorCode;
import com.illdangag.iricom.server.exception.IricomException;
import com.illdangag.iricom.server.repository.BoardRepository;
import com.illdangag.iricom.server.repository.CommentRepository;
import com.illdangag.iricom.server.repository.PostRepository;

import java.util.Optional;

public abstract class IricomService {
    final protected BoardRepository boardRepository;
    final protected PostRepository postRepository;
    final protected CommentRepository commentRepository;

    public IricomService(BoardRepository boardRepository, PostRepository postRepository, CommentRepository commentRepository) {
        this.boardRepository = boardRepository;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
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
}
