package com.illdangag.iricom.server.service.implement;

import com.illdangag.iricom.server.data.entity.*;
import com.illdangag.iricom.server.data.entity.type.PostState;
import com.illdangag.iricom.server.data.entity.type.ReportType;
import com.illdangag.iricom.server.data.request.CommentReportInfoCreate;
import com.illdangag.iricom.server.data.request.CommentReportInfoSearch;
import com.illdangag.iricom.server.data.request.PostReportInfoCreate;
import com.illdangag.iricom.server.data.request.PostReportInfoSearch;
import com.illdangag.iricom.server.data.response.*;
import com.illdangag.iricom.server.exception.IricomErrorCode;
import com.illdangag.iricom.server.exception.IricomException;
import com.illdangag.iricom.server.repository.*;
import com.illdangag.iricom.server.service.BoardAuthorizationService;
import com.illdangag.iricom.server.service.CommentService;
import com.illdangag.iricom.server.service.PostService;
import com.illdangag.iricom.server.service.ReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ReportServiceImpl extends IricomService implements ReportService {
    private final ReportRepository reportRepository;

    private final PostService postService;
    private final CommentService commentService;
    private final BoardAuthorizationService boardAuthorizationService;

    @Autowired
    private ReportServiceImpl(ReportRepository reportRepository, PostRepository postRepository, BoardRepository boardRepository,
                              CommentRepository commentRepository,  PostService postService, CommentService commentService,
                              BoardAuthorizationService boardAuthorizationService) {
        super(boardRepository, postRepository, commentRepository);
        this.reportRepository = reportRepository;

        this.postService = postService;
        this.commentService = commentService;
        this.boardAuthorizationService = boardAuthorizationService;
    }

    @Override
    public PostReportInfo getPostReportInfo(Account account, String boardId, String postId, String reportId) {
        Board board = this.getBoard(boardId);
        Post post = this.getPost(postId);
        return this.getPostReportInfo(account, board, post, reportId);
    }

    @Override
    public PostReportInfo getPostReportInfo(Account account, Board board, Post post, String reportId) {
        this.validate(account, board, post);

        Optional<PostReport> postReportOptional = this.reportRepository.getPostReport(reportId);
        PostReport postReport = postReportOptional.orElseThrow(() -> new IricomException(IricomErrorCode.NOT_EXIST_POST_REPORT));

        Post reportPost = postReport.getPost();
        if (!reportPost.equals(post)) {
            throw new IricomException(IricomErrorCode.NOT_EXIST_POST_REPORT);
        }

        Board reportBoard = reportPost.getBoard();
        if (!reportBoard.equals(board)) {
            throw new IricomException(IricomErrorCode.NOT_EXIST_POST_REPORT);
        }

        PostInfo postInfo = this.postService.getPostInfo(account, post, PostState.PUBLISH, true);
        return new PostReportInfo(postReport, postInfo);
    }

    @Override
    public PostReportInfoList getPostReportInfoList(Account account, String boardId, PostReportInfoSearch postReportInfoSearch) {
        Board board = this.getBoard(boardId);
        return this.getPostReportInfoList(account, board, postReportInfoSearch);
    }

    @Override
    public PostReportInfoList getPostReportInfoList(Account account, Board board, PostReportInfoSearch postReportInfoSearch) {
        this.validate(account, board);

        if (!this.boardAuthorizationService.hasAuthorization(account, board)) {
            throw new IricomException(IricomErrorCode.INVALID_AUTHORIZATION_TO_GET_POST_REPORT_LIST);
        }

        ReportType reportType = postReportInfoSearch.getType();
        String reason = postReportInfoSearch.getReason();
        int skip = postReportInfoSearch.getSkip();
        int limit = postReportInfoSearch.getLimit();

        List<PostReport> postReportList;
        long total;

        if (reportType == null) {
            postReportList = this.reportRepository.getPostReportList(board, reason, skip, limit);
            total = this.reportRepository.getPostReportListTotalCount(board, reason);
        } else {
            postReportList = this.reportRepository.getPostReportList(board, reportType, reason, skip, limit);
            total = this.reportRepository.getPostReportListTotalCount(board, reportType, reason);
        }

        List<PostReportInfo> postReportInfoList = postReportList.stream()
                .map(item -> {
                    PostInfo postInfo = this.postService.getPostInfo(account, item.getPost(), PostState.PUBLISH, false);
                    return new PostReportInfo(item, postInfo);
                })
                .collect(Collectors.toList());
        return PostReportInfoList.builder()
                .total(total)
                .skip(skip)
                .limit(limit)
                .postReportInfoList(postReportInfoList)
                .build();
    }

    @Override
    public PostReportInfoList getPostReportInfoList(Account account, String boardId, String postId, PostReportInfoSearch postReportInfoSearch) {
        Board board = this.getBoard(boardId);
        Post post = this.getPost(postId);
        return this.getPostReportInfoList(account, board, post, postReportInfoSearch);
    }

    @Override
    public PostReportInfoList getPostReportInfoList(Account account, Board board, Post post, PostReportInfoSearch postReportInfoSearch) {
        this.validate(account, board, post);

        if (!this.boardAuthorizationService.hasAuthorization(account, board)) {
            throw new IricomException(IricomErrorCode.INVALID_AUTHORIZATION_TO_GET_POST_REPORT_LIST);
        }

        ReportType reportType = postReportInfoSearch.getType();
        String reason = postReportInfoSearch.getReason();
        int skip = postReportInfoSearch.getSkip();
        int limit = postReportInfoSearch.getLimit();


        List<PostReport> postReportList;
        long total;

        if (reportType == null) {
            postReportList = this.reportRepository.getPostReportList(board, post, reason, skip, limit);
            total = this.reportRepository.getPostReportListTotalCount(board, post, reason);
        } else {
            postReportList = this.reportRepository.getPostReportList(board, post, reportType, reason, skip, limit);
            total = this.reportRepository.getPostReportListTotalCount(board, post, reportType, reason);
        }

        List<PostReportInfo> postReportInfoList = postReportList.stream()
                .map(item -> {
                    PostInfo postInfo = this.postService.getPostInfo(account, item.getPost(), PostState.PUBLISH, false);
                    return new PostReportInfo(item, postInfo);
                })
                .collect(Collectors.toList());
        return PostReportInfoList.builder()
                .total(total)
                .skip(skip)
                .limit(limit)
                .postReportInfoList(postReportInfoList)
                .build();
    }

    @Override
    public PostReportInfo reportPost(Account account, String boardId, String postId, PostReportInfoCreate postReportInfoCreate) {
        Board board = this.getBoard(boardId);
        Post post = this.getPost(postId);

        return this.reportPost(account, board, post, postReportInfoCreate);
    }

    @Override
    public PostReportInfo reportPost(Account account, Board board, Post post, PostReportInfoCreate postReportInfoCreate) {
        this.validate(account, board, post);

        if (!post.getBoard().equals(board)) {
            throw new IricomException(IricomErrorCode.NOT_EXIST_POST);
        }

        List<PostReport> postReportList = this.reportRepository.getPostReportList(account, post);
        if (!postReportList.isEmpty()) {
            // 해당 계정으로 같은 게시물에 대한 중복 신고는 허용하지 않음
            throw new IricomException(IricomErrorCode.ALREADY_REPORT_POST);
        }

        PostReport postReport = PostReport.builder()
                .account(account)
                .post(post)
                .type(postReportInfoCreate.getType())
                .reason(postReportInfoCreate.getReason())
                .build();

        this.reportRepository.savePostReport(postReport);

        PostInfo postInfo = this.postService.getPostInfo(account, post, PostState.PUBLISH, false);
        return new PostReportInfo(postReport, postInfo);
    }

    @Override
    public CommentReportInfo reportComment(Account account, String boardId, String postId, String commentId, CommentReportInfoCreate commentReportInfoCreate) {
        Board board = this.getBoard(boardId);
        Post post = this.getPost(postId);
        Comment comment = this.getComment(commentId);

        return this.reportComment(account, board, post, comment, commentReportInfoCreate);
    }

    @Override
    public CommentReportInfo reportComment(Account account, Board board, Post post, Comment comment, CommentReportInfoCreate commentReportInfoCreate) {
        this.validate(account, board, post, comment);

        if (!board.getEnabled()) {
            throw new IricomException(IricomErrorCode.DISABLED_BOARD);
        } else if (!post.isPublish()) {
            throw new IricomException(IricomErrorCode.NOT_EXIST_PUBLISH_CONTENT);
        } else if (!post.getContent().getAllowComment()) {
            throw new IricomException(IricomErrorCode.NOT_ALLOW_COMMENT);
        }

        List<CommentReport> commentReportList = this.reportRepository.getCommentReportList(account, comment);
        if (!commentReportList.isEmpty()) {
            // 해당 계정으로 같은 댓글에 대한 중복 신고는 허용하지 않음
            throw new IricomException(IricomErrorCode.ALREADY_REPORT_COMMENT);
        }

        CommentReport commentReport = CommentReport.builder()
                .account(account)
                .comment(comment)
                .type(commentReportInfoCreate.getType())
                .reason(commentReportInfoCreate.getReason())
                .build();

        this.reportRepository.saveCommentReport(commentReport);
        CommentInfo commentInfo = this.commentService.getComment(board, post, comment);
        return new CommentReportInfo(commentReport, commentInfo);
    }

    @Override
    public CommentReportInfoList getCommentReportInfoList(Account account, String boardId, CommentReportInfoSearch commentReportInfoSearch) {
        Board board = this.getBoard(boardId);
        return this.getCommentReportInfoList(account, board, commentReportInfoSearch);
    }

    @Override
    public CommentReportInfoList getCommentReportInfoList(Account account, Board board, CommentReportInfoSearch commentReportInfoSearch) {
        this.validate(account, board);

        if (!this.boardAuthorizationService.hasAuthorization(account, board)) {
            throw new IricomException(IricomErrorCode.INVALID_AUTHORIZATION_TO_GET_POST_REPORT_LIST);
        }

        int skip = commentReportInfoSearch.getSkip();
        int limit = commentReportInfoSearch.getLimit();
        ReportType reportType = commentReportInfoSearch.getType();
        String reason = commentReportInfoSearch.getReason();

        long total = -1;
        List<CommentReport> commentReportList;

        if (reportType != null) {
            total = this.reportRepository.getCommentReportListTotalCount(board, reportType, reason);
            commentReportList = this.reportRepository.getCommentReportList(board, reportType, reason, skip, limit);
        } else {
            total = this.reportRepository.getCommentReportListTotalCount(board, reason);
            commentReportList = this.reportRepository.getCommentReportList(board, reason, skip, limit);
        }

        List<CommentReportInfo> commentReportInfoList = commentReportList.stream()
                .map(item -> {
                    Comment comment = item.getComment();
                    Post post = comment.getPost();
                    CommentInfo commentInfo = this.commentService.getComment(board, post, comment);
                    return new CommentReportInfo(item, commentInfo);
                }).collect(Collectors.toList());
        return CommentReportInfoList.builder()
                .skip(skip)
                .limit(limit)
                .total(total)
                .commentReportInfoList(commentReportInfoList)
                .build();
    }

    @Override
    public CommentReportInfoList getCommentReportInfoList(Account account, String boardId, String postId, CommentReportInfoSearch commentReportInfoSearch) {
        Board board = this.getBoard(boardId);
        Post post = this.getPost(postId);

        return this.getCommentReportInfoList(account, board, post, commentReportInfoSearch);
    }

    @Override
    public CommentReportInfoList getCommentReportInfoList(Account account, Board board, Post post, CommentReportInfoSearch commentReportInfoSearch) {
        this.validate(account, board, post);

        if (!this.boardAuthorizationService.hasAuthorization(account, board)) {
            throw new IricomException(IricomErrorCode.INVALID_AUTHORIZATION_TO_GET_POST_REPORT_LIST);
        }
        int skip = commentReportInfoSearch.getSkip();
        int limit = commentReportInfoSearch.getLimit();
        ReportType reportType = commentReportInfoSearch.getType();
        String reason = commentReportInfoSearch.getReason();

        long total = -1;
        List<CommentReport> commentReportList;

        if (reportType != null) {
            total = this.reportRepository.getCommentReportListTotalCount(board, post, reportType, reason);
            commentReportList = this.reportRepository.getCommentReportList(board, post, reportType, reason, skip, limit);
        } else {
            total = this.reportRepository.getCommentReportListTotalCount(board, post, reason);
            commentReportList = this.reportRepository.getCommentReportList(board, post, reason, skip, limit);
        }

        List<CommentReportInfo> commentReportInfoList = commentReportList.stream()
                .map(item -> {
                    Comment comment = item.getComment();
                    CommentInfo commentInfo = this.commentService.getComment(board, post, comment);
                    return new CommentReportInfo(item, commentInfo);
                }).collect(Collectors.toList());
        return CommentReportInfoList.builder()
                .skip(skip)
                .limit(limit)
                .total(total)
                .commentReportInfoList(commentReportInfoList)
                .build();
    }

    @Override
    public CommentReportInfoList getCommentReportInfoList(Account account, String boardId, String postId, String commentId, CommentReportInfoSearch commentReportInfoSearch) {
        Board board = this.getBoard(boardId);
        Post post = this.getPost(postId);
        Comment comment = this.getComment(commentId);

        return this.getCommentReportInfoList(account, board, post, comment, commentReportInfoSearch);
    }

    @Override
    public CommentReportInfoList getCommentReportInfoList(Account account, Board board, Post post, Comment comment, CommentReportInfoSearch commentReportInfoSearch) {
        this.validate(account, board, post, comment);

        if (!this.boardAuthorizationService.hasAuthorization(account, board)) {
            throw new IricomException(IricomErrorCode.INVALID_AUTHORIZATION_TO_GET_POST_REPORT_LIST);
        }
        int skip = commentReportInfoSearch.getSkip();
        int limit = commentReportInfoSearch.getLimit();
        ReportType reportType = commentReportInfoSearch.getType();
        String reason = commentReportInfoSearch.getReason();

        long total = -1;
        List<CommentReport> commentReportList;

        if (reportType != null) {
            total = this.reportRepository.getCommentReportListTotalCount(board, post, comment, reportType, reason);
            commentReportList = this.reportRepository.getCommentReportList(board, post, comment, reportType, reason, skip, limit);
        } else {
            total = this.reportRepository.getCommentReportListTotalCount(board, post, comment, reason);
            commentReportList = this.reportRepository.getCommentReportList(board, post, comment, reason, skip, limit);
        }

        List<CommentReportInfo> commentReportInfoList = commentReportList.stream()
                .map(item -> {
                    CommentInfo commentInfo = this.commentService.getComment(board, post, comment);
                    return new CommentReportInfo(item, commentInfo);
                }).collect(Collectors.toList());
        return CommentReportInfoList.builder()
                .skip(skip)
                .limit(limit)
                .total(total)
                .commentReportInfoList(commentReportInfoList)
                .build();
    }

    @Override
    public CommentReportInfo getCommentReportInfo(Account account, String boardId, String postId, String commentId, String reportId) {
        Board board = this.getBoard(boardId);
        Post post = this.getPost(postId);
        Comment comment = this.getComment(commentId);

        return this.getCommentReportInfo(account, board, post, comment, reportId);
    }

    @Override
    public CommentReportInfo getCommentReportInfo(Account account, Board board, Post post, Comment comment, String reportId) {
        this.validate(account, board, post, comment);

        if (!this.boardAuthorizationService.hasAuthorization(account, board)) {
            throw new IricomException(IricomErrorCode.INVALID_AUTHORIZATION_TO_GET_POST_REPORT_LIST);
        }

        Post commentPost = comment.getPost();
        if (!commentPost.equals(post)) {
            throw new IricomException(IricomErrorCode.NOT_EXIST_COMMENT_REPORT);
        }

        Board commentBoard = commentPost.getBoard();
        if (!commentBoard.equals(board)) {
            throw new IricomException(IricomErrorCode.NOT_EXIST_COMMENT_REPORT);
        }

        Optional<CommentReport> commentReportOptional = this.reportRepository.getCommentReport(reportId);
        CommentReport commentReport = commentReportOptional.orElseThrow(() -> new IricomException(IricomErrorCode.NOT_EXIST_COMMENT_REPORT));
        if (!commentReport.getComment().equals(comment)) {
            throw new IricomException(IricomErrorCode.NOT_EXIST_COMMENT_REPORT);
        }

        CommentInfo commentInfo = this.commentService.getComment(board, post, comment);
        return new CommentReportInfo(commentReport, commentInfo);
    }
}
