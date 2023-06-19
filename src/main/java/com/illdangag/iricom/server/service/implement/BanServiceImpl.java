package com.illdangag.iricom.server.service.implement;

import com.illdangag.iricom.server.data.entity.*;
import com.illdangag.iricom.server.data.request.PostBanInfoCreate;
import com.illdangag.iricom.server.data.request.PostBanInfoSearch;
import com.illdangag.iricom.server.data.response.PostBanInfo;
import com.illdangag.iricom.server.data.response.PostBanInfoList;
import com.illdangag.iricom.server.data.response.PostInfo;
import com.illdangag.iricom.server.exception.IricomErrorCode;
import com.illdangag.iricom.server.exception.IricomException;
import com.illdangag.iricom.server.repository.BanRepository;
import com.illdangag.iricom.server.repository.BoardRepository;
import com.illdangag.iricom.server.repository.PostRepository;
import com.illdangag.iricom.server.service.BanService;
import com.illdangag.iricom.server.service.BoardAuthorizationService;
import com.illdangag.iricom.server.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BanServiceImpl implements BanService {
    private final PostRepository postRepository;
    private final BanRepository banRepository;
    private final BoardRepository boardRepository;

    private final BoardAuthorizationService boardAuthorizationService;
    private final PostService postService;

    @Autowired
    public BanServiceImpl(PostRepository postRepository, BanRepository banRepository, BoardRepository boardRepository,
                          BoardAuthorizationService boardAuthorizationService, PostService postService) {
        this.postRepository = postRepository;
        this.banRepository = banRepository;
        this.boardRepository = boardRepository;
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

        PostInfo postInfo = this.postService.getPostInfo(post, PostState.PUBLISH, false);
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
        PostInfo postInfo = this.postService.getPostInfo(post, PostState.PUBLISH, false);
        return new PostBanInfo(postBan, postInfo);
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

        List<PostBanInfo> postBanInfoList = postBanList.stream()
                .map(item -> {
                    PostInfo postInfo = this.postService.getPostInfo(item.getPost(), PostState.PUBLISH, false);
                    return new PostBanInfo(item, postInfo);
                })
                .collect(Collectors.toList());

        return PostBanInfoList.builder()
                .skip(skip)
                .limit(limit)
                .postBanInfoList(postBanInfoList)
                .build();
    }

    private Board getBoard(String id) {
        Optional<Board> boardOptional = this.boardRepository.getBoard(id);
        return boardOptional.orElseThrow(() -> new IricomException(IricomErrorCode.NOT_EXIST_BOARD));
    }

    private Post getPost(String id) {
        Optional<Post> postOptional = this.postRepository.getPost(id);
        return postOptional.orElseThrow(() -> new IricomException(IricomErrorCode.NOT_EXIST_POST));
    }
}
