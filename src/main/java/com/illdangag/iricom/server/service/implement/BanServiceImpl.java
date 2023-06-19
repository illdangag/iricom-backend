package com.illdangag.iricom.server.service.implement;

import com.illdangag.iricom.server.data.entity.*;
import com.illdangag.iricom.server.data.request.PostBanCreate;
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
    public PostInfo banPost(Account account, String boardId, String postId, PostBanCreate postBanCreate) {
        Board board = this.getBoard(boardId);
        Post post = this.getPost(postId);
        return this.banPost(account, board, post, postBanCreate);
    }

    @Override
    public PostInfo banPost(Account account, Board board, Post post, PostBanCreate postBanCreate) {
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
                .reason(postBanCreate.getReason())
                .enabled(true)
                .build();
        this.banRepository.savePostBan(postBan);

        return this.postService.getPostInfo(post, PostState.PUBLISH, true);
    }

    @Override
    public PostInfo unbanPost(Account account, String boardId, String postId) {
        Board board = this.getBoard(boardId);
        Post post = this.getPost(postId);
        return this.unbanPost(account, board, post);
    }

    @Override
    public PostInfo unbanPost(Account account, Board board, Post post) {
        if (!this.boardAuthorizationService.hasAuthorization(account, board)) {
            throw new IricomException(IricomErrorCode.INVALID_AUTHORIZATION_TO_BAN_POST);
        }

        if (!post.getBoard().equals(board)) {
            throw new IricomException(IricomErrorCode.NOT_EXIST_POST);
        }

        List<PostBan> postBanList = this.banRepository.getPostBanList(post);
        postBanList.forEach(item -> item.setEnabled(false));
        postBanList.forEach(this.banRepository::savePostBan);

        return this.postService.getPostInfo(post, PostState.PUBLISH, true);
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
