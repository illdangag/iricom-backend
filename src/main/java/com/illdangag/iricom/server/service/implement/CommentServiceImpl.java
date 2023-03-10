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
        return new CommentInfo(comment, accountInfo);
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

        if (!comment.getAccount().equals(account)) {
            // ?????? ????????? ????????? ???????????? ??????
            throw new IricomException(IricomErrorCode.INVALID_AUTHORIZATION_TO_UPDATE_COMMENT);
        }

        comment.setContent(commentInfoUpdate.getContent());
        comment.setUpdateDate(LocalDateTime.now());
        this.commentRepository.save(comment);
        AccountInfo accountInfo = this.accountService.getAccountInfo(comment.getAccount());
        return new CommentInfo(comment, accountInfo);
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
                .map(comment -> {
                    AccountInfo accountInfo = this.accountService.getAccountInfo(comment.getAccount());
                    return new CommentInfo(comment, accountInfo);
                }).collect(Collectors.toList());

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
            // ?????? ????????? ????????? ?????? ?????? ??????
            throw new IricomException(IricomErrorCode.INVALID_AUTHORIZATION_TO_DELETE_COMMENT);
        }

        comment.setDeleted(true);
        this.commentRepository.save(comment);
        AccountInfo accountInfo = this.accountService.getAccountInfo(comment.getAccount());
        return new CommentInfo(comment, accountInfo);
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
        if (!board.getEnabled()) {
            throw new IricomException(IricomErrorCode.DISABLED_BOARD_TO_VOTE);
        }

        if (!board.equals(post.getBoard())) {
            throw new IricomException(IricomErrorCode.NOT_EXIST_BOARD);
        }

        if (!post.isPublish()) {
            throw new IricomException(IricomErrorCode.NOT_EXIST_PUBLISH_CONTENT);
        }

        if (!post.equals(comment.getPost())) {
            throw new IricomException(IricomErrorCode.NOT_EXIST_COMMENT);
        }

        if (!post.getContent().getAllowComment()) {
            throw new IricomException(IricomErrorCode.NOT_ALLOW_COMMENT);
        }

        Optional<CommentVote> commentVoteOptional = this.commentVoteRepository.getCommentVote(account, comment, voteType);
        if (commentVoteOptional.isPresent()) {
            throw new IricomException(IricomErrorCode.ALREADY_VOTE_COMMENT);
        }

        long count = this.commentVoteRepository.getCommentVoteCount(comment, voteType);
        CommentVote commentVote = CommentVote.builder()
                .comment(comment)
                .account(account)
                .type(voteType)
                .build();

        if (voteType == VoteType.UPVOTE) {
            comment.setUpvote(count + 1);
        } else {
            comment.setDownvote(count + 1);
        }

        this.commentRepository.save(comment);
        this.commentVoteRepository.save(commentVote);

        AccountInfo accountInfo = this.accountService.getAccountInfo(account);
        return new CommentInfo(comment, accountInfo);
    }

    private void validate(Board board, Post post) {
        if (!board.getEnabled()) {
            // ???????????? ???????????? ??????
            throw new IricomException(IricomErrorCode.DISABLED_BOARD_TO_COMMENT);
        }

        if (!board.equals(post.getBoard()) || post.getContent() == null) {
            // ???????????? ???????????? ???????????? ????????? ???????????? ?????? ???????????? ??????
            throw new IricomException(IricomErrorCode.NOT_EXIST_POST);
        }

        if (!post.getContent().getAllowComment()) {
            // ????????? ???????????? ?????? ???????????? ??????
            throw new IricomException(IricomErrorCode.NOT_ALLOW_COMMENT);
        }

        if (!post.getBoard().equals(board)) {
            // ?????? ??????????????? ???????????? ?????? ?????????
            throw new IricomException(IricomErrorCode.NOT_EXIST_POST);
        }
    }

    private void validate(Board board, Post post, Comment comment) {
        this.validate(board, post);

        if (!comment.getPost().equals(post)) {
            // ?????? ???????????? ????????? ????????? ?????? ??????
            throw new IricomException(IricomErrorCode.NOT_EXIST_COMMENT);
        }
    }
}
