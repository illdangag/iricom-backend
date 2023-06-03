package com.illdangag.iricom.server.service.implement;

import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.data.entity.Board;
import com.illdangag.iricom.server.data.entity.Post;
import com.illdangag.iricom.server.data.entity.PostReport;
import com.illdangag.iricom.server.data.request.PostReportCreate;
import com.illdangag.iricom.server.exception.IricomErrorCode;
import com.illdangag.iricom.server.exception.IricomException;
import com.illdangag.iricom.server.repository.BoardRepository;
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

    @Autowired
    private ReportServiceImpl(ReportRepository reportRepository, PostRepository postRepository, BoardRepository boardRepository) {
        this.reportRepository = reportRepository;
        this.postRepository = postRepository;
        this.boardRepository = boardRepository;
    }

    @Override
    public void reportPost(Account account, PostReportCreate postReportCreate) {
        String boardId = postReportCreate.getBoardId();
        String postId = postReportCreate.getPostId();

        Optional<Board> boardOptional = this.boardRepository.getBoard(boardId);
        Board board = boardOptional.orElseThrow(() -> new IricomException(IricomErrorCode.NOT_EXIST_BOARD));
        Optional<Post> postOptional = this.postRepository.getPost(postId);
        Post post = postOptional.orElseThrow(() -> new IricomException(IricomErrorCode.NOT_EXIST_POST));

        if (!post.getBoard().equals(board)) {
            throw new IricomException(IricomErrorCode.NOT_EXIST_POST);
        }

        List<PostReport> postReportList = this.reportRepository.getPostReport(account, post);
        if (!postReportList.isEmpty()) {
            // 해당 계정으로 같은 게시물을 중복 신고는 허용하지 않음
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
}
