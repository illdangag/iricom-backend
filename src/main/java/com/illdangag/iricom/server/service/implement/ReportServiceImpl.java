package com.illdangag.iricom.server.service.implement;

import com.illdangag.iricom.server.data.entity.*;
import com.illdangag.iricom.server.data.request.CommentReportCreate;
import com.illdangag.iricom.server.data.request.PostReportCreate;
import com.illdangag.iricom.server.exception.IricomErrorCode;
import com.illdangag.iricom.server.exception.IricomException;
import com.illdangag.iricom.server.repository.BoardRepository;
import com.illdangag.iricom.server.repository.CommentRepository;
import com.illdangag.iricom.server.repository.PostRepository;
import com.illdangag.iricom.server.repository.ReportRepository;
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

    @Autowired
    private ReportServiceImpl(ReportRepository reportRepository, PostRepository postRepository, BoardRepository boardRepository, CommentRepository commentRepository) {
        this.reportRepository = reportRepository;
        this.postRepository = postRepository;
        this.boardRepository = boardRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    public void reportPost(Account account, PostReportCreate postReportCreate) {
        String boardId = postReportCreate.getBoardId();
        String postId = postReportCreate.getPostId();

        Optional<Board> boardOptional = this.boardRepository.getBoard(boardId);
        Optional<Post> postOptional = this.postRepository.getPost(postId);

        Board board = boardOptional.orElseThrow(() -> new IricomException(IricomErrorCode.NOT_EXIST_BOARD));
        Post post = postOptional.orElseThrow(() -> new IricomException(IricomErrorCode.NOT_EXIST_POST));

        if (!post.getBoard().equals(board)) {
            throw new IricomException(IricomErrorCode.NOT_EXIST_POST);
        }

        List<PostReport> postReportList = this.reportRepository.getPostReport(account, post);
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
    }

    @Override
    public void reportComment(Account account, CommentReportCreate commentReportCreate) {
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
            throw new IricomException(IricomErrorCode.NOT_EXIST_COMMENT);
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
    }
}
