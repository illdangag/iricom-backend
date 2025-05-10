package com.illdangag.iricom.core.service.implement;

import com.illdangag.iricom.core.data.entity.*;
import com.illdangag.iricom.core.data.entity.type.AccountAuth;
import com.illdangag.iricom.core.data.entity.type.PostState;
import com.illdangag.iricom.core.data.request.CommentBlockInfoCreate;
import com.illdangag.iricom.core.data.request.PostBlockInfoCreate;
import com.illdangag.iricom.core.data.request.PostBlockInfoSearch;
import com.illdangag.iricom.core.data.request.PostBlockInfoUpdate;
import com.illdangag.iricom.core.data.response.*;
import com.illdangag.iricom.core.exception.IricomErrorCode;
import com.illdangag.iricom.core.exception.IricomException;
import com.illdangag.iricom.core.repository.*;
import com.illdangag.iricom.core.service.BlockService;
import com.illdangag.iricom.core.service.BoardAuthorizationService;
import com.illdangag.iricom.core.service.CommentService;
import com.illdangag.iricom.core.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Transactional
@Service
public class BlockServiceImpl extends IricomService implements BlockService {
    private final BlockRepository blockRepository;

    private final BoardAuthorizationService boardAuthorizationService;
    private final PostService postService;
    private final CommentService commentService;

    @Autowired
    public BlockServiceImpl(AccountRepository accountRepository, BoardRepository boardRepository,
                            PostRepository postRepository, CommentRepository commentRepository,
                            BlockRepository blockRepository, BoardAuthorizationService boardAuthorizationService,
                            PostService postService, CommentService commentService) {
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
        if (post.getPostBlock() != null) {
            throw new IricomException(IricomErrorCode.ALREADY_BLOCK_POST);
        }

        PostBlock postBlock = PostBlock.builder()
                .post(post)
                .adminAccount(account)
                .reason(postBlockInfoCreate.getReason())
                .build();
        post.setPostBlock(postBlock);
        this.postRepository.save(post);

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

        if (post.getPostBlock() == null) {
            throw new IricomException(IricomErrorCode.NOT_BLOCKED_POST);
        }

        PostBlock postBlock = post.getPostBlock();
        post.setPostBlock(null);
        this.postRepository.save(post);
        this.blockRepository.remove(postBlock);

        PostInfo postInfo = this.postService.getPostInfo(account, post, PostState.PUBLISH, false);
        return new PostBlockInfo(postBlock, postInfo);
    }

    @Override
    public PostBlockInfoList getPostBlockInfoList(String accountId, PostBlockInfoSearch postBlockInfoSearch) {
        Account account = this.getAccount(accountId);
        return this.getPostBlockInfoList(account, postBlockInfoSearch);
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

        if (post.getPostBlock() == null) {
            throw new IricomException(IricomErrorCode.NOT_EXIST_POST_BLOCK);
        }

        PostBlock postBlock = post.getPostBlock();
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

        if (post.getPostBlock() == null) {
            throw new IricomException(IricomErrorCode.NOT_EXIST_POST_BLOCK);
        }

        PostBlock postBlock = post.getPostBlock();
        String reason = postBlockInfoUpdate.getReason();
        postBlock.setReason(reason);
        this.postRepository.save(post);

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
        if (post.getPostBlock() != null) {
            throw new IricomException(IricomErrorCode.ALREADY_BLOCK_POST);
        }

        // 이미 차단 처리 된 댓글인지 확인
        List<CommentBlock> commentBlockList = this.blockRepository.getCommentBlockList(comment, null, null);
        if (!commentBlockList.isEmpty()) {
            throw new IricomException(IricomErrorCode.ALREADY_BLOCKED_COMMENT);
        }

        CommentBlock commentBlock = CommentBlock.builder()
                .comment(comment)
                .adminAccount(account)
                .reason(commentBlockInfoCreate.getReason())
                .build();

        comment.setCommentBlock(commentBlock);
        this.commentRepository.save(comment);

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
        if (post.getPostBlock() != null) {
            throw new IricomException(IricomErrorCode.ALREADY_BLOCK_POST);
        }

        // 차단 처리 된 댓글인지 확인
        List<CommentBlock> commentBlockList = this.blockRepository.getCommentBlockList(comment, null, null);
        if (commentBlockList.isEmpty()) {
            throw new IricomException(IricomErrorCode.NOT_BLOCK_COMMENT);
        }

        CommentBlock commentBlock = commentBlockList.get(0);
        comment.setCommentBlock(null);
        this.commentRepository.save(comment);
        this.blockRepository.remove(commentBlock);

        CommentInfo commentInfo = this.commentService.getComment(account, board, post, comment);
        return new CommentBlockInfo(commentBlock, commentInfo);
    }
}
