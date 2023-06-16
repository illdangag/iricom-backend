package com.illdangag.iricom.server.service.implement;

import com.illdangag.iricom.server.data.entity.*;
import com.illdangag.iricom.server.data.request.CommentReportCreate;
import com.illdangag.iricom.server.data.request.PostBanCreate;
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
import org.checkerframework.checker.units.qual.A;
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
    public CommentReportInfo reportComment(Account account, CommentReportCreate commentReportCreate) {
        String boardId = commentReportCreate.getBoardId();
        String postId = commentReportCreate.getPostId();
        String commentId = commentReportCreate.getCommentId();

        Optional<Board> boardOptional = this.boardRepository.getBoard(boardId);
        Optional<Post> postOptional = this.postRepository.getPost(postId);
        Optional<Comment> commentOptional = this.commentRepository.getComment(commentId);

        Board board = boardOptional.orElseThrow(() -> new IricomException(IricomErrorCode.NOT_EXIST_BOARD));
        Post post = postOptional.orElseThrow(() -> new IricomException(IricomErrorCode.NOT_EXIST_POST));
        Comment comment = commentOptional.orElseThrow(() -> new IricomException(IricomErrorCode.NOT_EXIST_COMMENT));

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

    @Override
    public PostInfo banPost(Account account, String boardId, PostBanCreate postBanCreate) {
        Board board = this.getBoard(boardId);
        return this.banPost(account, board, postBanCreate);
    }

    @Override
    public PostInfo banPost(Account account, Board board, PostBanCreate postBanCreate) {
        if (!this.boardAuthorizationService.hasAuthorization(account, board)) {
            throw new IricomException(IricomErrorCode.INVALID_AUTHORIZATION_TO_BAN_POST);
        }

        Optional<Post> postOptional = this.postRepository.getPost(postBanCreate.getPostId());
        Post post = postOptional.orElseThrow(() -> {
            return new IricomException(IricomErrorCode.NOT_EXIST_POST);
        });

        if (!post.isPublish()) {
            // 발행되지 않은 게시물인 경우, 밴 처리를 하지 않음
            throw new IricomException(IricomErrorCode.NOT_EXIST_PUBLISH_CONTENT);
        }

        // 이미 밴 처리 된 게시물인지 확인
        List<PostBan> postBanList = this.banRepository.getPostBanList(post);
        if (!postBanList.isEmpty()) {
            throw new IricomException(IricomErrorCode.ALREADY_BAN_POST);
        }

        PostBan postBan = PostBan.builder()
                .post(post)
                .adminAccount(account)
                .reason(postBanCreate.getReason())
                .enabled(true)
                .build();
        this.banRepository.savePostBan(postBan);

        return this.postService.getPostInfo(post, PostState.PUBLISH, true);
    }

    private Board getBoard(String id) {
        Optional<Board> boardOptional = this.boardRepository.getBoard(id);
        return boardOptional.orElseThrow(() -> new IricomException(IricomErrorCode.NOT_EXIST_BOARD));
    }
}
