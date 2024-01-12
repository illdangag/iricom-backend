package com.illdangag.iricom.server.service.implement;

import com.illdangag.iricom.server.data.entity.*;
import com.illdangag.iricom.server.data.entity.type.AccountPointType;
import com.illdangag.iricom.server.data.entity.type.VoteType;
import com.illdangag.iricom.server.data.request.CommentInfoCreate;
import com.illdangag.iricom.server.data.request.CommentInfoSearch;
import com.illdangag.iricom.server.data.request.CommentInfoUpdate;
import com.illdangag.iricom.server.data.response.AccountInfo;
import com.illdangag.iricom.server.data.response.CommentInfo;
import com.illdangag.iricom.server.data.response.CommentInfoList;
import com.illdangag.iricom.server.exception.IricomErrorCode;
import com.illdangag.iricom.server.exception.IricomException;
import com.illdangag.iricom.server.repository.*;
import com.illdangag.iricom.server.service.AccountPointService;
import com.illdangag.iricom.server.service.AccountService;
import com.illdangag.iricom.server.service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Validated
@Transactional
@Service
public class CommentServiceImpl extends IricomService implements CommentService {
    private final CommentVoteRepository commentVoteRepository;
    private final ReportRepository reportRepository;
    private final BlockRepository blockRepository;
    private final AccountService accountService;
    private final AccountPointService accountPointService;

    @Autowired
    public CommentServiceImpl(AccountRepository accountRepository, BoardRepository boardRepository,
                              PostRepository postRepository, CommentRepository commentRepository,
                              CommentVoteRepository commentVoteRepository, BlockRepository blockRepository,
                              ReportRepository reportRepository, AccountService accountService,
                              AccountPointService accountPointService) {
        super(accountRepository, boardRepository, postRepository, commentRepository);
        this.commentVoteRepository = commentVoteRepository;
        this.reportRepository = reportRepository;
        this.blockRepository = blockRepository;
        this.accountService = accountService;
        this.accountPointService = accountPointService;
    }

    @Override
    public CommentInfo createCommentInfo(String accountId, String boardId, String postId, @Valid CommentInfoCreate commentInfoCreate) {
        Account account = this.getAccount(accountId);
        return this.createCommentInfo(account, boardId, postId, commentInfoCreate);
    }

    /**
     * 댓글 생성
     */
    @Override
    public CommentInfo createCommentInfo(Account account, String boardId, String postId, @Valid CommentInfoCreate commentInfoCreate) {
        Board board = this.getBoard(boardId);
        Post post = this.getPost(postId);

        return this.createCommentInfo(account, board, post, commentInfoCreate);
    }

    /**
     * 댓글 생성
     */
    @Override
    public CommentInfo createCommentInfo(Account account, Board board, Post post, @Valid CommentInfoCreate commentInfoCreate) {
        this.validate(account, board, post);

        if (!board.getEnabled()) { // 비활성화 게시판인 경우
            throw new IricomException(IricomErrorCode.DISABLED_BOARD);
        }

        if (!post.isPublish()) { // 발행되지 않은 게시물인 경우
            throw new IricomException(IricomErrorCode.NOT_EXIST_POST);
        }

        if (this.isBlockPost(post)) { // 차단된 게시물인 경우
            throw new IricomException(IricomErrorCode.BLOCKED_POST);
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

        this.accountPointService.addAccountPoint(account, AccountPointType.CREATE_COMMENT);

        return new CommentInfo(comment, accountInfo, 0, 0, 0);
    }

    @Override
    public CommentInfo updateComment(String accountId, String boardId, String postId, String commentId, @Valid CommentInfoUpdate commentInfoUpdate) {
        Account account = this.getAccount(accountId);
        return this.updateComment(account, boardId, postId, commentId, commentInfoUpdate);
    }

    /**
     * 댓글 수정
     */
    @Override
    public CommentInfo updateComment(Account account, String boardId, String postId, String commentId, @Valid CommentInfoUpdate commentInfoUpdate) {
        Board board = this.getBoard(boardId);
        Post post = this.getPost(postId);
        Comment comment = this.getComment(commentId);
        return this.updateComment(account, board, post, comment, commentInfoUpdate);
    }

    /**
     * 댓글 수정
     */
    @Override
    public CommentInfo updateComment(Account account, Board board, Post post, Comment comment, @Valid CommentInfoUpdate commentInfoUpdate) {
        this.validate(account, board, post, comment);

        if (!board.getEnabled()) { // 비활성화 게시판인 경우
            throw new IricomException(IricomErrorCode.DISABLED_BOARD);
        }

        if (!post.isPublish()) { // 발행되지 않은 게시물인 경우
            throw new IricomException(IricomErrorCode.NOT_EXIST_POST);
        }

        if (this.isBlockPost(post)) { // 차단된 게시물인 경우
            throw new IricomException(IricomErrorCode.BLOCKED_POST);
        }

        if (!post.getContent().getAllowComment()) { // 댓글을 허용하지 않은 게시물인 경우
            throw new IricomException(IricomErrorCode.NOT_ALLOW_COMMENT);
        }

        if (!comment.getAccount().equals(account)) { // 다른 계정의 댓글을 수정하는 경우
            throw new IricomException(IricomErrorCode.INVALID_AUTHORIZATION_TO_UPDATE_COMMENT);
        }

        comment.setContent(commentInfoUpdate.getContent());
        comment.setUpdateDate(LocalDateTime.now());
        this.commentRepository.save(comment);

        AccountInfo accountInfo = this.accountService.getAccountInfo(comment.getAccount());
        long upvote = this.commentVoteRepository.getCommentVoteCount(comment, VoteType.UPVOTE);
        long downvote = this.commentVoteRepository.getCommentVoteCount(comment, VoteType.DOWNVOTE);
        long reportCount = this.reportRepository.getCommentReportCount(comment);

        return new CommentInfo(comment, accountInfo, upvote, downvote, reportCount);
    }

    /**
     * 댓글 목록 조회
     */
    @Override
    public CommentInfoList getComment(String boardId, String postId, @Valid CommentInfoSearch commentInfoSearch) {
        Board board = this.getBoard(boardId);
        Post post = this.getPost(postId);

        return this.getComment(board, post, commentInfoSearch);
    }

    /**
     * 댓글 목록 조회
     */
    @Override
    public CommentInfoList getComment(Account account, String boardId, String postId, CommentInfoSearch commentInfoSearch) {
        Board board = this.getBoard(boardId);
        Post post = this.getPost(postId);

        return this.getComment(account, board, post, commentInfoSearch);
    }

    /**
     * 댓글 목록 조회
     */
    @Override
    public CommentInfoList getComment(Board board, Post post, @Valid CommentInfoSearch commentInfoSearch) {
        return this.getComment(null, board, post, commentInfoSearch);
    }

    /**
     * 댓글 목록 조회
     */
    @Override
    public CommentInfoList getComment(Account account, Board board, Post post, CommentInfoSearch commentInfoSearch) {
        this.validate(account, board, post);

        if (!board.getEnabled()) { // 비활성화 게시판인 경우
            throw new IricomException(IricomErrorCode.DISABLED_BOARD);
        }

        if (!post.isPublish()) { // 발행되지 않은 게시물인 경우
            throw new IricomException(IricomErrorCode.NOT_EXIST_POST);
        }

        if (this.isBlockPost(post)) { // 차단된 게시물인 경우
            throw new IricomException(IricomErrorCode.BLOCKED_POST);
        }

        long total;
        List<Comment> commentList;
        if (commentInfoSearch.getReferenceCommentId() == null || commentInfoSearch.getReferenceCommentId().isEmpty()) { // 대댓글 조회가 아닌 경우
            commentList = this.commentRepository.getCommentList(post, commentInfoSearch.getSkip(), commentInfoSearch.getLimit());
            total = this.commentRepository.getCommentCount(post);
        } else { // 대댓글 조회인 경우
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

    /**
     * 댓글 조회
     */
    @Override
    public CommentInfo getComment(String boardId, String postId, String commentId) {
        Board board = this.getBoard(boardId);
        Post post = this.getPost(postId);
        Comment comment = this.getComment(commentId);

        return this.getComment(null, board, post, comment);
    }

    /**
     * 댓글 조회
     */
    @Override
    public CommentInfo getComment(Account account, String boardId, String postId, String commentId) {
        Board board = this.getBoard(boardId);
        Post post = this.getPost(postId);
        Comment comment = this.getComment(commentId);

        return this.getComment(account, board, post, comment);
    }

    /**
     * 댓글 조회
     */
    @Override
    public CommentInfo getComment(Board board, Post post, Comment comment) {
        return this.getComment(null, board, post, comment);
    }

    /**
     * 댓글 조회
     */
    @Override
    public CommentInfo getComment(Account account, Board board, Post post, Comment comment) {
        this.validate(account, board, post, comment);

        if (!board.getEnabled()) { // 비활성화 게시판인 경우
            throw new IricomException(IricomErrorCode.DISABLED_BOARD);
        }

        if (!post.isPublish()) { // 발행되지 않은 게시물인 경우
            throw new IricomException(IricomErrorCode.NOT_EXIST_POST);
        }

        if (this.isBlockPost(post)) { // 차단된 게시물인 경우
            throw new IricomException(IricomErrorCode.BLOCKED_POST);
        }

        return this.getCommentInfo(comment);
    }

    private CommentInfo getCommentInfo(Comment comment) {
        AccountInfo accountInfo = this.accountService.getAccountInfo(comment.getAccount());
        long upvote = this.commentVoteRepository.getCommentVoteCount(comment, VoteType.UPVOTE);
        long downvote = this.commentVoteRepository.getCommentVoteCount(comment, VoteType.DOWNVOTE);
        long reportCount = this.reportRepository.getCommentReportCount(comment);
        return new CommentInfo(comment, accountInfo, upvote, downvote, reportCount);
    }

    @Override
    public CommentInfo deleteComment(String accountId, String boardId, String postId, String commentId) {
        Account account = this.getAccount(accountId);
        return this.deleteComment(account, boardId, postId, commentId);
    }

    /**
     * 댓글 삭제
     */
    @Override
    public CommentInfo deleteComment(Account account, String boardId, String postId, String commentId) {
        Board board = this.getBoard(boardId);
        Post post = this.getPost(postId);
        Comment comment = this.getComment(commentId);

        return this.deleteComment(account, board, post, comment);
    }

    /**
     * 댓글 삭제
     */
    @Override
    public CommentInfo deleteComment(Account account, Board board, Post post, Comment comment) {
        this.validate(account, board, post, comment);

        if (!board.getEnabled()) { // 비활성화 게시판인 경우
            throw new IricomException(IricomErrorCode.DISABLED_BOARD);
        }

        if (!post.isPublish()) { // 발행되지 않은 게시물인 경우
            throw new IricomException(IricomErrorCode.NOT_EXIST_POST);
        }

        if (this.isBlockPost(post)) { // 차단된 게시물인 경우
            throw new IricomException(IricomErrorCode.BLOCKED_POST);
        }

        if (!comment.getAccount().equals(account)) {
            // 다른 계정의 댓글을 삭제 하는 경우
            throw new IricomException(IricomErrorCode.INVALID_AUTHORIZATION_TO_DELETE_COMMENT);
        }

        comment.setDeleted(true);
        this.commentRepository.save(comment);
        AccountInfo accountInfo = this.accountService.getAccountInfo(comment.getAccount());
        long upvote = this.commentVoteRepository.getCommentVoteCount(comment, VoteType.UPVOTE);
        long downvote = this.commentVoteRepository.getCommentVoteCount(comment, VoteType.DOWNVOTE);
        long reportCount = this.reportRepository.getCommentReportCount(comment);
        return new CommentInfo(comment, accountInfo, upvote, downvote, reportCount);
    }

    @Override
    public CommentInfo voteComment(String accountId, String boardId, String postId, String commentId, VoteType voteType) {
        Account account = this.getAccount(accountId);
        return this.voteComment(account, boardId, postId, commentId, voteType);
    }

    /**
     * 댓글 좋아요 싫어요
     */
    @Override
    public CommentInfo voteComment(Account account, String boardId, String postId, String commentId, VoteType voteType) {
        Board board = this.getBoard(boardId);
        Post post = this.getPost(postId);
        Comment comment = this.getComment(commentId);

        return this.voteComment(account, board, post, comment, voteType);
    }

    /**
     * 댓글 좋아요 싫어요
     */
    @Override
    public CommentInfo voteComment(Account account, Board board, Post post, Comment comment, VoteType voteType) {
        this.validate(account, board, post, comment);

        if (!board.getEnabled()) { // 비활성화 게시판인 경우
            throw new IricomException(IricomErrorCode.DISABLED_BOARD);
        }

        if (!post.isPublish()) { // 발행되지 않은 게시물인 경우
            throw new IricomException(IricomErrorCode.NOT_EXIST_POST);
        }

        if (this.isBlockPost(post)) { // 차단된 게시물인 경우
            throw new IricomException(IricomErrorCode.BLOCKED_POST);
        }

        if (!post.getContent().getAllowComment()) {
            throw new IricomException(IricomErrorCode.NOT_ALLOW_COMMENT);
        }

        if (comment.getDeleted()) {
            throw new IricomException(IricomErrorCode.NOT_EXIST_COMMENT);
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

        AccountInfo accountInfo = this.accountService.getAccountInfo(comment.getAccount());
        long upvote = this.commentVoteRepository.getCommentVoteCount(comment, VoteType.UPVOTE);
        long downvote = this.commentVoteRepository.getCommentVoteCount(comment, VoteType.DOWNVOTE);
        long reportCount = this.reportRepository.getCommentReportCount(comment);
        return new CommentInfo(comment, accountInfo, upvote, downvote, reportCount);
    }

    private boolean isBlockPost(Post post) {
        List<PostBlock> postBlockList = post.getPostBlockList();
        return !postBlockList.isEmpty();
    }
}
