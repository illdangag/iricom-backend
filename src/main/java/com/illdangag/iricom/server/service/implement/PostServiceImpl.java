package com.illdangag.iricom.server.service.implement;

import com.illdangag.iricom.server.data.entity.*;
import com.illdangag.iricom.server.data.request.PostInfoCreate;
import com.illdangag.iricom.server.data.request.PostInfoSearch;
import com.illdangag.iricom.server.data.request.PostInfoUpdate;
import com.illdangag.iricom.server.data.response.PostInfo;
import com.illdangag.iricom.server.data.response.PostInfoList;
import com.illdangag.iricom.server.exception.IricomErrorCode;
import com.illdangag.iricom.server.exception.IricomException;
import com.illdangag.iricom.server.repository.CommentRepository;
import com.illdangag.iricom.server.repository.PostRepository;
import com.illdangag.iricom.server.repository.PostVoteRepository;
import com.illdangag.iricom.server.service.AccountService;
import com.illdangag.iricom.server.service.BoardAuthorizationService;
import com.illdangag.iricom.server.service.BoardService;
import com.illdangag.iricom.server.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Validated
@Service
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final PostVoteRepository postVoteRepository;
    private final CommentRepository commentRepository;

    private final BoardService boardService;
    private final BoardAuthorizationService boardAuthorizationService;

    @Autowired
    public PostServiceImpl(PostRepository postRepository, PostVoteRepository postVoteRepository, CommentRepository commentRepository,
                           BoardAuthorizationService boardAuthorizationService, BoardService boardService) {
        this.postRepository = postRepository;
        this.postVoteRepository = postVoteRepository;
        this.commentRepository = commentRepository;

        this.boardService = boardService;
        this.boardAuthorizationService = boardAuthorizationService;
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
    public PostInfo createPostInfo(Account account, Board board, @Valid PostInfoCreate postInfoCreate) {
        // ???????????? ??????????????? ????????? ?????? ??????
        if (!board.getEnabled()) {
            throw new IricomException(IricomErrorCode.DISABLED_BOARD_TO_POST);
        }

        // ?????? ????????? ?????? ????????? ????????? ?????? ?????? ????????? ???????????? ?????? ??????
        if (postInfoCreate.getType() == PostType.NOTIFICATION) {
            if (!this.hasAuthorization(account, board)) {
                throw new IricomException(IricomErrorCode.INVALID_AUTHORIZATION_TO_NOTIFICATION);
            }
        }

        Post post = Post.builder()
                .account(account)
                .board(board)
                .build();
        PostContent postContent = PostContent.builder(post, PostState.TEMPORARY)
                .type(postInfoCreate.getType())
                .title(postInfoCreate.getTitle())
                .content(postInfoCreate.getContent())
                .allowComment(postInfoCreate.getIsAllowComment())
                .build();
        post.setTemporaryContent(postContent);

        this.postRepository.save(post, postContent);
        return new PostInfo(post, true, PostState.TEMPORARY, 0, 0, 0);
    }

    @Override
    public PostInfo updatePostInfo(Account account, String boardId, String postId, PostInfoUpdate postInfoUpdate) {
        Board board = this.boardService.getBoard(boardId);
        Post post = this.getPost(postId);
        return this.updatePostInfo(account, board, post, postInfoUpdate);
    }

    @Override
    public PostInfo updatePostInfo(Account account, Board board, Post post, @Valid PostInfoUpdate postInfoUpdate) {
        // ???????????? ??????????????? ????????? ?????? ??????
        if (!board.getEnabled()) {
            throw new IricomException(IricomErrorCode.DISABLED_BOARD_TO_POST);
        }

        // ?????? ????????? ?????? ????????? ????????? ?????? ?????? ????????? ???????????? ?????? ??????
        if (postInfoUpdate.getType() == PostType.NOTIFICATION) {
            if (!this.hasAuthorization(account, board)) {
                throw new IricomException(IricomErrorCode.INVALID_AUTHORIZATION_TO_NOTIFICATION);
            }
        }

        // ????????? ????????? ???????????? ????????? ??????
        if (!post.getAccount().equals(account)) {
            throw new IricomException(IricomErrorCode.INVALID_AUTHORIZATION_TO_UPDATE_POST_OR_NOTIFICATION);
        }

        // ?????? ?????? ????????? ????????? ?????? ?????? ????????? ?????? ?????? ??????
        // ?????? ?????? ????????? ????????? ?????? ?????? ????????? ??????
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
        long commentCount = this.commentRepository.getCommentListSize(post);
        long upvote = this.postVoteRepository.getPostVoteCount(post, VoteType.UPVOTE);
        long downvote = this.postVoteRepository.getPostVoteCount(post, VoteType.DOWNVOTE);

        return new PostInfo(post, true, PostState.TEMPORARY, commentCount, upvote, downvote);
    }

    @Override
    public PostInfo getPostInfo(Account account, String boardId, String postId, PostState postState) {
        Board board = this.boardService.getBoard(boardId);
        Post post = this.getPost(postId);
        return this.getPostInfo(account, board, post, postState);
    }

    @Override
    public PostInfo getPostInfo(Account account, Board board, Post post, PostState postState) {
        if (!board.getEnabled()) {
            throw new IricomException(IricomErrorCode.DISABLED_BOARD_TO_POST);
        }

        if (postState == PostState.TEMPORARY) {
            if (!post.getAccount().equals(account)) {
                // ?????? ????????? ????????? ???????????? ???????????? ????????? ????????? ???????????? ??????
                throw new IricomException(IricomErrorCode.INVALID_AUTHORIZATION_TO_GET_TEMPORARY_CONTENT);
            }
        } else {
            post.setViewCount(post.getViewCount() + 1);
        }

        this.postRepository.save(post);

        long commentCount = this.commentRepository.getCommentListSize(post);
        long upvote = this.postVoteRepository.getPostVoteCount(post, VoteType.UPVOTE);
        long downvote = this.postVoteRepository.getPostVoteCount(post, VoteType.DOWNVOTE);

        return new PostInfo(post, true, postState, commentCount, upvote, downvote);
    }

    @Override
    public PostInfo publishPostInfo(Account account, String boardId, String postId) {
        Board board = this.boardService.getBoard(boardId);
        Post post = this.getPost(postId);
        return this.publishPostInfo(account, board, post);
    }

    @Override
    public PostInfo publishPostInfo(Account account, Board board, Post post) {
        // ???????????? ??????????????? ????????? ?????? ??????
        if (!board.getEnabled()) {
            throw new IricomException(IricomErrorCode.DISABLED_BOARD_TO_POST);
        }

        // ????????? ????????? ???????????? ?????? ??????
        if (!post.getAccount().equals(account)) {
            throw new IricomException(IricomErrorCode.INVALID_AUTHORIZATION_TO_UPDATE_POST_OR_NOTIFICATION);
        }

        // ?????? ????????? ???????????? ?????? ??????
        if (post.getTemporaryContent() == null) {
            throw new IricomException(IricomErrorCode.NOT_EXIST_TEMPORARY_CONTENT);
        }

        // ?????? ????????? ?????? ????????? ????????? ?????? ?????? ????????? ???????????? ?????? ??????
        if (post.getTemporaryContent().getType() == PostType.NOTIFICATION) {
            if (!this.hasAuthorization(account, board)) {
                throw new IricomException(IricomErrorCode.INVALID_AUTHORIZATION_TO_NOTIFICATION);
            }
        }

        // ??????: ?????? ?????? ????????? ???????????? ????????? ?????? ?????? ????????? ??????
        PostContent temporaryPostContent = post.getTemporaryContent();
        if (temporaryPostContent != null) {
            temporaryPostContent.setState(PostState.PUBLISH);
            post.setContent(temporaryPostContent);
            post.setTemporaryContent(null);
        }

        this.postRepository.save(post);
        long commentCount = this.commentRepository.getCommentCount(post);
        long upvote = this.postVoteRepository.getPostVoteCount(post, VoteType.UPVOTE);
        long downvote = this.postVoteRepository.getPostVoteCount(post, VoteType.DOWNVOTE);

        return new PostInfo(post, true, PostState.PUBLISH, commentCount, upvote, downvote);
    }

    @Override
    public PostInfoList getPublishPostInfoList(Board board, @Valid PostInfoSearch postInfoSearch) {
        List<Post> postList;
        long totalPostCount;
        if (postInfoSearch.getTitle().isEmpty()) {
            if (postInfoSearch.getType() != null) {
                // ?????? ??????, ????????? ?????? ??????
                postList = this.postRepository.getPublishPostList(board, postInfoSearch.getType(), postInfoSearch.getSkip(), postInfoSearch.getLimit());
                totalPostCount = this.postRepository.getPublishPostCount(board, postInfoSearch.getType());
            } else {
                // ?????? ??????, ????????? ?????? ??????
                postList = this.postRepository.getPublishPostList(board,  postInfoSearch.getSkip(), postInfoSearch.getLimit());
                totalPostCount = this.postRepository.getPublishPostCount(board);
            }
        } else {
            if (postInfoSearch.getType() != null) {
                // ?????? ??????, ????????? ?????? ??????
                postList = this.postRepository.getPublishPostList(board, postInfoSearch.getType(), postInfoSearch.getTitle(), postInfoSearch.getSkip(), postInfoSearch.getLimit());
                totalPostCount = this.postRepository.getPublishPostCount(board, postInfoSearch.getType(), postInfoSearch.getTitle());
            } else {
                // ?????? ??????, ????????? ?????? ??????
                postList = this.postRepository.getPublishPostList(board, postInfoSearch.getTitle(), postInfoSearch.getSkip(), postInfoSearch.getLimit());
                totalPostCount = this.postRepository.getPublishPostCount(board, postInfoSearch.getTitle());
            }
        }


        List<Account> accountList = postList.stream()
                .map(Post::getAccount)
                .distinct()
                .collect(Collectors.toList());

        List<PostInfo> postInfoList = postList.stream().map(post -> {
            long commentCount = this.commentRepository.getCommentCount(post);
            long upvote = this.postVoteRepository.getPostVoteCount(post, VoteType.UPVOTE);
            long downvote = this.postVoteRepository.getPostVoteCount(post, VoteType.DOWNVOTE);

            return new PostInfo(post, false, PostState.PUBLISH, commentCount, upvote, downvote);
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
        // ???????????? ??????????????? ????????? ?????? ??????
        if (!board.getEnabled()) {
            throw new IricomException(IricomErrorCode.DISABLED_BOARD_TO_POST);
        }

        // ????????? ????????? ???????????? ?????? ??????
        if (!post.getAccount().equals(account)) {
            throw new IricomException(IricomErrorCode.INVALID_AUTHORIZATION_TO_UPDATE_POST_OR_NOTIFICATION);
        }

        // ?????? ????????? ?????? ????????? ????????? ?????? ?????? ????????? ???????????? ??????
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

        if (!post.getDeleted()) {
            post.setDeleted(true);
            this.postRepository.save(post);
        }
        long commentCount = this.commentRepository.getCommentCount(post);
        long upvote = this.postVoteRepository.getPostVoteCount(post, VoteType.UPVOTE);
        long downvote = this.postVoteRepository.getPostVoteCount(post, VoteType.DOWNVOTE);

        PostState responsePostState;
        if (post.isPublish()) {
            responsePostState = PostState.PUBLISH;
        } else {
            responsePostState = PostState.TEMPORARY;
        }

        return new PostInfo(post, true, responsePostState, commentCount, upvote, downvote);
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

        if (!post.isPublish()) {
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

        this.postRepository.save(post);
        this.postVoteRepository.save(postVote);
        long commentCount = this.commentRepository.getCommentListSize(post);
        long upvote = this.postVoteRepository.getPostVoteCount(post, VoteType.UPVOTE);
        long downvote = this.postVoteRepository.getPostVoteCount(post, VoteType.DOWNVOTE);

        return new PostInfo(post, true, PostState.PUBLISH, commentCount, upvote, downvote);
    }

    @Override
    public PostInfoList getPostInfoList(Account account, @Valid PostInfoSearch postInfoSearch) {
        List<Post> postList = this.postRepository.getPostList(account, postInfoSearch.getSkip(), postInfoSearch.getLimit());
        long totalPostCount = this.postRepository.getPostCount(account);

        List<PostInfo> postInfoList = postList.stream().map(post -> {
            long commentCount = this.commentRepository.getCommentCount(post);
            long upvote = this.postVoteRepository.getPostVoteCount(post, VoteType.UPVOTE);
            long downvote = this.postVoteRepository.getPostVoteCount(post, VoteType.DOWNVOTE);
            PostState responsePostState;
            if (post.isPublish()) {
                responsePostState = PostState.PUBLISH;
            } else {
                responsePostState = PostState.TEMPORARY;
            }
            return new PostInfo(post, false, responsePostState, commentCount, upvote, downvote);
        }).collect(Collectors.toList());

        return PostInfoList.builder()
                .total(totalPostCount)
                .skip(postInfoSearch.getSkip())
                .limit(postInfoSearch.getLimit())
                .postInfoList(postInfoList)
                .build();
    }

    /**
     * ????????? ????????? ????????? ?????? ???????????? ????????? ????????? ?????? ???????????? ??????
     */
    private boolean hasAuthorization(Account account, Board board) {
        if (account.getAuth() == AccountAuth.SYSTEM_ADMIN) {
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
