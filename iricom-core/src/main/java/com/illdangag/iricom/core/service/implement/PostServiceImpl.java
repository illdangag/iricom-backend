package com.illdangag.iricom.core.service.implement;

import com.illdangag.iricom.core.data.entity.*;
import com.illdangag.iricom.core.data.entity.type.*;
import com.illdangag.iricom.core.data.request.PostInfoCreate;
import com.illdangag.iricom.core.data.request.PostInfoSearch;
import com.illdangag.iricom.core.data.request.PostInfoUpdate;
import com.illdangag.iricom.core.data.response.PostInfo;
import com.illdangag.iricom.core.data.response.PostInfoList;
import com.illdangag.iricom.core.exception.IricomErrorCode;
import com.illdangag.iricom.core.exception.IricomException;
import com.illdangag.iricom.core.repository.*;
import com.illdangag.iricom.core.service.AccountPointService;
import com.illdangag.iricom.core.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Validated
@Transactional
@Service
public class PostServiceImpl extends IricomService implements PostService {
    private final PostVoteRepository postVoteRepository;
    private final ReportRepository reportRepository;
    private final AccountPointService accountPointService;

    @Autowired
    public PostServiceImpl(AccountRepository accountRepository, BoardRepository boardRepository,
                           PostRepository postRepository, CommentRepository commentRepository,
                           PostVoteRepository postVoteRepository, ReportRepository reportRepository,
                           AccountPointService accountPointService) {
        super(accountRepository, boardRepository, postRepository, commentRepository);
        this.postVoteRepository = postVoteRepository;
        this.reportRepository = reportRepository;
        this.accountPointService = accountPointService;
    }

    @Override
    public PostInfo createPostInfo(String accountId, String boardId, PostInfoCreate postInfoCreate) {
        Account account = this.getAccount(accountId);
        return this.createPostInfo(account, boardId, postInfoCreate);
    }

    /**
     * 게시물 생성
     */
    @Override
    public PostInfo createPostInfo(Account account, String boardId, PostInfoCreate postInfoCreate) {
        Board board = this.getBoard(boardId);
        return this.createPostInfo(account, board, postInfoCreate);
    }

    /**
     * 게시물 생성
     */
    @Override
    public PostInfo createPostInfo(Account account, Board board, PostInfoCreate postInfoCreate) {
        this.validate(account, board);

        if (account.getAuth() == AccountAuth.UNREGISTERED_ACCOUNT) { // 등록 되지 않은 계정
            throw new IricomException(IricomErrorCode.NOT_EXIST_ACCOUNT_NICKNAME_TO_POST);
        }

        if (!board.getEnabled()) { // 비활성화 게시판인 경우
            throw new IricomException(IricomErrorCode.DISABLED_BOARD);
        }

        if (postInfoCreate.getType() == PostType.NOTIFICATION && !this.hasAuthorization(account, board)) { // 게시물이 공지 사항이면서, 작성자가 시스템 관리자 또는 게시판 관리자가 아닌 경우
            throw new IricomException(IricomErrorCode.INVALID_AUTHORIZATION_TO_NOTIFICATION);
        }

        if (postInfoCreate.getType() == PostType.POST && board.getNotificationOnly()) { // 게시물이 일반 게시물이면서 게시판이 공지 사항 전용인 경우
            throw new IricomException(IricomErrorCode.INVALID_AUTHORIZATION_TO_POST_ONLY_NOTIFICATION_BOARD);
        }

        Post post = Post.builder()
                .account(account)
                .board(board)
                .build();
        PostContent postContent = PostContent.builder(post, PostState.TEMPORARY)
                .type(postInfoCreate.getType())
                .title(postInfoCreate.getTitle())
                .content(postInfoCreate.getContent())
                .allowComment(postInfoCreate.getAllowComment())
                .build();
        post.setTemporaryContent(postContent);

        this.postRepository.save(post, postContent);
        return new PostInfo(post, true, PostState.TEMPORARY, 0, 0, 0, 0, false);
    }

    @Override
    public PostInfo updatePostInfo(String accountId, String boardId, String postId, PostInfoUpdate postInfoUpdate) {
        Account account = this.getAccount(accountId);
        return this.updatePostInfo(account, boardId, postId, postInfoUpdate);
    }

    /**
     * 게시물 수정
     */
    @Override
    public PostInfo updatePostInfo(Account account, String boardId, String postId, PostInfoUpdate postInfoUpdate) {
        Board board = this.getBoard(boardId);
        Post post = this.getPost(postId);

        return this.updatePostInfo(account, board, post, postInfoUpdate);
    }

    /**
     * 게시물 수정
     */
    @Override
    public PostInfo updatePostInfo(Account account, Board board, Post post, PostInfoUpdate postInfoUpdate) {
        this.validate(account, board, post);

        if (account.getAuth() == AccountAuth.UNREGISTERED_ACCOUNT) { // 등록 되지 않은 계정
            throw new IricomException(IricomErrorCode.NOT_EXIST_ACCOUNT_NICKNAME_TO_POST);
        }

        if (!board.getEnabled()) { // 비활성화 게시판인 경우
            throw new IricomException(IricomErrorCode.DISABLED_BOARD);
        }

        if (!post.getAccount().equals(account)) { // 본인이 작성한 게시물만 수정이 가능
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
        } else {
            temporaryPostContent = post.getTemporaryContent();
        }

        PostType updatePostType = postInfoUpdate.getType();
        if (board.getNotificationOnly()) { // 공지 사항 전용 게시판의 게시물인 경우
            // 현재 일반 게시물로 작성된 게시물을 갱신 하거나 공지 사항 게시물을 일반 게시물로 변경 할 수 없음
            if (updatePostType == PostType.POST || temporaryPostContent.getType() == PostType.POST && updatePostType == null) {
                throw new IricomException(IricomErrorCode.INVALID_UPDATE_POST_IN_NOTIFICATION_ONLY_BOARD);
            }
        }

        if (updatePostType == PostType.NOTIFICATION) { // 공지 사항인 경우
            // 시스템 관리자 또는 해당 게시판 관리자만 수정 가능
            if (!this.hasAuthorization(account, board)) {
                throw new IricomException(IricomErrorCode.INVALID_AUTHORIZATION_TO_NOTIFICATION);
            }
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

        if (postInfoUpdate.getAllowComment() != null) {
            temporaryPostContent.setAllowComment(postInfoUpdate.getAllowComment());
        }

        this.postRepository.save(post, temporaryPostContent);

        return this.getPostInfo(account, post, PostState.TEMPORARY, true);
    }

    /**
     * 게시물 조회
     */
    @Override
    public PostInfo getPostInfo(Account account, Post post, PostState postState, boolean includeContent) {
        Board board = post.getBoard();
        this.validate(account, board, post);

        long commentCount = this.commentRepository.getCommentListSize(post);
        long upvote = this.postVoteRepository.getPostVoteCount(post, VoteType.UPVOTE);
        long downvote = this.postVoteRepository.getPostVoteCount(post, VoteType.DOWNVOTE);
        long reportCount = this.reportRepository.getPortReportCount(post);
        boolean blocked = this.isBlockPost(post);

        return new PostInfo(post, includeContent, postState, commentCount, upvote, downvote, reportCount, blocked);
    }

    /**
     * 게시물 조회
     */
    @Override
    public PostInfo getPostInfo(String boardId, String postId, PostState postState, boolean includeContent) {
        Board board = this.getBoard(boardId);
        Post post = this.getPost(postId);

        return this.getPostInfo(board, post, postState, includeContent);
    }

    /**
     * 게시물 조회
     */
    @Override
    public PostInfo getPostInfo(Board board, Post post, PostState postState, boolean includeContent) {
        this.validate(null, board);

        long commentCount = this.commentRepository.getCommentListSize(post);
        long upvote = this.postVoteRepository.getPostVoteCount(post, VoteType.UPVOTE);
        long downvote = this.postVoteRepository.getPostVoteCount(post, VoteType.DOWNVOTE);
        long reportCount = this.reportRepository.getPortReportCount(post);
        boolean blocked = this.isBlockPost(post);

        return new PostInfo(post, includeContent, postState, commentCount, upvote, downvote, reportCount, blocked);
    }

    /**
     * 게시물 조회
     */
    @Override
    public PostInfo getPostInfo(Account account, String boardId, String postId, PostState postState) {
        Board board = this.getBoard(boardId);
        Post post = this.getPost(postId);

        return this.getPostInfo(account, board, post, postState);
    }

    /**
     * 게시물 조회
     */
    @Override
    public PostInfo getPostInfo(Account account, Board board, Post post, PostState postState) {
        this.validate(account, board, post);

        if (!board.getEnabled()) { // 비활성화 게시판인 경우
            throw new IricomException(IricomErrorCode.DISABLED_BOARD);
        }

        if (postState == PostState.TEMPORARY) { // 임시 저장 내용을 조회 하는 경우
            if (!post.getAccount().equals(account)) { // 본인이 작성한 게시물이 아닌 경우
                throw new IricomException(IricomErrorCode.INVALID_AUTHORIZATION_TO_GET_TEMPORARY_CONTENT);
            }
        } else {
            post.setViewCount(post.getViewCount() + 1);
        }

        this.postRepository.save(post);
        return this.getPostInfo(account, post, postState, true);
    }

    /**
     * 게시물 조회
     */
    @Override
    public PostInfo getPostInfo(String boardId, String postId, PostState postState) {
        Board board = this.getBoard(boardId);
        Post post = this.getPost(postId);

        return this.getPostInfo(board, post, postState);
    }

    /**
     * 게시물 조회
     */
    @Override
    public PostInfo getPostInfo(Board board, Post post, PostState postState) {
        this.validate(null, board, post);

        if (!board.getEnabled()) {
            throw new IricomException(IricomErrorCode.DISABLED_BOARD);
        }

        if (postState == PostState.TEMPORARY) { // 임시 저장 내용을 조회 하는 경우
            throw new IricomException(IricomErrorCode.INVALID_AUTHORIZATION_TO_GET_TEMPORARY_CONTENT);
        }

        post.setViewCount(post.getViewCount() + 1);
        this.postRepository.save(post);
        return this.getPostInfo(board, post, postState, true);
    }

    @Override
    public PostInfo publishPostInfo(String accountId, String boardId, String postId) {
        Account account = this.getAccount(accountId);
        return this.publishPostInfo(account, boardId, postId);
    }

    /**
     * 게시물 발행
     */
    @Override
    public PostInfo publishPostInfo(Account account, String boardId, String postId) {
        Board board = this.getBoard(boardId);
        Post post = this.getPost(postId);
        return this.publishPostInfo(account, board, post);
    }

    /**
     * 게시물 발행
     */
    @Override
    public PostInfo publishPostInfo(Account account, Board board, Post post) {
        this.validate(account, board, post);

        // 활성화된 게시판에만 게시물 발행 가능
        if (!board.getEnabled()) {
            throw new IricomException(IricomErrorCode.DISABLED_BOARD);
        }

        // 본인이 작성한 게시물만 발행 가능
        if (!post.getAccount().equals(account)) {
            throw new IricomException(IricomErrorCode.INVALID_AUTHORIZATION_TO_UPDATE_POST_OR_NOTIFICATION);
        }

        // 임시 저장한 게시물이 없는 경우
        if (post.getTemporaryContent() == null) {
            throw new IricomException(IricomErrorCode.NOT_EXIST_TEMPORARY_CONTENT);
        }

        // 공지 사항인 경우 시스템 관리자 또는 해당 게시판 관리자만 발행 가능
        if (post.getTemporaryContent().getType() == PostType.NOTIFICATION) {
            if (!this.hasAuthorization(account, board)) {
                throw new IricomException(IricomErrorCode.INVALID_AUTHORIZATION_TO_NOTIFICATION);
            }
        }

        // 임시 저장 내용을 본문으로 옮기고 임시 저장 내용을 삭제
        PostContent temporaryPostContent = post.getTemporaryContent();
        temporaryPostContent.setState(PostState.PUBLISH);
        post.setContent(temporaryPostContent);
        post.setTemporaryContent(null);

        this.postRepository.save(post);

        this.accountPointService.addAccountPoint(account, AccountPointType.PUBLISH_POST);
        return this.getPostInfo(account, post, PostState.PUBLISH, true);
    }

    /**
     * 발행된 게시물 목록 조회
     */
    @Override
    public PostInfoList getPublishPostInfoList(String boardId, @Valid PostInfoSearch postInfoSearch) {
        Board board = this.getBoard(boardId);

        return this.getPublishPostInfoList(null, board, postInfoSearch);
    }

    @Override
    public PostInfoList getPublishPostInfoList(String accountId, String boardId, @Valid PostInfoSearch postInfoSearch) {
        Account account = this.getAccount(accountId);
        return this.getPublishPostInfoList(account, boardId, postInfoSearch);
    }

    /**
     * 발행된 게시물 목록 조회
     */
    @Override
    public PostInfoList getPublishPostInfoList(Account account, String boardId, @Valid PostInfoSearch postInfoSearch) {
        Board board = this.getBoard(boardId);
        return this.getPublishPostInfoList(account, board, postInfoSearch);
    }

    /**
     * 발행된 게시물 목록 조회
     */
    @Override
    public PostInfoList getPublishPostInfoList(Account account, Board board, @Valid PostInfoSearch postInfoSearch) {
        this.validate(account, board);

        List<Post> postList;
        long totalPostCount;

        if (postInfoSearch.getTitle().isEmpty()) { // 검색어가 존재하지 않는 경우
            if (postInfoSearch.getType() != null) {
                // 제목 없음, 게시물 종류 있음
                postList = this.postRepository.getPublishPostList(board, postInfoSearch.getType(), postInfoSearch.getSkip(), postInfoSearch.getLimit());
                totalPostCount = this.postRepository.getPublishPostCount(board, postInfoSearch.getType());
            } else {
                // 제목 없음, 게시물 종류 없음
                postList = this.postRepository.getPublishPostList(board, postInfoSearch.getSkip(), postInfoSearch.getLimit());
                totalPostCount = this.postRepository.getPublishPostCount(board);
            }
        } else { // 검색어가 존재하는 경우
            if (postInfoSearch.getType() != null) {
                // 제목 있음, 게시물 종류 있음
                postList = this.postRepository.getPublishPostList(board, postInfoSearch.getType(), postInfoSearch.getTitle(), postInfoSearch.getSkip(), postInfoSearch.getLimit());
                totalPostCount = this.postRepository.getPublishPostCount(board, postInfoSearch.getType(), postInfoSearch.getTitle());
            } else {
                // 제목 있음, 게시물 종류 없음
                postList = this.postRepository.getPublishPostList(board, postInfoSearch.getTitle(), postInfoSearch.getSkip(), postInfoSearch.getLimit());
                totalPostCount = this.postRepository.getPublishPostCount(board, postInfoSearch.getTitle());
            }
        }

        List<PostInfo> postInfoList = postList.stream().map(post -> {
            if (account != null) {
                return this.getPostInfo(account, post, PostState.PUBLISH, false);
            } else {
                return this.getPostInfo(board, post, PostState.PUBLISH, false);
            }
        }).collect(Collectors.toList());

        return PostInfoList.builder()
                .total(totalPostCount)
                .skip(postInfoSearch.getSkip())
                .limit(postInfoSearch.getLimit())
                .postInfoList(postInfoList)
                .build();
    }

    @Override
    public PostInfo deletePostInfo(String accountId, String boardId, String postId) {
        Account account = this.getAccount(accountId);
        return this.deletePostInfo(account, boardId, postId);
    }

    /**
     * 게시물 삭제
     */
    @Override
    public PostInfo deletePostInfo(Account account, String boardId, String postId) {
        Board board = this.getBoard(boardId);
        Post post = this.getPost(postId);

        return this.deletePostInfo(account, board, post);
    }

    /**
     * 게시물 삭제
     */
    @Override
    public PostInfo deletePostInfo(Account account, Board board, Post post) {
        this.validate(account, board, post);

        if (!board.getEnabled()) {
            throw new IricomException(IricomErrorCode.DISABLED_BOARD);
        }

        if (post.getDeleted()) { // 이미 삭제된 게시물인 경우
            throw new IricomException(IricomErrorCode.NOT_EXIST_POST);
        }

        if (!post.getAccount().equals(account)) { // 본인이 작성한 게시물이 아닌 경우
            throw new IricomException(IricomErrorCode.INVALID_AUTHORIZATION_TO_UPDATE_POST_OR_NOTIFICATION);
        }

        PostContent content;
        if (post.isPublish()) {
            content = post.getContent();
        } else {
            content = post.getTemporaryContent();
        }

        // 공지 사항인 경우 시스템 관리자 또는 해당 게시판 관리자만 삭제
        if (content.getType() == PostType.NOTIFICATION) {
            if (!this.hasAuthorization(account, board)) {
                throw new IricomException(IricomErrorCode.INVALID_AUTHORIZATION_TO_NOTIFICATION);
            }
        }

        post.setDeleted(true);
        this.postRepository.save(post);

        PostState responsePostState;
        if (post.isPublish()) {
            responsePostState = PostState.PUBLISH;
        } else {
            responsePostState = PostState.TEMPORARY;
        }

        return this.getPostInfo(account, post, responsePostState, false);
    }

    /**
     * 게시물 추천 비추천
     */
    @Override
    public PostInfo votePost(Account account, String boardId, String postId, VoteType voteType) {
        Board board = this.getBoard(boardId);
        Post post = this.getPost(postId);

        return this.votePost(account, board, post, voteType);
    }

    /**
     * 게시물 추천 비추천
     */
    @Override
    public PostInfo votePost(Account account, Board board, Post post, VoteType voteType) {
        this.validate(account, board, post);

        if (!board.getEnabled()) {
            throw new IricomException(IricomErrorCode.DISABLED_BOARD);
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

        return this.getPostInfo(account, post, PostState.PUBLISH, true);
    }

    @Override
    public PostInfoList getPostInfoList(String accountId, PostInfoSearch postInfoSearch) {
        Account account = this.getAccount(accountId);
        return this.getPostInfoList(account, postInfoSearch);
    }

    /**
     * 게시물 목록 조회
     */
    @Override
    public PostInfoList getPostInfoList(Account account, PostInfoSearch postInfoSearch) {
        List<Board> accessibleBoardList = this.boardRepository.getBoardList(account, null, null, null, null);
        List<Long> accessibleBoardIdList = accessibleBoardList.stream()
                .map(Board::getId)
                .collect(Collectors.toList());
        List<Post> postList = this.postRepository.getPostList(account, accessibleBoardIdList, postInfoSearch.getSkip(), postInfoSearch.getLimit());
        long totalPostCount = this.postRepository.getPostCount(account, accessibleBoardIdList);

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

            long reportCount = this.reportRepository.getPortReportCount(post);
            boolean blocked = this.isBlockPost(post);

            // 게시물의 목록을 조회 하므로 게시물의 내용을 포함 하지 않음
            return new PostInfo(post, false, responsePostState, commentCount, upvote, downvote, reportCount, blocked);
        }).collect(Collectors.toList());

        return PostInfoList.builder()
                .total(totalPostCount)
                .skip(postInfoSearch.getSkip())
                .limit(postInfoSearch.getLimit())
                .postInfoList(postInfoList)
                .build();
    }

    /**
     * 시스템 관리자 이거나 해당 게시판에 관리자 권한이 있는 계정인지 확인
     */
    private boolean hasAuthorization(Account account, Board board) {
        if (account.getAuth() == AccountAuth.SYSTEM_ADMIN) {
            return true;
        }

        List<BoardAdmin> boardAdminList = this.boardRepository.getBoardAdminList(board, account);
        return !boardAdminList.isEmpty();
    }

    /**
     * 게시물의 차단 여부
     */
    private boolean isBlockPost(Post post) {
        return post.getPostBlock() != null;
    }
}
