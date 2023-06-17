package com.illdangag.iricom.server.service.implement;

import com.illdangag.iricom.server.data.entity.*;
import com.illdangag.iricom.server.data.request.CommentReportCreate;
import com.illdangag.iricom.server.data.request.PostReportCreate;
import com.illdangag.iricom.server.data.response.CommentInfo;
import com.illdangag.iricom.server.data.response.CommentReportInfo;
import com.illdangag.iricom.server.data.response.PostInfo;
import com.illdangag.iricom.server.data.response.PostReportInfo;
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

@Slf4j
@Service
public class ReportServiceImpl implements ReportService {
    private final ReportRepository reportRepository;
    private final PostRepository postRepository;
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    private final BanRepository banRepository;

    private final PostService postService;
    private final CommentService commentService;
    private final BoardAuthorizationService boardAuthorizationService;

    @Autowired
    private ReportServiceImpl(ReportRepository reportRepository, PostRepository postRepository, BoardRepository boardRepository,
                              CommentRepository commentRepository, BanRepository banRepository,
                              PostService postService, CommentService commentService, BoardAuthorizationService boardAuthorizationService) {
        this.reportRepository = reportRepository;
        this.postRepository = postRepository;
        this.boardRepository = boardRepository;
        this.commentRepository = commentRepository;
        this.banRepository = banRepository;

        this.postService = postService;
        this.commentService = commentService;
        this.boardAuthorizationService = boardAuthorizationService;
    }

    @Override
    public PostReportInfo reportPost(Account account, String boardId, String postId, PostReportCreate postReportCreate) {
        Optional<Board> boardOptional = this.boardRepository.getBoard(boardId);
        Optional<Post> postOptional = this.postRepository.getPost(postId);

        Board board = boardOptional.orElseThrow(() -> new IricomException(IricomErrorCode.NOT_EXIST_BOARD));
        Post post = postOptional.orElseThrow(() -> new IricomException(IricomErrorCode.NOT_EXIST_POST));

        return this.reportPost(account, board, post, postReportCreate);
    }

    @Override
    public PostReportInfo reportPost(Account account, Board board, Post post, PostReportCreate postReportCreate) {
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
                .type(postReportCreate.getType())
                .reason(postReportCreate.getReason())
                .build();

        this.reportRepository.savePostReport(postReport);

        PostInfo postInfo = this.postService.getPostInfo(account, board, post, PostState.PUBLISH);
        return new PostReportInfo(postReport, postInfo);
    }

    @Override
    public CommentReportInfo reportComment(Account account, String boardId, String postId, String commentId, CommentReportCreate commentReportCreate) {
        Optional<Board> boardOptional = this.boardRepository.getBoard(boardId);
        Optional<Post> postOptional = this.postRepository.getPost(postId);
        Optional<Comment> commentOptional = this.commentRepository.getComment(commentId);

        Board board = boardOptional.orElseThrow(() -> new IricomException(IricomErrorCode.NOT_EXIST_BOARD));
        Post post = postOptional.orElseThrow(() -> new IricomException(IricomErrorCode.NOT_EXIST_POST));
        Comment comment = commentOptional.orElseThrow(() -> new IricomException(IricomErrorCode.NOT_EXIST_COMMENT));

        return this.reportComment(account, board, post, comment, commentReportCreate);
    }

    @Override
    public CommentReportInfo reportComment(Account account, Board board, Post post, Comment comment, CommentReportCreate commentReportCreate) {
        if (!comment.getPost().equals(post)) {
            throw new IricomException(IricomErrorCode.NOT_EXIST_COMMENT);
        } else if (!post.getBoard().equals(board)) {
            throw new IricomException(IricomErrorCode.NOT_EXIST_POST);
        } else if (!board.getEnabled()) {
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
                .type(commentReportCreate.getType())
                .reason(commentReportCreate.getReason())
                .build();

        this.reportRepository.saveCommentReport(commentReport);
        CommentInfo commentInfo = this.commentService.getComment(board, post, comment);
        return new CommentReportInfo(commentReport, commentInfo);
    }
}
