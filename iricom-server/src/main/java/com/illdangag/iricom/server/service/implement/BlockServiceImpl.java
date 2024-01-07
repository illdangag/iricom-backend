package com.illdangag.iricom.server.service.implement;

import com.illdangag.iricom.server.data.entity.*;
import com.illdangag.iricom.server.data.entity.type.AccountAuth;
import com.illdangag.iricom.server.data.entity.type.PostState;
import com.illdangag.iricom.server.data.request.CommentBlockInfoCreate;
import com.illdangag.iricom.server.data.request.PostBlockInfoCreate;
import com.illdangag.iricom.server.data.request.PostBlockInfoSearch;
import com.illdangag.iricom.server.data.request.PostBlockInfoUpdate;
import com.illdangag.iricom.server.data.response.*;
import com.illdangag.iricom.server.exception.IricomErrorCode;
import com.illdangag.iricom.server.exception.IricomException;
import com.illdangag.iricom.server.repository.*;
import com.illdangag.iricom.server.service.BlockService;
import com.illdangag.iricom.server.service.BoardAuthorizationService;
import com.illdangag.iricom.server.service.CommentService;
import com.illdangag.iricom.server.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BlockServiceImpl extends IricomService implements BlockService {
    private final BlockRepository blockRepository;

    private final BoardAuthorizationService boardAuthorizationService;
    private final PostService postService;
    private final CommentService commentService;

    @Autowired
    public BlockServiceImpl(AccountRepository accountRepository, PostRepository postRepository,
                            BlockRepository blockRepository, BoardRepository boardRepository,
                            BoardAuthorizationService boardAuthorizationService, PostService postService,
                            CommentRepository commentRepository, CommentService commentService) {
        super(accountRepository, boardRepository, postRepository, commentRepository);
        this.blockRepository = blockRepository;
        this.boardAuthorizationService = boardAuthorizationService;
        this.postService = postService;
        this.commentService = commentService;
    }

    @Override
    public PostBlockInfo blockPost(String accountId, String boardId, String postId, PostBlockInfoCreate postBlockInfoCreate) {
        Account account = this.getAccount(accountId);
        return this.blockPost(account, boardId, postId, postBlockInfoCreate);
    }

    @Override
    public PostBlockInfo blockPost(Account account, String boardId, String postId, PostBlockInfoCreate postBlockInfoCreate) {
        Board board = this.getBoard(boardId);
        Post post = this.getPost(postId);
        return this.blockPost(account, board, post, postBlockInfoCreate);
    }

    @Override
    public PostBlockInfo blockPost(Account account, Board board, Post post, PostBlockInfoCreate postBlockInfoCreate) {
        this.validate(account, board, post);

        if (!this.boardAuthorizationService.hasAuthorization(account, board)) { // 계정이 게시판에 관리자 권한이 있는지 확인
            throw new IricomException(IricomErrorCode.INVALID_AUTHORIZATION_TO_BLOCK_POST);
        }

        if (!post.isPublish()) { // 발행되지 않은 게시물인 경우
            // 게시물 차단을 할 수 없음
            throw new IricomException(IricomErrorCode.NOT_EXIST_PUBLISH_CONTENT);
        }

        // 이미 밴 처리 된 게시물인지 확인
        List<PostBlock> postBlockList = this.blockRepository.getPostBlockList(post);
        if (!postBlockList.isEmpty()) {
            throw new IricomException(IricomErrorCode.ALREADY_BLOCK_POST);
        }

        PostBlock postBlock = PostBlock.builder()
                .post(post)
                .adminAccount(account)
                .reason(postBlockInfoCreate.getReason())
                .enabled(true)
                .build();

        this.blockRepository.save(postBlock);

        PostInfo postInfo = this.postService.getPostInfo(account, post, PostState.PUBLISH, false);
        return new PostBlockInfo(postBlock, postInfo);
    }

    @Override
    public PostBlockInfo unblockPost(String accountId, String boardId, String postId) {
        Account account = this.getAccount(accountId);
        return this.unblockPost(account, boardId, postId);
    }

    @Override
    public PostBlockInfo unblockPost(Account account, String boardId, String postId) {
        Board board = this.getBoard(boardId);
        Post post = this.getPost(postId);
        return this.unblockPost(account, board, post);
    }

    @Override
    public PostBlockInfo unblockPost(Account account, Board board, Post post) {
        this.validate(account, board, post);

        if (!this.boardAuthorizationService.hasAuthorization(account, board)) {
            throw new IricomException(IricomErrorCode.INVALID_AUTHORIZATION_TO_BLOCK_POST);
        }

        List<PostBlock> postBlockList = this.blockRepository.getPostBlockList(post);
        if (postBlockList.isEmpty()) {
            throw new IricomException(IricomErrorCode.NOT_BLOCKED_POST);
        }

        postBlockList.forEach(item -> item.setEnabled(false));
        postBlockList.forEach(this.blockRepository::save);

        PostBlock postBlock = postBlockList.get(0);
        PostInfo postInfo = this.postService.getPostInfo(account, post, PostState.PUBLISH, false);
        return new PostBlockInfo(postBlock, postInfo);
    }

    @Override
    public PostBlockInfoList getPostBlockInfoList(Account account, PostBlockInfoSearch postBlockInfoSearch) {
        if (!account.getAuth().equals(AccountAuth.SYSTEM_ADMIN)) {
            throw new IricomException(IricomErrorCode.INVALID_AUTHORIZATION_TO_BLOCK_POST);
        }

        String reason = postBlockInfoSearch.getReason();
        int skip = postBlockInfoSearch.getSkip();
        int limit = postBlockInfoSearch.getLimit();

        List<PostBlock> postBlockList = this.blockRepository.getPostBlockList(reason, skip, limit);
        long total = this.blockRepository.getPostBlockListCount(reason);

        List<PostBlockInfo> postBlockInfoList = postBlockList.stream()
                .map(item -> {
                    PostInfo postInfo = this.postService.getPostInfo(account, item.getPost(), PostState.PUBLISH, false);
                    return new PostBlockInfo(item, postInfo);
                })
                .collect(Collectors.toList());

        return PostBlockInfoList.builder()
                .total(total)
                .skip(skip)
                .limit(limit)
                .postBlockInfoList(postBlockInfoList)
                .build();
    }

    @Override
    public PostBlockInfoList getPostBlockInfoList(String accountId, String boardId, PostBlockInfoSearch postBlockInfoSearch) {
        Account account = this.getAccount(accountId);
        return this.getPostBlockInfoList(account, boardId, postBlockInfoSearch);
    }

    @Override
    public PostBlockInfoList getPostBlockInfoList(Account account, String boardId, PostBlockInfoSearch postBlockInfoSearch) {
        Board board = this.getBoard(boardId);
        return this.getPostBlockInfoList(account, board, postBlockInfoSearch);
    }

    @Override
    public PostBlockInfoList getPostBlockInfoList(Account account, Board board, PostBlockInfoSearch postBlockInfoSearch) {
        if (!this.boardAuthorizationService.hasAuthorization(account, board)) {
            throw new IricomException(IricomErrorCode.INVALID_AUTHORIZATION_TO_BLOCK_POST);
        }

        String reason = postBlockInfoSearch.getReason();
        int skip = postBlockInfoSearch.getSkip();
        int limit = postBlockInfoSearch.getLimit();

        List<PostBlock> postBlockList = this.blockRepository.getPostBlockList(board, reason, skip, limit);
        long total = this.blockRepository.getPostBlockListCount(board, reason);

        List<PostBlockInfo> postBlockInfoList = postBlockList.stream()
                .map(item -> {
                    PostInfo postInfo = this.postService.getPostInfo(account, item.getPost(), PostState.PUBLISH, false);
                    return new PostBlockInfo(item, postInfo);
                })
                .collect(Collectors.toList());

        return PostBlockInfoList.builder()
                .total(total)
                .skip(skip)
                .limit(limit)
                .postBlockInfoList(postBlockInfoList)
                .build();
    }

    @Override
    public PostBlockInfo getPostBlockInfo(String accountId, String boardId, String postId) {
        Account account = this.getAccount(accountId);
        return this.getPostBlockInfo(account, boardId, postId);
    }

    @Override
    public PostBlockInfo getPostBlockInfo(Account account, String boardId, String postId) {
        Board board = this.getBoard(boardId);
        Post post = this.getPost(postId);
        return this.getPostBlockInfo(account, board, post);
    }

    @Override
    public PostBlockInfo getPostBlockInfo(Account account, Board board, Post post) {
        if (!this.boardAuthorizationService.hasAuthorization(account, board)) {
            throw new IricomException(IricomErrorCode.INVALID_AUTHORIZATION_TO_BLOCK_POST);
        }

        if (!board.equals(post.getBoard())) {
            throw new IricomException(IricomErrorCode.NOT_EXIST_POST);
        }

        List<PostBlock> postBlockList = this.blockRepository.getPostBlockList(post);
        if (postBlockList.isEmpty()) {
            throw new IricomException(IricomErrorCode.NOT_EXIST_POST_BLOCK);
        }

        PostBlock postBlock = postBlockList.get(0);

        PostInfo postInfo = this.postService.getPostInfo(account, post, PostState.PUBLISH, false);
        return new PostBlockInfo(postBlock, postInfo);
    }

    @Override
    public PostBlockInfo updatePostBlockInfo(String accountId, String boardId, String postId, PostBlockInfoUpdate postBlockInfoUpdate) {
        Account account = this.getAccount(accountId);
        return this.updatePostBlockInfo(account, boardId, postId, postBlockInfoUpdate);
    }

    @Override
    public PostBlockInfo updatePostBlockInfo(Account account, String boardId, String postId, PostBlockInfoUpdate postBlockInfoUpdate) {
        Board board = this.getBoard(boardId);
        Post post = this.getPost(postId);
        return this.updatePostBlockInfo(account, board, post, postBlockInfoUpdate);
    }

    @Override
    public PostBlockInfo updatePostBlockInfo(Account account, Board board, Post post, PostBlockInfoUpdate postBlockInfoUpdate) {
        if (!this.boardAuthorizationService.hasAuthorization(account, board)) {
            throw new IricomException(IricomErrorCode.INVALID_AUTHORIZATION_TO_BLOCK_POST);
        }

        if (!board.equals(post.getBoard())) {
            throw new IricomException(IricomErrorCode.NOT_EXIST_POST);
        }

        List<PostBlock> postBlockList = this.blockRepository.getPostBlockList(post);
        if (postBlockList.isEmpty()) {
            throw new IricomException(IricomErrorCode.NOT_EXIST_POST_BLOCK);
        }

        PostBlock postBlock = postBlockList.get(0);

        String reason = postBlockInfoUpdate.getReason();
        postBlock.setReason(reason);
        this.blockRepository.save(postBlock);

        PostInfo postInfo = this.postService.getPostInfo(account, post, PostState.PUBLISH, false);
        return new PostBlockInfo(postBlock, postInfo);
    }

    @Override
    public CommentBlockInfo blockComment(String accountId, String boardId, String postId, String commentId, @Valid CommentBlockInfoCreate commentBlockInfoCreate) {
        Account account = this.getAccount(accountId);
        return this.blockComment(account, boardId, postId, commentId, commentBlockInfoCreate);
    }

    @Override
    public CommentBlockInfo blockComment(Account account, String boardId, String postId, String commentId, @Valid CommentBlockInfoCreate commentBlockInfoCreate) {
        Board board = this.getBoard(boardId);
        Post post = this.getPost(postId);
        Comment comment = this.getComment(commentId);

        return this.blockComment(account, board, post, comment, commentBlockInfoCreate);
    }

    @Override
    public CommentBlockInfo blockComment(Account account, Board board, Post post, Comment comment, @Valid CommentBlockInfoCreate commentBlockInfoCreate) {
        this.validate(account, board, post, comment);

        if (!this.boardAuthorizationService.hasAuthorization(account, board)) { // 계정이 게시판에 관리자 권한이 있는지 확인
            throw new IricomException(IricomErrorCode.INVALID_AUTHORIZATION_TO_BLOCK_COMMENT);
        }

        if (!post.isPublish()) { // 발행되지 않은 게시물인 경우
            // 게시물 차단을 할 수 없음
            throw new IricomException(IricomErrorCode.NOT_EXIST_PUBLISH_CONTENT);
        }

        // 이미 차단 처리 된 게시물인지 확인
        List<PostBlock> postBlockList = this.blockRepository.getPostBlockList(post);
        if (!postBlockList.isEmpty()) {
            throw new IricomException(IricomErrorCode.ALREADY_BLOCK_POST);
        }

        // 이미 차단 처리 된 댓글인지 확인
        List<CommentBlock> commentBlockList = this.blockRepository.getCommentBlockList(comment, true, null, null);
        if (!commentBlockList.isEmpty()) {
            throw new IricomException(IricomErrorCode.ALREADY_BLOCKED_COMMENT);
        }

        CommentBlock commentBlock = CommentBlock.builder()
                .comment(comment)
                .adminAccount(account)
                .reason(commentBlockInfoCreate.getReason())
                .enabled(true)
                .build();

        this.blockRepository.save(commentBlock);

        CommentInfo commentInfo = this.commentService.getComment(account, board, post, comment);
        return new CommentBlockInfo(commentBlock, commentInfo);
    }

    @Override
    public CommentBlockInfo unblockComment(String accountId, String boardId, String postId, String commentId) {
        Account account = this.getAccount(accountId);
        return this.unblockComment(account, boardId, postId, commentId);
    }

    @Override
    public CommentBlockInfo unblockComment(Account account, String boardId, String postId, String commentId) {
        Board board = this.getBoard(boardId);
        Post post = this.getPost(postId);
        Comment comment = this.getComment(commentId);

        return this.unblockComment(account, board, post, comment);
    }

    @Override
    public CommentBlockInfo unblockComment(Account account, Board board, Post post, Comment comment) {
        this.validate(account, board, post, comment);

        if (!this.boardAuthorizationService.hasAuthorization(account, board)) { // 계정이 게시판에 관리자 권한이 있는지 확인
            throw new IricomException(IricomErrorCode.INVALID_AUTHORIZATION_TO_BLOCK_COMMENT);
        }

        if (!post.isPublish()) { // 발행되지 않은 게시물인 경우
            // 게시물 차단을 할 수 없음
            throw new IricomException(IricomErrorCode.NOT_EXIST_PUBLISH_CONTENT);
        }

        // 이미 차단 처리 된 게시물인지 확인
        List<PostBlock> postBlockList = this.blockRepository.getPostBlockList(post);
        if (!postBlockList.isEmpty()) {
            throw new IricomException(IricomErrorCode.ALREADY_BLOCK_POST);
        }

        // 차단 처리 된 댓글인지 확인
        List<CommentBlock> commentBlockList = this.blockRepository.getCommentBlockList(comment, true, null, null);
        if (commentBlockList.isEmpty()) {
            throw new IricomException(IricomErrorCode.NOT_BLOCK_COMMENT);
        }

        CommentBlock commentBlock = commentBlockList.get(0);
        commentBlock.setEnabled(false);
        this.blockRepository.save(commentBlock);

        CommentInfo commentInfo = this.commentService.getComment(account, board, post, comment);
        return new CommentBlockInfo(commentBlock, commentInfo);
    }
}
