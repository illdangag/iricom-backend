package com.illdangag.iricom.server.service.implement;

import com.illdangag.iricom.server.data.entity.*;
import com.illdangag.iricom.server.data.request.CommentInfoCreate;
import com.illdangag.iricom.server.data.request.CommentInfoSearch;
import com.illdangag.iricom.server.data.request.CommentInfoUpdate;
import com.illdangag.iricom.server.data.response.AccountInfo;
import com.illdangag.iricom.server.data.response.CommentInfo;
import com.illdangag.iricom.server.data.response.CommentInfoList;
import com.illdangag.iricom.server.exception.IricomErrorCode;
import com.illdangag.iricom.server.exception.IricomException;
import com.illdangag.iricom.server.repository.CommentRepository;
import com.illdangag.iricom.server.repository.CommentVoteRepository;
import com.illdangag.iricom.server.service.AccountService;
import com.illdangag.iricom.server.service.BoardService;
import com.illdangag.iricom.server.service.CommentService;
import com.illdangag.iricom.server.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Validated
@Service
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final CommentVoteRepository commentVoteRepository;
    private final BoardService boardService;
    private final PostService postService;
    private final AccountService accountService;

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository, CommentVoteRepository commentVoteRepository,
                              BoardService boardService, PostService postService, AccountService accountService) {
        this.commentRepository = commentRepository;
        this.commentVoteRepository = commentVoteRepository;
        this.boardService = boardService;
        this.postService = postService;
        this.accountService = accountService;
    }

    @Override
    public Comment getComment(String id) {
        try {
            return this.getComment(Long.parseLong(id));
        } catch (Exception exception) {
            throw new IricomException(IricomErrorCode.NOT_EXIST_COMMENT);
        }
    }

    @Override
    public Comment getComment(long id) {
        Optional<Comment> commentOptional = this.commentRepository.getComment(id);
        return commentOptional.orElseThrow(() -> new IricomException(IricomErrorCode.NOT_EXIST_COMMENT));
    }

    @Override
    public CommentInfo createCommentInfo(Account account, String boardId, String postId, @Valid CommentInfoCreate commentInfoCreate) {
        Board board = this.boardService.getBoard(boardId);
        Post post = this.postService.getPost(postId);
        return this.createCommentInfo(account, board, post, commentInfoCreate);
    }

    @Override
    public CommentInfo createCommentInfo(Account account, Board board, Post post, @Valid CommentInfoCreate commentInfoCreate) {
        this.validate(board, post);

        if (!board.getEnabled()) { // 비활성화 게시판인 경우
            throw new IricomException(IricomErrorCode.DISABLED_BOARD);
        }

        if (!board.equals(post.getBoard()) || post.getContent() == null) { // 게시판에 게시물이 존재하지 않거나 발행되지 않은 게시물인 경우
            throw new IricomException(IricomErrorCode.NOT_EXIST_POST);
        }

        if (!post.getContent().getAllowComment()) { // 댓글을 허용하지 않은 게시물인 경우
            throw new IricomException(IricomErrorCode.NOT_ALLOW_COMMENT);
        }

        Comment referenceComment = null;
        if (commentInfoCreate.getReferenceCommentId() != null) {
            try {
                referenceComment = this.getComment(commentInfoCreate.getReferenceCommentId());
            } catch (Exception exception) {
                throw new IricomException(IricomErrorCode.NOT_EXIST_REFERENCE_COMMENT);
            }
            referenceComment.setHasNestedComment(true);
        }

        Comment comment = Comment.builder()
                .account(account)
                .post(post)
                .referenceComment(referenceComment)
                .content(commentInfoCreate.getContent())
                .build();

        AccountInfo accountInfo = this.accountService.getAccountInfo(comment.getAccount());

        this.commentRepository.save(comment);
        if (referenceComment != null) {
            this.commentRepository.save(referenceComment);
        }
        return new CommentInfo(comment, accountInfo, 0, 0);
    }

    @Override
    public CommentInfo updateComment(Account account, String boardId, String postId, String commentId, @Valid CommentInfoUpdate commentInfoUpdate) {
        Board board = this.boardService.getBoard(boardId);
        Post post = this.postService.getPost(postId);
        Comment comment = this.getComment(commentId);
        return this.updateComment(account, board, post, comment, commentInfoUpdate);
    }

    @Override
    public CommentInfo updateComment(Account account, Board board, Post post, Comment comment, @Valid CommentInfoUpdate commentInfoUpdate) {
        this.validate(board, post, comment);

        if (!board.getEnabled()) { // 비활성화 게시판인 경우
            throw new IricomException(IricomErrorCode.DISABLED_BOARD);
        }

        if (!board.equals(post.getBoard()) || post.getContent() == null) { // 게시판에 게시물이 존재하지 않거나 발행되지 않은 게시물인 경우
            throw new IricomException(IricomErrorCode.NOT_EXIST_POST);
        }

        if (!post.getContent().getAllowComment()) { // 댓글을 허용하지 않은 게시물인 경우
            throw new IricomException(IricomErrorCode.NOT_ALLOW_COMMENT);
        }

        if (!comment.getAccount().equals(account)) {
            // 다른 계정의 댓글을 수정하는 경우
            throw new IricomException(IricomErrorCode.INVALID_AUTHORIZATION_TO_UPDATE_COMMENT);
        }

        comment.setContent(commentInfoUpdate.getContent());
        comment.setUpdateDate(LocalDateTime.now());
        this.commentRepository.save(comment);
        AccountInfo accountInfo = this.accountService.getAccountInfo(comment.getAccount());
        long upvote = this.commentVoteRepository.getCommentVoteCount(comment, VoteType.UPVOTE);
        long downvote = this.commentVoteRepository.getCommentVoteCount(comment, VoteType.DOWNVOTE);
        return new CommentInfo(comment, accountInfo, upvote, downvote);
    }

    @Override
    public CommentInfoList getComment(String boardId, String postId, @Valid CommentInfoSearch commentInfoSearch) {
        Board board = this.boardService.getBoard(boardId);
        Post post = this.postService.getPost(postId);
        return this.getComment(board, post, commentInfoSearch);
    }

    @Override
    public CommentInfoList getComment(Board board, Post post, @Valid CommentInfoSearch commentInfoSearch) {
        this.validate(board, post);

        long total;
        List<Comment> commentList;
        if (commentInfoSearch.getReferenceCommentId() == null || commentInfoSearch.getReferenceCommentId().isEmpty()) {
            commentList = this.commentRepository.getCommentList(post, commentInfoSearch.getSkip(), commentInfoSearch.getLimit());
            total = this.commentRepository.getCommentCount(post);
        } else {
            Comment referenceComment = this.getComment(commentInfoSearch.getReferenceCommentId());
            commentList = this.commentRepository.getCommentList(post, referenceComment, commentInfoSearch.getSkip(), commentInfoSearch.getLimit());
            total = this.commentRepository.getCommentListSize(post, referenceComment);
        }

        List<CommentInfo> commentInfoList = commentList.stream()
                .map(this::getCommentInfo)
                .collect(Collectors.toList());

        if (commentInfoSearch.isIncludeComment()) {
            for (CommentInfo commentInfo : commentInfoList) {
                if (commentInfo.getHasNestedComment()) {
                    CommentInfoSearch nestedCommentInfoSearch = CommentInfoSearch.builder()
                            .skip(0)
                            .limit(commentInfoSearch.getIncludeCommentLimit())
                            .referenceCommentId(commentInfo.getId())
                            .build();
                    CommentInfoList nestedCommentInfoList = this.getComment(board, post, nestedCommentInfoSearch);
                    commentInfo.setNestedCommentList(nestedCommentInfoList.getCommentInfoList());
                }
            }
        }
        return CommentInfoList.builder()
                .total(total)
                .skip(commentInfoSearch.getSkip())
                .limit(commentInfoSearch.getLimit())
                .commentInfoList(commentInfoList)
                .build();
    }

    @Override
    public CommentInfo getComment(String boardId, String postId, String commentId) {
        Board board = this.boardService.getBoard(boardId);
        Post post = this.postService.getPost(postId);
        Comment comment = this.getComment(commentId);
        return this.getComment(board, post, comment);
    }

    @Override
    public CommentInfo getComment(Board board, Post post, Comment comment) {
        this.validate(board, post, comment);
        return this.getCommentInfo(comment);
    }

    private CommentInfo getCommentInfo(Comment comment) {
        AccountInfo accountInfo = this.accountService.getAccountInfo(comment.getAccount());
        long upvote = this.commentVoteRepository.getCommentVoteCount(comment, VoteType.UPVOTE);
        long downvote = this.commentVoteRepository.getCommentVoteCount(comment, VoteType.DOWNVOTE);
        return new CommentInfo(comment, accountInfo, upvote, downvote);
    }


    @Override
    public CommentInfo deleteComment(Account account, String boardId, String postId, String commentId) {
        Board board = this.boardService.getBoard(boardId);
        Post post = this.postService.getPost(postId);
        Comment comment = this.getComment(commentId);
        return this.deleteComment(account, board, post, comment);
    }

    @Override
    public CommentInfo deleteComment(Account account, Board board, Post post, Comment comment) {
        this.validate(board, post, comment);

        if (!comment.getAccount().equals(account)) {
            // 다른 계정의 댓글을 삭제 하는 경우
            throw new IricomException(IricomErrorCode.INVALID_AUTHORIZATION_TO_DELETE_COMMENT);
        }

        comment.setDeleted(true);
        this.commentRepository.save(comment);
        AccountInfo accountInfo = this.accountService.getAccountInfo(comment.getAccount());
        long upvote = this.commentVoteRepository.getCommentVoteCount(comment, VoteType.UPVOTE);
        long downvote = this.commentVoteRepository.getCommentVoteCount(comment, VoteType.DOWNVOTE);
        return new CommentInfo(comment, accountInfo, upvote, downvote);
    }

    @Override
    public CommentInfo voteComment(Account account, String boardId, String postId, String commentId, VoteType voteType) {
        Board board = this.boardService.getBoard(boardId);
        Post post = this.postService.getPost(postId);
        Comment comment = this.getComment(commentId);
        return this.voteComment(account, board, post, comment, voteType);
    }

    @Override
    public CommentInfo voteComment(Account account, Board board, Post post, Comment comment, VoteType voteType) {
        this.validate(board, post, comment);

        if (!board.getEnabled()) {
            throw new IricomException(IricomErrorCode.DISABLED_BOARD);
        }

        if (!post.getContent().getAllowComment()) {
            throw new IricomException(IricomErrorCode.NOT_ALLOW_COMMENT);
        }

        Optional<CommentVote> commentVoteOptional = this.commentVoteRepository.getCommentVote(account, comment, voteType);
        if (commentVoteOptional.isPresent()) {
            throw new IricomException(IricomErrorCode.ALREADY_VOTE_COMMENT);
        }

        CommentVote commentVote = CommentVote.builder()
                .comment(comment)
                .account(account)
                .type(voteType)
                .build();

        this.commentRepository.save(comment);
        this.commentVoteRepository.save(commentVote);

        AccountInfo accountInfo = this.accountService.getAccountInfo(account);
        long upvote = this.commentVoteRepository.getCommentVoteCount(comment, VoteType.UPVOTE);
        long downvote = this.commentVoteRepository.getCommentVoteCount(comment, VoteType.DOWNVOTE);
        return new CommentInfo(comment, accountInfo, upvote, downvote);
    }

    private void validate(Board board, Post post) {
        if (!post.getBoard().equals(board)) { // 해당 개시판에서 발행되지 않은 게시물
            throw new IricomException(IricomErrorCode.NOT_EXIST_POST);
        }

        if (!post.isPublish()) { // 발행되지 않은 게시물
            throw new IricomException(IricomErrorCode.NOT_EXIST_PUBLISH_CONTENT);
        }
    }

    private void validate(Board board, Post post, Comment comment) {
        this.validate(board, post);

        if (!comment.getPost().equals(post)) { // 해당 게시물에 작성된 댓글이 아닌 경우
            throw new IricomException(IricomErrorCode.NOT_EXIST_COMMENT);
        }
    }
}
