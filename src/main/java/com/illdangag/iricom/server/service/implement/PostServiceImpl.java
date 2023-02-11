package com.illdangag.iricom.server.service.implement;

import com.illdangag.iricom.server.data.entity.*;
import com.illdangag.iricom.server.repository.PostRepository;
import com.illdangag.iricom.server.data.request.PostInfoCreate;
import com.illdangag.iricom.server.data.request.PostInfoSearch;
import com.illdangag.iricom.server.data.request.PostInfoUpdate;
import com.illdangag.iricom.server.data.response.AccountInfo;
import com.illdangag.iricom.server.data.response.PostInfo;
import com.illdangag.iricom.server.data.response.PostInfoList;
import com.illdangag.iricom.server.exception.IricomErrorCode;
import com.illdangag.iricom.server.exception.IricomException;
import com.illdangag.iricom.server.repository.PostVoteRepository;
import com.illdangag.iricom.server.service.AccountService;
import com.illdangag.iricom.server.service.BoardAuthorizationService;
import com.illdangag.iricom.server.service.BoardService;
import com.illdangag.iricom.server.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Validated
@Service
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final PostVoteRepository postVoteRepository;
    private final BoardService boardService;
    private final BoardAuthorizationService boardAuthorizationService;
    private final AccountService accountService;

    @Autowired
    public PostServiceImpl(PostRepository postRepository, PostVoteRepository postVoteRepository,
                           BoardAuthorizationService boardAuthorizationService, BoardService boardService,
                           AccountService accountService) {
        this.postRepository = postRepository;
        this.postVoteRepository = postVoteRepository;
        this.boardService = boardService;
        this.boardAuthorizationService = boardAuthorizationService;
        this.accountService = accountService;
    }

    @Override
    public Post getPost(String id) {
        try {
            return this.getPost(Long.parseLong(id));
        } catch (Exception exception) {
            throw new IricomException(IricomErrorCode.NOT_EXIST_POST);
        }
    }

    @Override
    public Post getPost(long id) {
        Optional<Post> postOptional = this.postRepository.getPost(id);
        return postOptional.orElseThrow(() -> new IricomException(IricomErrorCode.NOT_EXIST_POST));
    }

    @Override
    public PostInfo createPostInfo(Account account, String boardId, PostInfoCreate postInfoCreate) {
        Board board = this.boardService.getBoard(boardId);
        return this.createPostInfo(account, board, postInfoCreate);
    }

    @Override
    @Transactional
    public PostInfo createPostInfo(Account account, Board board, @Valid PostInfoCreate postInfoCreate) {
        // 활성화된 게시판에만 게시물 작성 가능
        if (!board.getEnabled()) {
            throw new IricomException(IricomErrorCode.DISABLED_BOARD_TO_POST);
        }

        // 공지 사항인 경우 시스템 관리자 또는 해당 게시판 관리자만 작성 가능
        if (postInfoCreate.getType() == PostType.NOTIFICATION) {
            if (!this.hasAuthorization(account, board)) {
                throw new IricomException(IricomErrorCode.INVALID_AUTHORIZATION_TO_NOTIFICATION);
            }
        }

        Post post = Post.builder(account, board).build();
        PostContent postContent = PostContent.builder(post, PostState.TEMPORARY)
                .type(postInfoCreate.getType())
                .title(postInfoCreate.getTitle())
                .content(postInfoCreate.getContent())
                .allowComment(postInfoCreate.getIsAllowComment())
                .build();
        post.setTemporaryContent(postContent);
        this.postRepository.save(post, postContent);

        return new PostInfo(post, postContent, PostInfo.Type.INCLUDE_CONTENT);
    }

    @Override
    public PostInfo updatePostInfo(Account account, String boardId, String postId, PostInfoUpdate postInfoUpdate) {
        Board board = this.boardService.getBoard(boardId);
        Post post = this.getPost(postId);
        return this.updatePostInfo(account, board, post, postInfoUpdate);
    }

    @Override
    @Transactional
    public PostInfo updatePostInfo(Account account, Board board, Post post, @Valid PostInfoUpdate postInfoUpdate) {
        // 활성화된 게시판에만 게시물 수정 가능
        if (!board.getEnabled()) {
            throw new IricomException(IricomErrorCode.DISABLED_BOARD_TO_POST);
        }

        // 공지 사항인 경우 시스템 관리자 또는 해당 게시판 관리자만 수정 가능
        if (postInfoUpdate.getType() == PostType.NOTIFICATION) {
            if (!this.hasAuthorization(account, board)) {
                throw new IricomException(IricomErrorCode.INVALID_AUTHORIZATION_TO_NOTIFICATION);
            }
        }

        // 본인이 작성한 게시물만 수정이 가능
        if (!post.getAccount().equals(account)) {
            throw new IricomException(IricomErrorCode.INVALID_AUTHORIZATION_TO_UPDATE_POST_OR_NOTIFICATION);
        }

        // 임시 저장 내용이 없으면 임시 저장 내용을 생성 하여 설정
        // 임시 저장 내용이 있으면 임시 저장 내용을 갱신
        PostContent temporaryPostContent;
        if (post.getTemporaryContent() == null) {
            PostContent content = post.getContent();
            temporaryPostContent = PostContent.builder(post, PostState.TEMPORARY)
                    .type(content.getType())
                    .title(content.getTitle())
                    .content(content.getContent())
                    .allowComment(content.getAllowComment())
                    .build();
            post.setTemporaryContent(temporaryPostContent);
            this.postRepository.save(post, temporaryPostContent);
        } else {
            temporaryPostContent = post.getTemporaryContent();
        }

        if (postInfoUpdate.getType() != null) {
            temporaryPostContent.setType(postInfoUpdate.getType());
        }

        if (postInfoUpdate.getTitle() != null) {
            temporaryPostContent.setTitle(postInfoUpdate.getTitle());
        }
        if (postInfoUpdate.getContent() != null) {
            temporaryPostContent.setContent(postInfoUpdate.getContent());
        }
        if (postInfoUpdate.getIsAllowComment() != null) {
            temporaryPostContent.setAllowComment(postInfoUpdate.getIsAllowComment());
        }

        this.postRepository.save(temporaryPostContent);
        return new PostInfo(post, temporaryPostContent, PostInfo.Type.INCLUDE_CONTENT);
    }

    @Override
    public PostInfo getPostInfo(String boardId, String postId, PostState postState) {
        Board board = this.boardService.getBoard(boardId);
        Post post = this.getPost(postId);
        return this.getPostInfo(board, post, postState);
    }

    @Override
    public PostInfo getPostInfo(Board board, Post post, PostState postState) {
        if (!board.getEnabled()) {
            throw new IricomException(IricomErrorCode.DISABLED_BOARD_TO_POST);
        }

        PostContent postContent;
        if (postState == PostState.TEMPORARY) {
            if (post.getTemporaryContent() == null) {
                throw new IricomException(IricomErrorCode.NOT_EXIST_TEMPORARY_CONTENT);
            }
            postContent = post.getTemporaryContent();
        } else {
            if (post.getContent() == null) {
                throw new IricomException(IricomErrorCode.NOT_EXIST_PUBLISH_CONTENT);
            }
            postContent = post.getContent();
        }

        Account creator = post.getAccount();
        AccountInfo creatorInfo = this.accountService.getAccountInfo(creator);

        return new PostInfo(post, postContent, creatorInfo, PostInfo.Type.INCLUDE_CONTENT);
    }

    @Override
    public PostInfo publishPostInfo(Account account, String boardId, String postId) {
        Board board = this.boardService.getBoard(boardId);
        Post post = this.getPost(postId);
        return this.publishPostInfo(account, board, post);
    }

    @Override
    public PostInfo publishPostInfo(Account account, Board board, Post post) {
        // 활성화된 게시판에만 게시물 발행 가능
        if (!board.getEnabled()) {
            throw new IricomException(IricomErrorCode.DISABLED_BOARD_TO_POST);
        }

        // 본인이 작성한 게시물만 발행 가능
        if (!post.getAccount().equals(account)) {
            throw new IricomException(IricomErrorCode.INVALID_AUTHORIZATION_TO_UPDATE_POST_OR_NOTIFICATION);
        }

        // 공지 사항인 경우 시스템 관리자 또는 해당 게시판 관리자만 수정 가능
        if (post.getTemporaryContent().getType() == PostType.NOTIFICATION) {
            if (!this.hasAuthorization(account, board)) {
                throw new IricomException(IricomErrorCode.INVALID_AUTHORIZATION_TO_NOTIFICATION);
            }
        }

        // 발행: 임시 저장 내용을 본문으로 옮기고 임시 저장 내용을 삭제
        PostContent temporaryPostContent = post.getTemporaryContent();
        if (temporaryPostContent != null) {
            temporaryPostContent.setState(PostState.POST);
            post.setContent(temporaryPostContent);
            post.setTemporaryContent(null);
        }

        this.postRepository.save(post);
        return new PostInfo(post, post.getContent(), PostInfo.Type.INCLUDE_CONTENT);
    }

    @Override
    public PostInfoList getPostInfoList(Board board, @Valid PostInfoSearch postInfoSearch) {
        final PostInfo.Type postInfoType = postInfoSearch.getIncludeContent() ? PostInfo.Type.INCLUDE_CONTENT : PostInfo.Type.SIMPLE;

        List<Post> postList;
        long totalPostCount;
        if (postInfoSearch.getTitle().isEmpty()) {
            postList = this.postRepository.getPublishPostList(board, postInfoSearch.getType(), postInfoSearch.getSkip(), postInfoSearch.getLimit());
            totalPostCount = this.postRepository.getPublishPostCount(board, postInfoSearch.getType());
        } else {
            postList = this.postRepository.getPublishPostList(board, postInfoSearch.getType(), postInfoSearch.getTitle(), postInfoSearch.getSkip(), postInfoSearch.getLimit());
            totalPostCount = this.postRepository.getPublishPostCount(board, postInfoSearch.getType(), postInfoSearch.getTitle());
        }

        List<Account> accountList = postList.stream()
                .map(Post::getAccount)
                .distinct()
                .collect(Collectors.toList());

        Map<Account, AccountInfo> accountAccountInfoMap = this.accountService.getAccountInfoMap(accountList);
        List<PostInfo> postInfoList = postList.stream().map(post -> {
            AccountInfo accountInfo = accountAccountInfoMap.get(post.getAccount());
            return new PostInfo(post, post.getContent(), accountInfo, postInfoType);
        }).collect(Collectors.toList());

        return PostInfoList.builder()
                .total(totalPostCount)
                .skip(postInfoSearch.getSkip())
                .limit(postInfoSearch.getLimit())
                .postInfoList(postInfoList)
                .build();
    }

    @Override
    public PostInfo deletePostInfo(Account account, String boardId, String postId) {
        Board board = this.boardService.getBoard(boardId);
        Post post = this.getPost(postId);
        return this.deletePostInfo(account, board, post);
    }

    @Override
    public PostInfo deletePostInfo(Account account, Board board, Post post) {
        // 활성화된 게시판에만 게시물 발행 가능
        if (!board.getEnabled()) {
            throw new IricomException(IricomErrorCode.DISABLED_BOARD_TO_POST);
        }

        // 공지 사항인 경우 시스템 관리자 또는 해당 게시판 관리자만 삭제
        PostContent content;
        if (post.getContent() != null) {
            content = post.getContent();
        } else {
            content = post.getTemporaryContent();
        }

        if (content.getType() == PostType.NOTIFICATION) {
            if (!this.hasAuthorization(account, board)) {
                throw new IricomException(IricomErrorCode.INVALID_AUTHORIZATION_TO_NOTIFICATION);
            }
        }

        // 본인이 작성한 게시물만 삭제 가능
        if (!post.getAccount().equals(account)) {
            throw new IricomException(IricomErrorCode.INVALID_AUTHORIZATION_TO_UPDATE_POST_OR_NOTIFICATION);
        }

        if (!post.getDeleted()) {
            post.setDeleted(true);
            this.postRepository.save(post);
        }

        return new PostInfo(post, content, PostInfo.Type.INCLUDE_CONTENT);
    }

    public PostInfo votePost(Account account, String boardId, String postId, VoteType voteType) {
        Board board = this.boardService.getBoard(boardId);
        Post post = this.getPost(postId);
        return this.votePost(account, board, post, voteType);
    }

    @Override
    public PostInfo votePost(Account account, Board board, Post post, VoteType voteType) {
        if (!board.equals(post.getBoard())) {
            throw new IricomException(IricomErrorCode.NOT_EXIST_POST);
        }

        if (!board.getEnabled()) {
            throw new IricomException(IricomErrorCode.DISABLED_BOARD_TO_VOTE);
        }

        if (post.getContent() == null) {
            throw new IricomException(IricomErrorCode.NOT_EXIST_PUBLISH_CONTENT);
        }

        Optional<PostVote> postVoteOptional = this.postVoteRepository.getPostVote(account, post, voteType);
        if (postVoteOptional.isPresent()) {
            throw new IricomException(IricomErrorCode.ALREADY_VOTE_POST);
        }

        PostVote postVote = PostVote.builder()
                .post(post)
                .type(voteType)
                .account(account)
                .build();
        if (voteType == VoteType.UPVOTE) {
            long upvoteCount = this.postVoteRepository.getPostVoteCount(post, VoteType.UPVOTE);
            post.setUpvote(upvoteCount + 1);
        } else {
            long downvoteCount = this.postVoteRepository.getPostVoteCount(post, VoteType.DOWNVOTE);
            post.setDownvote(downvoteCount + 1);
        }

        this.postRepository.save(post);
        this.postVoteRepository.save(postVote);

        return new PostInfo(post, post.getContent(), PostInfo.Type.INCLUDE_CONTENT);
    }

    /**
     * 시스템 관리자 이거나 해당 게시판에 관리자 권한이 있는 계정인지 확인
     */
    private boolean hasAuthorization(Account account, Board board) {
        if (account.isAdmin()) {
            return true;
        }

        try {
            this.boardAuthorizationService.getBoardAdmin(account, board);
            return true;
        } catch (Exception exception) {
            return false;
        }
    }
}
