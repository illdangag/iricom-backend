package com.illdangag.iricom.server.service.implement;

import com.illdangag.iricom.server.data.entity.*;
import com.illdangag.iricom.server.data.entity.type.AccountAuth;
import com.illdangag.iricom.server.data.entity.type.PostState;
import com.illdangag.iricom.server.data.entity.type.PostType;
import com.illdangag.iricom.server.data.entity.type.VoteType;
import com.illdangag.iricom.server.data.request.PostInfoCreate;
import com.illdangag.iricom.server.data.request.PostInfoSearch;
import com.illdangag.iricom.server.data.request.PostInfoUpdate;
import com.illdangag.iricom.server.data.response.PostInfo;
import com.illdangag.iricom.server.data.response.PostInfoList;
import com.illdangag.iricom.server.exception.IricomErrorCode;
import com.illdangag.iricom.server.exception.IricomException;
import com.illdangag.iricom.server.repository.*;
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
    private final BoardRepository boardRepository;
    private final BoardAdminRepository boardAdminRepository;
    private final ReportRepository reportRepository;
    private final BanRepository banRepository;

    @Autowired
    public PostServiceImpl(PostRepository postRepository, PostVoteRepository postVoteRepository, CommentRepository commentRepository,
                           BoardRepository boardRepository, BoardAdminRepository boardAdminRepository, ReportRepository reportRepository,
                           BanRepository banRepository) {
        this.postRepository = postRepository;
        this.postVoteRepository = postVoteRepository;
        this.commentRepository = commentRepository;
        this.boardRepository = boardRepository;
        this.boardAdminRepository = boardAdminRepository;
        this.reportRepository = reportRepository;
        this.banRepository = banRepository;
    }

    @Override
    public PostInfo createPostInfo(Account account, String boardId, PostInfoCreate postInfoCreate) {
        Board board = this.getBoard(boardId);
        return this.createPostInfo(account, board, postInfoCreate);
    }

    @Override
    public PostInfo createPostInfo(Account account, Board board, PostInfoCreate postInfoCreate) {
        this.validate(account, board);

        // 사용자의 닉네임을 등록하지 않은 경우 게시물을 작성 할 수 없음
        AccountDetail accountDetail = account.getAccountDetail();
        if (accountDetail == null || accountDetail.getNickname() == null || accountDetail.getNickname().isEmpty()) {
            throw new IricomException(IricomErrorCode.NOT_EXIST_ACCOUNT_NICKNAME_TO_POST);
        }

        // 활성화된 게시판에만 게시물 작성 가능
        if (!board.getEnabled()) {
            throw new IricomException(IricomErrorCode.DISABLED_BOARD);
        }

        // 공지 사항인 경우 시스템 관리자 또는 해당 게시판 관리자만 작성 가능
        if (postInfoCreate.getType() == PostType.NOTIFICATION) {
            if (!this.hasAuthorization(account, board)) {
                throw new IricomException(IricomErrorCode.INVALID_AUTHORIZATION_TO_NOTIFICATION);
            }
        }

        if (postInfoCreate.getType() == PostType.POST && board.getNotificationOnly()) {
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
    public PostInfo updatePostInfo(Account account, String boardId, String postId, PostInfoUpdate postInfoUpdate) {
        Board board = this.getBoard(boardId);
        Post post = this.getPost(postId);
        return this.updatePostInfo(account, board, post, postInfoUpdate);
    }

    @Override
    public PostInfo updatePostInfo(Account account, Board board, Post post, PostInfoUpdate postInfoUpdate) {
        this.validate(account, board, post);

        // 게시판에 존재하는 게시물인지 확인
        if (!board.equals(post.getBoard())) {
            throw new IricomException(IricomErrorCode.NOT_EXIST_POST);
        }

        // 활성화된 게시판에만 게시물 수정 가능
        if (!board.getEnabled()) {
            throw new IricomException(IricomErrorCode.DISABLED_BOARD);
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

    @Override
    public PostInfo getPostInfo(Account account, String postId, PostState postState, boolean includeContent) {
        Post post = this.getPost(postId);
        return this.getPostInfo(account, post, postState, includeContent);
    }

    @Override
    public PostInfo getPostInfo(Account account, Post post, PostState postState, boolean includeContent) {
        Board board = post.getBoard();
        this.validate(account, board, post);

        long commentCount = this.commentRepository.getCommentListSize(post);
        long upvote = this.postVoteRepository.getPostVoteCount(post, VoteType.UPVOTE);
        long downvote = this.postVoteRepository.getPostVoteCount(post, VoteType.DOWNVOTE);
        long reportCount = this.reportRepository.getPortReportCount(post);
        boolean isBan = this.isBanPost(post);

        return new PostInfo(post, includeContent, postState, commentCount, upvote, downvote, reportCount, isBan);
    }

    @Override
    public PostInfo getPostInfo(String postId, PostState postState, boolean includeContent) {
        Post post = this.getPost(postId);
        return this.getPostInfo(post, postState, includeContent);
    }

    @Override
    public PostInfo getPostInfo(Post post, PostState postState, boolean includeContent) {
        Board board = post.getBoard();

        this.validate(null, board);

        long commentCount = this.commentRepository.getCommentListSize(post);
        long upvote = this.postVoteRepository.getPostVoteCount(post, VoteType.UPVOTE);
        long downvote = this.postVoteRepository.getPostVoteCount(post, VoteType.DOWNVOTE);
        long reportCount = this.reportRepository.getPortReportCount(post);
        boolean isBan = this.isBanPost(post);

        return new PostInfo(post, includeContent, postState, commentCount, upvote, downvote, reportCount, isBan);
    }

    @Override
    public PostInfo getPostInfo(Account account, String boardId, String postId, PostState postState) {
        Board board = this.getBoard(boardId);
        Post post = this.getPost(postId);

        return this.getPostInfo(account, board, post, postState);
    }

    @Override
    public PostInfo getPostInfo(Account account, Board board, Post post, PostState postState) {
        this.validate(account, board, post);

        if (!board.getEnabled()) {
            throw new IricomException(IricomErrorCode.DISABLED_BOARD);
        }

        // 게시판에 존재하는 게시물인지 확인
        if (!board.equals(post.getBoard())) {
            throw new IricomException(IricomErrorCode.NOT_EXIST_POST);
        }

        if (postState == PostState.TEMPORARY) {
            if (!post.getAccount().equals(account)) {
                // 임시 저장한 내용을 조회하는 경우에는 본인이 작성한 게시물만 가능
                throw new IricomException(IricomErrorCode.INVALID_AUTHORIZATION_TO_GET_TEMPORARY_CONTENT);
            }
        } else {
            post.setViewCount(post.getViewCount() + 1);
        }

        this.postRepository.save(post);

        return this.getPostInfo(account, post, postState, true);
    }

    @Override
    public PostInfo getPostInfo(String boardId, String postId, PostState postState) {
        Board board = this.getBoard(boardId);
        Post post = this.getPost(postId);

        return this.getPostInfo(board, post, postState);
    }

    @Override
    public PostInfo getPostInfo(Board board, Post post, PostState postState) {
        this.validate(null, board, post);

        if (!board.getEnabled()) {
            throw new IricomException(IricomErrorCode.DISABLED_BOARD);
        }

        // 게시판에 존재하는 게시물인지 확인
        if (!board.equals(post.getBoard())) {
            throw new IricomException(IricomErrorCode.NOT_EXIST_POST);
        }

        if (postState == PostState.TEMPORARY) {
            throw new IricomException(IricomErrorCode.INVALID_AUTHORIZATION_TO_GET_TEMPORARY_CONTENT);
        } else {
            post.setViewCount(post.getViewCount() + 1);
        }

        this.postRepository.save(post);

        return this.getPostInfo(post, postState, true);
    }

    @Override
    public PostInfo publishPostInfo(Account account, String boardId, String postId) {
        Board board = this.getBoard(boardId);
        Post post = this.getPost(postId);
        return this.publishPostInfo(account, board, post);
    }

    @Override
    public PostInfo publishPostInfo(Account account, Board board, Post post) {
        this.validate(account, board, post);

        // 게시판에 포함된 게시물인지 확인
        if (!board.equals(post.getBoard())) {
            throw new IricomException(IricomErrorCode.NOT_EXIST_POST);
        }

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

        // 공지 사항인 경우 시스템 관리자 또는 해당 게시판 관리자만 수정 가능
        if (post.getTemporaryContent().getType() == PostType.NOTIFICATION) {
            if (!this.hasAuthorization(account, board)) {
                throw new IricomException(IricomErrorCode.INVALID_AUTHORIZATION_TO_NOTIFICATION);
            }
        }

        // 발행: 임시 저장 내용을 본문으로 옮기고 임시 저장 내용을 삭제
        PostContent temporaryPostContent = post.getTemporaryContent();
        if (temporaryPostContent != null) {
            temporaryPostContent.setState(PostState.PUBLISH);
            post.setContent(temporaryPostContent);
            post.setTemporaryContent(null);
        }

        this.postRepository.save(post);

        return this.getPostInfo(account, post, PostState.PUBLISH, true);
    }

    @Override
    public PostInfoList getPublishPostInfoList(String boardId, @Valid PostInfoSearch postInfoSearch) {
        Board board = this.getBoard(boardId);
        return this.getPublishPostInfoList(null, board, postInfoSearch);
    }

    @Override
    public PostInfoList getPublishPostInfoList(Account account, String boardId, @Valid PostInfoSearch postInfoSearch) {
        Board board = this.getBoard(boardId);
        return this.getPublishPostInfoList(account, board, postInfoSearch);
    }

    @Override
    public PostInfoList getPublishPostInfoList(Account account, Board board, @Valid PostInfoSearch postInfoSearch) {
        this.validate(account, board);

        List<Post> postList;
        long totalPostCount;

        if (postInfoSearch.getTitle().isEmpty()) {
            if (postInfoSearch.getType() != null) {
                // 제목 없음, 게시물 종류 있음
                postList = this.postRepository.getPublishPostList(board, postInfoSearch.getType(), postInfoSearch.getSkip(), postInfoSearch.getLimit());
                totalPostCount = this.postRepository.getPublishPostCount(board, postInfoSearch.getType());
            } else {
                // 제목 없음, 게시물 종류 없음
                postList = this.postRepository.getPublishPostList(board,  postInfoSearch.getSkip(), postInfoSearch.getLimit());
                totalPostCount = this.postRepository.getPublishPostCount(board);
            }
        } else {
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
                return this.getPostInfo(post, PostState.PUBLISH, false);
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
    public PostInfo deletePostInfo(Account account, String boardId, String postId) {
        Board board = this.getBoard(boardId);
        Post post = this.getPost(postId);
        return this.deletePostInfo(account, board, post);
    }

    @Override
    public PostInfo deletePostInfo(Account account, Board board, Post post) {
        this.validate(account, board, post);

        // 게시판에 포함된 게시물인지 확인
        if (!board.equals(post.getBoard())) {
            throw new IricomException(IricomErrorCode.NOT_EXIST_POST);
        }

        // 활성화된 게시판에만 게시물 삭제 가능
        if (!board.getEnabled()) {
            throw new IricomException(IricomErrorCode.DISABLED_BOARD);
        }

        if (post.getDeleted()) { // 이미 삭제된 게시물인 경우
            // 존재하지 않은 게시물로 간주
            throw new IricomException(IricomErrorCode.NOT_EXIST_POST);
        }

        // 본인이 작성한 게시물만 삭제 가능
        if (!post.getAccount().equals(account)) {
            throw new IricomException(IricomErrorCode.INVALID_AUTHORIZATION_TO_UPDATE_POST_OR_NOTIFICATION);
        }

        PostContent content;
        if (post.getContent() != null) {
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

        if (!post.getDeleted()) {
            post.setDeleted(true);
            this.postRepository.save(post);
        }

        PostState responsePostState;
        if (post.isPublish()) {
            responsePostState = PostState.PUBLISH;
        } else {
            responsePostState = PostState.TEMPORARY;
        }
        return this.getPostInfo(account, post, responsePostState, false);
    }

    public PostInfo votePost(Account account, String boardId, String postId, VoteType voteType) {
        Board board = this.getBoard(boardId);
        Post post = this.getPost(postId);
        return this.votePost(account, board, post, voteType);
    }

    @Override
    public PostInfo votePost(Account account, Board board, Post post, VoteType voteType) {
        this.validate(account, board, post);

        // 게시판에 포함된 게시물인지 확인
        if (!board.equals(post.getBoard())) {
            throw new IricomException(IricomErrorCode.NOT_EXIST_POST);
        }

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
    public PostInfoList getPostInfoList(Account account, PostInfoSearch postInfoSearch) {
        List<Long> accessibleBoardIdList = this.boardRepository.getAccessibleBoardIdList(account);
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
            boolean isBan = this.isBanPost(post);

            return new PostInfo(post, false, responsePostState, commentCount, upvote, downvote, reportCount, isBan);
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

        Optional<BoardAdmin> boardAdminOptional = this.boardAdminRepository.getBoardAdmin(board, account);
        if (boardAdminOptional.isEmpty()) {
            return false;
        }

        BoardAdmin boardAdmin = boardAdminOptional.get();
        return !boardAdmin.getDeleted();
    }

    private Post getPost(String id) {
        Optional<Post> postOptional = this.postRepository.getPost(id);
        return postOptional.orElseThrow(() -> new IricomException(IricomErrorCode.NOT_EXIST_POST));
    }

    private boolean isBanPost(Post post) {
        long postBanCount = this.banRepository.getPostBanCount(post);
        return postBanCount > 0;
    }

    private Board getBoard(String id) {
        long boardId = -1;
        try {
            boardId = Long.parseLong(id);
        } catch (Exception exception) {
            throw new IricomException(IricomErrorCode.NOT_EXIST_BOARD);
        }
        Optional<Board> boardOptional = this.boardRepository.getBoard(boardId);
        return boardOptional.orElseThrow(() -> new IricomException(IricomErrorCode.NOT_EXIST_BOARD));
    }

    private void validate(Account account, Board board) {
        if (board.getUndisclosed()) {
            if (account == null) {
                throw new IricomException(IricomErrorCode.NOT_EXIST_BOARD);
            }

            List<Long> accessibleBoardIdList = this.boardRepository.getAccessibleBoardIdList(account);
            if (!accessibleBoardIdList.contains(board.getId())) {
                throw new IricomException(IricomErrorCode.NOT_EXIST_BOARD);
            }
        }
    }

    private void validate(Account account, Board board, Post post) {
        this.validate(account, board);

        if (!post.getBoard().equals(board)) { // 해당 개시판에서 발행되지 않은 게시물
            throw new IricomException(IricomErrorCode.NOT_EXIST_POST);
        }
    }
}
