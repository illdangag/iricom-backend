package com.illdangag.iricom.server.service.implement;

import com.illdangag.iricom.server.data.entity.*;
import com.illdangag.iricom.server.data.entity.type.AccountAuth;
import com.illdangag.iricom.server.data.entity.type.PostState;
import com.illdangag.iricom.server.data.request.CommentBanInfoCreate;
import com.illdangag.iricom.server.data.request.PostBanInfoCreate;
import com.illdangag.iricom.server.data.request.PostBanInfoSearch;
import com.illdangag.iricom.server.data.request.PostBanInfoUpdate;
import com.illdangag.iricom.server.data.response.CommentBanInfo;
import com.illdangag.iricom.server.data.response.PostBanInfo;
import com.illdangag.iricom.server.data.response.PostBanInfoList;
import com.illdangag.iricom.server.data.response.PostInfo;
import com.illdangag.iricom.server.exception.IricomErrorCode;
import com.illdangag.iricom.server.exception.IricomException;
import com.illdangag.iricom.server.repository.BanRepository;
import com.illdangag.iricom.server.repository.BoardRepository;
import com.illdangag.iricom.server.repository.CommentRepository;
import com.illdangag.iricom.server.repository.PostRepository;
import com.illdangag.iricom.server.service.BanService;
import com.illdangag.iricom.server.service.BoardAuthorizationService;
import com.illdangag.iricom.server.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BanServiceImpl extends IricomService implements BanService {
    private final BanRepository banRepository;

    private final BoardAuthorizationService boardAuthorizationService;
    private final PostService postService;

    @Autowired
    public BanServiceImpl(PostRepository postRepository, BanRepository banRepository, BoardRepository boardRepository,
                          BoardAuthorizationService boardAuthorizationService, PostService postService, CommentRepository commentRepository) {
        super(boardRepository, postRepository, commentRepository);
        this.banRepository = banRepository;
        this.boardAuthorizationService = boardAuthorizationService;
        this.postService = postService;
    }

    @Override
    public PostBanInfo banPost(Account account, String boardId, String postId, PostBanInfoCreate postBanInfoCreate) {
        Board board = this.getBoard(boardId);
        Post post = this.getPost(postId);
        return this.banPost(account, board, post, postBanInfoCreate);
    }

    @Override
    public PostBanInfo banPost(Account account, Board board, Post post, PostBanInfoCreate postBanInfoCreate) {
        if (!this.boardAuthorizationService.hasAuthorization(account, board)) {
            throw new IricomException(IricomErrorCode.INVALID_AUTHORIZATION_TO_BAN_POST);
        }

        if (!post.getBoard().equals(board)) {
            throw new IricomException(IricomErrorCode.NOT_EXIST_POST);
        }

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
                .reason(postBanInfoCreate.getReason())
                .enabled(true)
                .build();
        this.banRepository.savePostBan(postBan);

        PostInfo postInfo = this.postService.getPostInfo(account, post, PostState.PUBLISH, false);
        return new PostBanInfo(postBan, postInfo);
    }

    @Override
    public PostBanInfo unbanPost(Account account, String boardId, String postId) {
        Board board = this.getBoard(boardId);
        Post post = this.getPost(postId);
        return this.unbanPost(account, board, post);
    }

    @Override
    public PostBanInfo unbanPost(Account account, Board board, Post post) {
        if (!this.boardAuthorizationService.hasAuthorization(account, board)) {
            throw new IricomException(IricomErrorCode.INVALID_AUTHORIZATION_TO_BAN_POST);
        }

        if (!post.getBoard().equals(board)) {
            throw new IricomException(IricomErrorCode.NOT_EXIST_POST);
        }

        List<PostBan> postBanList = this.banRepository.getPostBanList(post);

        if (postBanList.isEmpty()) {
            throw new IricomException(IricomErrorCode.ALREADY_UNBAN_POST);
        }

        postBanList.forEach(item -> item.setEnabled(false));
        postBanList.forEach(this.banRepository::savePostBan);

        PostBan postBan = postBanList.get(0);
        PostInfo postInfo = this.postService.getPostInfo(account, post, PostState.PUBLISH, false);
        return new PostBanInfo(postBan, postInfo);
    }

    @Override
    public PostBanInfoList getPostBanInfoList(Account account, PostBanInfoSearch postBanInfoSearch) {
        if (!account.getAuth().equals(AccountAuth.SYSTEM_ADMIN)) {
            throw new IricomException(IricomErrorCode.INVALID_AUTHORIZATION_TO_BAN_POST);
        }

        String reason = postBanInfoSearch.getReason();
        int skip = postBanInfoSearch.getSkip();
        int limit = postBanInfoSearch.getLimit();

        List<PostBan> postBanList = this.banRepository.getPostBanList(reason, skip, limit);
        long total = this.banRepository.getPostBanListCount(reason);

        List<PostBanInfo> postBanInfoList = postBanList.stream()
                .map(item -> {
                    PostInfo postInfo = this.postService.getPostInfo(account, item.getPost(), PostState.PUBLISH, false);
                    return new PostBanInfo(item, postInfo);
                })
                .collect(Collectors.toList());

        return PostBanInfoList.builder()
                .total(total)
                .skip(skip)
                .limit(limit)
                .postBanInfoList(postBanInfoList)
                .build();
    }

    @Override
    public PostBanInfoList getPostBanInfoList(Account account, String boardId, PostBanInfoSearch postBanInfoSearch) {
        Board board = this.getBoard(boardId);
        return this.getPostBanInfoList(account, board, postBanInfoSearch);
    }

    @Override
    public PostBanInfoList getPostBanInfoList(Account account, Board board, PostBanInfoSearch postBanInfoSearch) {
        if (!this.boardAuthorizationService.hasAuthorization(account, board)) {
            throw new IricomException(IricomErrorCode.INVALID_AUTHORIZATION_TO_BAN_POST);
        }

        String reason = postBanInfoSearch.getReason();
        int skip = postBanInfoSearch.getSkip();
        int limit = postBanInfoSearch.getLimit();

        List<PostBan> postBanList = this.banRepository.getPostBanList(board, reason, skip, limit);
        long total = this.banRepository.getPostBanListCount(board, reason);

        List<PostBanInfo> postBanInfoList = postBanList.stream()
                .map(item -> {
                    PostInfo postInfo = this.postService.getPostInfo(account, item.getPost(), PostState.PUBLISH, false);
                    return new PostBanInfo(item, postInfo);
                })
                .collect(Collectors.toList());

        return PostBanInfoList.builder()
                .total(total)
                .skip(skip)
                .limit(limit)
                .postBanInfoList(postBanInfoList)
                .build();
    }

    @Override
    public PostBanInfo getPostBanInfo(Account account, String boardId, String postId) {
        Board board = this.getBoard(boardId);
        Post post = this.getPost(postId);
        return this.getPostBanInfo(account, board, post);
    }

    @Override
    public PostBanInfo getPostBanInfo(Account account, Board board, Post post) {
        if (!this.boardAuthorizationService.hasAuthorization(account, board)) {
            throw new IricomException(IricomErrorCode.INVALID_AUTHORIZATION_TO_BAN_POST);
        }

        if (!board.equals(post.getBoard())) {
            throw new IricomException(IricomErrorCode.NOT_EXIST_POST);
        }

        List<PostBan> postBanList = this.banRepository.getPostBanList(post);
        if (postBanList.isEmpty()) {
            throw new IricomException(IricomErrorCode.NOT_EXIST_POST_BAN);
        }

        PostBan postBan = postBanList.get(0);

        PostInfo postInfo = this.postService.getPostInfo(account, post, PostState.PUBLISH, false);
        return new PostBanInfo(postBan, postInfo);
    }

    @Override
    public PostBanInfo updatePostBanInfo(Account account, String boardId, String postId, PostBanInfoUpdate postBanInfoUpdate) {
        Board board = this.getBoard(boardId);
        Post post = this.getPost(postId);
        return this.updatePostBanInfo(account, board, post, postBanInfoUpdate);
    }

    @Override
    public PostBanInfo updatePostBanInfo(Account account, Board board, Post post, PostBanInfoUpdate postBanInfoUpdate) {
        if (!this.boardAuthorizationService.hasAuthorization(account, board)) {
            throw new IricomException(IricomErrorCode.INVALID_AUTHORIZATION_TO_BAN_POST);
        }

        if (!board.equals(post.getBoard())) {
            throw new IricomException(IricomErrorCode.NOT_EXIST_POST);
        }

        List<PostBan> postBanList = this.banRepository.getPostBanList(post);
        if (postBanList.isEmpty()) {
            throw new IricomException(IricomErrorCode.NOT_EXIST_POST_BAN);
        }

        PostBan postBan = postBanList.get(0);

        String reason = postBanInfoUpdate.getReason();
        postBan.setReason(reason);
        this.banRepository.savePostBan(postBan);

        PostInfo postInfo = this.postService.getPostInfo(account, post, PostState.PUBLISH, false);
        return new PostBanInfo(postBan, postInfo);
    }

    @Override
    public CommentBanInfo banComment(Account account, String boardId, String postId, String commentId, @Valid CommentBanInfoCreate commentBanInfoCreate) {
        Board board = this.getBoard(boardId);
        Post post = this.getPost(postId);
        Comment comment = this.getComment(commentId);

        return this.banComment(account, board, post, comment, commentBanInfoCreate);
    }

    @Override
    public CommentBanInfo banComment(Account account, Board board, Post post, Comment comment, @Valid CommentBanInfoCreate commentBanInfoCreate) {

        return null;
    }
}
