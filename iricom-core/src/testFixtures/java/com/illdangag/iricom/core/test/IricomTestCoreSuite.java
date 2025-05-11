package com.illdangag.iricom.core.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.illdangag.iricom.core.data.entity.Account;
import com.illdangag.iricom.core.data.entity.AccountDetail;
import com.illdangag.iricom.core.data.entity.type.*;
import com.illdangag.iricom.core.data.request.*;
import com.illdangag.iricom.core.data.response.*;
import com.illdangag.iricom.core.interceptor.MockAuthInterceptor;
import com.illdangag.iricom.core.repository.AccountRepository;
import com.illdangag.iricom.core.service.*;
import com.illdangag.iricom.core.test.data.wrapper.*;
import com.illdangag.iricom.core.test.util.SearchAllListResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
public abstract class IricomTestCoreSuite {
    protected static final String TEXT_10 = "0123456789";
    protected static final String TEXT_50 = TEXT_10 + TEXT_10 + TEXT_10 + TEXT_10 + TEXT_10;
    protected static final String TEXT_100 = TEXT_50 + TEXT_50;
    protected static final String TEXT_200 = TEXT_100 + TEXT_100;

    private final AccountService accountService;
    private final BoardService boardService;
    private final BoardAuthorizationService boardAuthorizationService;
    private final PostService postService;
    private final CommentService commentService;
    private final ReportService reportService;
    private final BlockService blockService;
    private final AccountGroupService accountGroupService;
    private final PersonalMessageService personalMessageService;

    private final AccountRepository accountRepository;

    private final MockAuthInterceptor mockAuthInterceptor;

    // 계정 설정
    protected static final TestAccountInfo systemAdmin = TestAccountInfo.builder()
            .email("admin@iricom.com").isAdmin(true).nickname("admin").description("system admin").build();

    private static final Map<TestAccountInfo, String> accountTokenMap = new HashMap<>();

    private static boolean isAccountInit = false;

    public IricomTestCoreSuite(ApplicationContext context) {
        this.accountService = context.getBean(AccountService.class);
        this.boardService = context.getBean(BoardService.class);
        this.boardAuthorizationService = context.getBean(BoardAuthorizationService.class);
        this.postService = context.getBean(PostService.class);
        this.commentService = context.getBean(CommentService.class);
        this.reportService = context.getBean(ReportService.class);
        this.blockService = context.getBean(BlockService.class);
        this.accountGroupService = context.getBean(AccountGroupService.class);
        this.personalMessageService = context.getBean(PersonalMessageService.class);

        this.accountRepository = context.getBean(AccountRepository.class);

        this.mockAuthInterceptor = context.getBean(MockAuthInterceptor.class);

        if (isAccountInit) {
            return;
        }

        isAccountInit = true;
        this.createAccount(systemAdmin);
    }

    /**
     * 계정 생성
     */
    protected List<TestAccountInfo> setRandomAccount(int count) {
        List<TestAccountInfo> accountList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            TestAccountInfo testAccountInfo = setRandomAccount();
            accountList.add(testAccountInfo);
        }
        return accountList;
    }

    protected TestAccountInfo setRandomAccount() {
        return setRandomAccount(false);
    }

    protected TestAccountInfo setRandomAccount(boolean isUnregistered) {
        String randomText = UUID.randomUUID().toString();
        String email = randomText + "@iricom.com";
        String nickname = randomText.substring(0, 20);
        String description = randomText;
        TestAccountInfo testAccountInfo = TestAccountInfo.builder()
                .email(email).nickname(nickname).description(description).isUnregistered(isUnregistered).build();
        createAccount(testAccountInfo);
        return testAccountInfo;
    }

    private void createAccount(TestAccountInfo testAccountInfo) {
        Account.AccountBuilder accountBuilder = Account.builder().email(testAccountInfo.getEmail());
        if (testAccountInfo.isAdmin()) {
            accountBuilder.auth(AccountAuth.SYSTEM_ADMIN);
        }
        Account account = accountBuilder.build();
        this.accountRepository.saveAccount(account);

        AccountDetail accountDetail = AccountDetail.builder().account(account).nickname("").description("").build();
        this.accountRepository.saveAccountDetail(accountDetail);
        account.setAccountDetail(accountDetail);
        this.accountRepository.saveAccount(account);

        // 테스트용 토큰 생성
        String token = this.mockAuthInterceptor.setAccount(account);
        accountTokenMap.put(testAccountInfo, token);

        AccountInfo accountInfo = this.accountService.getAccountInfo(String.valueOf(account.getId()));
        testAccountInfo.setId(accountInfo.getId());

        // 기본 정보 등록 여부 처리
        AccountInfoUpdate accountInfoUpdate;
        if (!testAccountInfo.isUnregistered()) {
            accountInfoUpdate = AccountInfoUpdate.builder()
                    .nickname(testAccountInfo.getNickname())
                    .description(testAccountInfo.getDescription())
                    .build();
            this.accountService.updateAccountDetail(accountInfo.getId(), accountInfoUpdate);
        }
    }

    /**
     * 게시판 생성
     */
    protected List<TestBoardInfo> setRandomBoard(int count) {
        List<TestBoardInfo> boardList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            TestBoardInfo testBoardInfo = setRandomBoard();
            boardList.add(testBoardInfo);
        }
        return boardList;
    }

    protected List<TestBoardInfo> setRandomBoard(List<TestAccountInfo> boardAdminTestAccountInfoList, int count) {
        List<TestBoardInfo> boardList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            TestBoardInfo testBoardInfo = setRandomBoard(boardAdminTestAccountInfoList);
            boardList.add(testBoardInfo);
        }
        return boardList;
    }

    protected List<TestBoardInfo> setRandomBoard(List<TestAccountInfo> boardAdminTestAccountInfoList, boolean enabled, boolean unDisclosed, int count) {
        List<TestBoardInfo> boardList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            TestBoardInfo testBoardInfo = setRandomBoard(boardAdminTestAccountInfoList, enabled, unDisclosed);
            boardList.add(testBoardInfo);
        }
        return boardList;
    }

    protected TestBoardInfo setRandomBoard() {
        return setRandomBoard(true, false);
    }

    protected TestBoardInfo setRandomBoard(List<TestAccountInfo> boardAdminTestAccountInfoList) {
        return setRandomBoard(boardAdminTestAccountInfoList, true, false);
    }

    protected TestBoardInfo setRandomBoard(boolean enabled, boolean undisclosed) {
        return setRandomBoard(Collections.emptyList(), enabled, undisclosed);
    }

    protected TestBoardInfo setRandomBoard(List<TestAccountInfo> boardAdminTestAccountInfoList, boolean enabled, boolean undisclosed) {
        String randomText = UUID.randomUUID().toString();
        String title = randomText.substring(0, 20);
        String description = randomText;
        TestBoardInfo testBoardInfo = TestBoardInfo.builder()
                .title(title).description(description)
                .adminList(boardAdminTestAccountInfoList)
                .isEnabled(enabled).undisclosed(undisclosed).build();
        this.setBoard(testBoardInfo);
        return testBoardInfo;
    }

    private void setBoard(TestBoardInfo testBoardInfo) {
        BoardInfo boardInfo = this.createBoard(testBoardInfo);
        testBoardInfo.setId(boardInfo.getId());

        for (TestAccountInfo testAccountInfo : testBoardInfo.getAdminList()) {
            String accountId = testAccountInfo.getId();
            String boardId = boardInfo.getId();
            this.createBoardAdmin(accountId, boardId);
        }
    }

    private BoardInfo createBoard(TestBoardInfo testBoardInfo) {
        BoardInfoCreate boardInfoCreate = BoardInfoCreate.builder()
                .title(testBoardInfo.getTitle())
                .description(testBoardInfo.getDescription())
                .enabled(testBoardInfo.isEnabled())
                .undisclosed(testBoardInfo.isUndisclosed())
                .build();

        return this.boardService.createBoardInfo(systemAdmin.getId(), boardInfoCreate);
    }

    /**
     * 게시물 생성
     */
    protected List<TestPostInfo> setRandomPost(TestBoardInfo testBoardInfo, TestAccountInfo testAccountInfo, int count) {
        List<TestPostInfo> postList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            TestPostInfo testPostInfo = setRandomPost(testBoardInfo, testAccountInfo);
            postList.add(testPostInfo);
        }
        return postList;
    }

    protected TestPostInfo setRandomPost(TestBoardInfo testBoardInfo, TestAccountInfo testAccountInfo) {
        return setRandomPost(testBoardInfo, testAccountInfo, PostType.POST, PostState.PUBLISH);
    }

    protected TestPostInfo setRandomPost(TestBoardInfo testBoardInfo, TestAccountInfo testAccountInfo, PostType postType, PostState postState) {
        return setRandomPost(testBoardInfo, testAccountInfo, postType, postState, true);
    }

    protected TestPostInfo setRandomPost(TestBoardInfo testBoardInfo, TestAccountInfo testAccountInfo, PostType postType, PostState postState, boolean isAllowComment) {
        String randomText = UUID.randomUUID().toString();
        TestPostInfo testPostInfo = TestPostInfo.builder()
                .title(randomText).content(randomText).isAllowComment(isAllowComment)
                .postType(postType).postState(postState)
                .creator(testAccountInfo).board(testBoardInfo)
                .build();
        this.setPost(testPostInfo);
        if (!testPostInfo.isAllowComment()) {
            this.updateDisabledAllowComment(testPostInfo);
        }
        return testPostInfo;
    }

    private void setPost(TestPostInfo testPostInfo) {
        PostInfo postInfo = this.createPost(testPostInfo);
        testPostInfo.setId(postInfo.getId());

        if (testPostInfo.getPostState() == PostState.PUBLISH) {
            this.publishPost(testPostInfo);
        }
    }

    private PostInfo createPost(TestPostInfo testPostInfo) {
        PostInfoCreate postInfoCreate = PostInfoCreate.builder()
                .title(testPostInfo.getTitle())
                .content(testPostInfo.getContent())
                .allowComment(true)
                .type(testPostInfo.getPostType())
                .build();
        return this.postService.createPostInfo(testPostInfo.getCreator().getId(), testPostInfo.getBoard().getId(), postInfoCreate);
    }

    /**
     * 댓글 생성
     */
    protected List<TestCommentInfo> setRandomComment(TestPostInfo testPostInfo, TestCommentInfo referenceComment, TestAccountInfo testAccountInfo, int count) {
        return setRandomComment(testPostInfo, referenceComment, testAccountInfo, false, count);
    }

    protected List<TestCommentInfo> setRandomComment(TestPostInfo testPostInfo, TestAccountInfo testAccountInfo, int count) {
        return setRandomComment(testPostInfo, null, testAccountInfo, false, count);
    }

    protected List<TestCommentInfo> setRandomComment(TestPostInfo testPostInfo, TestCommentInfo referenceComment, TestAccountInfo testAccountInfo, boolean deleted, int count) {
        List<TestCommentInfo> commentList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            TestCommentInfo testCommentInfo = setRandomComment(testPostInfo, referenceComment, testAccountInfo, deleted);
            commentList.add(testCommentInfo);
        }
        return commentList;
    }

    protected TestCommentInfo setRandomComment(TestPostInfo testPostInfo, TestAccountInfo testAccountInfo, boolean deleted) {
        return setRandomComment(testPostInfo, null, testAccountInfo, deleted);
    }

    protected TestCommentInfo setRandomComment(TestPostInfo testPostInfo, TestAccountInfo testAccountInfo) {
        return setRandomComment(testPostInfo, null, testAccountInfo, false);
    }

    protected TestCommentInfo setRandomComment(TestPostInfo testPostInfo, TestCommentInfo referenceComment, TestAccountInfo testAccountInfo) {
        return setRandomComment(testPostInfo, referenceComment, testAccountInfo, false);
    }

    protected TestCommentInfo setRandomComment(TestPostInfo testPostInfo, TestCommentInfo referenceComment, TestAccountInfo testAccountInfo, boolean deleted) {
        String randomText = UUID.randomUUID().toString();
        TestCommentInfo testCommentInfo = TestCommentInfo.builder()
                .post(testPostInfo)
                .referenceComment(referenceComment)
                .creator(testAccountInfo)
                .content(randomText)
                .deleted(deleted)
                .build();
        this.setComment(testCommentInfo);
        return testCommentInfo;
    }

    private void setComment(TestCommentInfo testCommentInfo) {
        CommentInfo commentInfo = this.createComment(testCommentInfo); // 댓글 생성
        testCommentInfo.setId(commentInfo.getId());

        if (testCommentInfo.isDeleted()) {
            this.deleteComment(testCommentInfo);
        }
    }

    private CommentInfo createComment(TestCommentInfo testCommentInfo) {
        TestAccountInfo testAccountInfo = testCommentInfo.getCreator();
        TestPostInfo testPostInfo = testCommentInfo.getPost();
        TestBoardInfo testBoardInfo = testPostInfo.getBoard();
        TestCommentInfo referenceTestCommentInfo = testCommentInfo.getReferenceComment();

        CommentInfoCreate.CommentInfoCreateBuilder builder = CommentInfoCreate.builder()
                .content(testCommentInfo.getContent());
        if (referenceTestCommentInfo != null) {
            builder.referenceCommentId(referenceTestCommentInfo.getId());
        }
        CommentInfoCreate commentInfoCreate = builder.build();

        String boardId = testBoardInfo.getId();
        String postId = testPostInfo.getId();
        return this.commentService.createCommentInfo(testAccountInfo.getId(), boardId, postId, commentInfoCreate);
    }

    /**
     * 게시물 신고
     */
    protected TestPostReportInfo setRandomPostReport(TestPostInfo post, TestAccountInfo reportAccount) {
        return setRandomPostReport(post, reportAccount, ReportType.ETC);
    }

    protected TestPostReportInfo setRandomPostReport(TestPostInfo post, TestAccountInfo reportAccount, ReportType reportType) {
        String randomText = UUID.randomUUID().toString();
        return setRandomPostReport(post, reportAccount, reportType, randomText);
    }

    protected TestPostReportInfo setRandomPostReport(TestPostInfo post, TestAccountInfo reportAccount, ReportType reportType, String reason) {
        TestPostReportInfo testPostReportInfo = TestPostReportInfo.builder()
                .post(post)
                .reportAccount(reportAccount)
                .type(reportType)
                .reason(reason)
                .build();
        this.setPostReport(Collections.singletonList(testPostReportInfo));
        return testPostReportInfo;
    }

    protected void setPostReport(List<TestPostReportInfo> testPostReportInfoList) {
        testPostReportInfoList.forEach(this::setPostReport);
    }

    private void setPostReport(TestPostReportInfo testPostReportInfo) {
        PostReportInfo postReportInfo = this.reportPost(testPostReportInfo);
        testPostReportInfo.setId(postReportInfo.getId());
    }

    /**
     * 댓글 신고
     */
    protected TestCommentReportInfo setRandomCommentReport(TestCommentInfo comment, TestAccountInfo reportAccount) {
        return setRandomCommentReport(comment, reportAccount, ReportType.ETC);
    }

    protected TestCommentReportInfo setRandomCommentReport(TestCommentInfo comment, TestAccountInfo reportAccount, ReportType reportType) {
        String randomText = UUID.randomUUID().toString();
        return setRandomCommentReport(comment, reportAccount, reportType, randomText);
    }

    protected TestCommentReportInfo setRandomCommentReport(TestCommentInfo comment, TestAccountInfo reportAccount, ReportType reportType, String reason) {
        TestCommentReportInfo testCommentReportInfo = TestCommentReportInfo.builder()
                .comment(comment)
                .reportAccount(reportAccount)
                .reason(reason)
                .type(reportType)
                .build();
        this.setCommentReport(testCommentReportInfo);
        return testCommentReportInfo;
    }

    protected void setCommentReport(TestCommentReportInfo testCommentReportInfo) {
        CommentReportInfo commentReportInfo = this.reportComment(testCommentReportInfo);
        testCommentReportInfo.setId(commentReportInfo.getId());
    }

    private CommentReportInfo reportComment(TestCommentReportInfo testCommentReportInfo) {
        TestCommentInfo testCommentInfo = testCommentReportInfo.getComment();

        String boardId = testCommentInfo.getPost().getBoard().getId();
        String postId = testCommentInfo.getPost().getId();
        String commentId = testCommentInfo.getId();
        String accountId = testCommentReportInfo.getReportAccount().getId();

        CommentReportInfoCreate commentReportInfoCreate = CommentReportInfoCreate.builder()
                .type(testCommentReportInfo.getType())
                .reason(testCommentReportInfo.getReason())
                .build();
        return this.reportService.reportComment(accountId, boardId, postId, commentId, commentReportInfoCreate);
    }

    /**
     * 댓글 삭제
     */
    protected void setDeletedComment(List<TestCommentInfo> testCommentInfoList) {
        testCommentInfoList.forEach(this::deleteComment);
    }

    /**
     * 게시물에 댓글 설정 비활성화
     */
    protected void setDisabledCommentPost(List<TestPostInfo> testPostInfoList) {
        testPostInfoList.forEach(this::updateDisabledAllowComment);
    }

    /**
     * 게시판 비활성화
     */
    protected void setDisabledBoard(List<TestBoardInfo> testBoardInfoList) {
        testBoardInfoList.stream()
                .map(TestBoardInfo::getId)
                .forEach(this::disableBoard);
    }

    /**
     * 공자 사항 전용 게시판
     */
    protected void setNotificationOnlyBoard(List<TestBoardInfo> testBoardInfoList) {
        testBoardInfoList.stream()
                .map(TestBoardInfo::getId)
                .forEach(this::notificationOnlyBoard);
    }

    /**
     * 게시물 차단
     */
    protected TestPostBlockInfo setRandomPostBlock(TestPostInfo post) {
        String randomText = UUID.randomUUID().toString();
        return setRandomPostBlock(post, randomText);
    }

    protected TestPostBlockInfo setRandomPostBlock(TestPostInfo post, String reason) {
        TestPostBlockInfo testPostBlockInfo = TestPostBlockInfo.builder()
                .account(systemAdmin).post(post)
                .reason(reason)
                .build();
        this.setBlockPost(Collections.singletonList(testPostBlockInfo));
        return testPostBlockInfo;
    }

    /**
     * 게시물 차단
     */
    protected void setBlockPost(List<TestPostBlockInfo> testPostBlockInfoList) {
        testPostBlockInfoList.forEach(this::blockPost);
    }

    /**
     * 계정 그룹 생성
     */
    protected List<TestAccountGroupInfo> setRandomAccountGroup(int count) {
        return setRandomAccountGroup(Collections.emptyList(), Collections.emptyList(), count);
    }

    protected List<TestAccountGroupInfo> setRandomAccountGroup(List<TestAccountInfo> testAccountInfoList, List<TestBoardInfo> testBoardInfoList, int count) {
        List<TestAccountGroupInfo> accountGroupList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            TestAccountGroupInfo testAccountGroupInfo = setRandomAccountGroup(testAccountInfoList, testBoardInfoList);
            accountGroupList.add(testAccountGroupInfo);
        }
        return accountGroupList;
    }

    protected TestAccountGroupInfo setRandomAccountGroup(List<TestAccountInfo> testAccountInfoList, List<TestBoardInfo> testBoardInfoList) {
        String randomText = UUID.randomUUID().toString();
        String title = randomText.substring(0, 20);
        String description = randomText;
        TestAccountGroupInfo testAccountGroupInfo = TestAccountGroupInfo.builder()
                .title(title).description(description)
                .accountList(testAccountInfoList).boardList(testBoardInfoList)
                .build();
        this.setAccountGroup(testAccountGroupInfo);

        return testAccountGroupInfo;
    }

    protected void setAccountGroup(TestAccountGroupInfo ... testAccountGroupInfoList) {
        for (TestAccountGroupInfo testAccountGroupInfo : testAccountGroupInfoList) {
            setAccountGroup(testAccountGroupInfo);
        }
    }

    private void setAccountGroup(TestAccountGroupInfo testAccountGroupInfo) {
        List<String> accountIdList = testAccountGroupInfo.getAccountList().stream()
                .map(TestAccountInfo::getId)
                .collect(Collectors.toList());

        List<String> boaredIdList = testAccountGroupInfo.getBoardList().stream()
                .map(TestBoardInfo::getId)
                .collect(Collectors.toList());

        AccountGroupInfoCreate accountGroupInfoCreate = AccountGroupInfoCreate.builder()
                .title(testAccountGroupInfo.getTitle())
                .description(testAccountGroupInfo.getDescription())
                .accountIdList(accountIdList)
                .boardIdList(boaredIdList)
                .build();
        AccountGroupInfo accountGroupInfo = this.accountGroupService.createAccountGroupInfo(accountGroupInfoCreate);
        testAccountGroupInfo.setId(accountGroupInfo.getId());
    }

    /**
     * 계정 그룹 삭제
     */
    private void deleteAccountGroup(List<TestAccountGroupInfo> testAccountGroupInfoList) {
        testAccountGroupInfoList.forEach(this::deleteAccountGroup);
    }

    /**
     * 게시판 관리자 삭제
     */
    protected void deleteBoardAdmin(List<TestBoardInfo> testBoardInfoList) {
        testBoardInfoList.forEach(board -> {
            this.deleteBoardAdmin(board, board.getAdminList());
        });
    }

    protected void deleteBoardAdmin(TestBoardInfo testBoardInfo, List<TestAccountInfo> testAccountInfoList) {
        testAccountInfoList.stream()
                .map(TestAccountInfo::getId)
                .forEach(accountId -> {
                    this.deleteBoardAdmin(accountId, testBoardInfo.getId());
                });
    }

    /**
     * 댓글 차단
     */
    protected void setBlockComment(List<TestCommentBlockInfo> testCommentBlockInfoList) {
        testCommentBlockInfoList.forEach(this::blockComment);
    }

    protected List<TestPersonalMessageInfo> setRandomPersonalMessage(TestAccountInfo sender, TestAccountInfo receiver, int count) {
        return setRandomPersonalMessage(sender, receiver, false, false, count);
    }

    protected List<TestPersonalMessageInfo> setRandomPersonalMessage(TestAccountInfo sender, TestAccountInfo receiver, boolean sendDeleted, boolean receiveDelete, int count) {
        List<TestPersonalMessageInfo> personalMessageList = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            TestPersonalMessageInfo testPersonalMessageInfo = setRandomPersonalMessage(sender, receiver, sendDeleted, receiveDelete);
            personalMessageList.add(testPersonalMessageInfo);
        }

        return personalMessageList;
    }

    protected TestPersonalMessageInfo setRandomPersonalMessage(TestAccountInfo sender, TestAccountInfo receiver) {
        return setRandomPersonalMessage(sender, receiver, false, false);
    }

    protected TestPersonalMessageInfo setRandomPersonalMessage(TestAccountInfo sender, TestAccountInfo receiver, boolean sendDeleted, boolean receiveDeleted) {
        String randomText = UUID.randomUUID().toString();
        TestPersonalMessageInfo testPersonalMessageInfo = TestPersonalMessageInfo.builder()
                .sender(sender)
                .receiver(receiver)
                .title(randomText)
                .message(randomText)
                .sendDeleted(sendDeleted)
                .receiveDeleted(receiveDeleted)
                .build();
        PersonalMessageInfo personalMessage = this.createPersonalMessage(testPersonalMessageInfo);
        testPersonalMessageInfo.setId(personalMessage.getId());
        this.deletePersonalMessage(testPersonalMessageInfo);
        return testPersonalMessageInfo;
    }

    /**
     * 개인 쪽지 전송
     */
    protected void createPersonalMessage(List<TestPersonalMessageInfo> testPersonalMessageInfoList) {
        testPersonalMessageInfoList.forEach(item -> {
            this.createPersonalMessage(item);
        });
    }

    /**
     * 개인 쪽지 삭제
     */
    protected void deletePersonalMessage(List<TestPersonalMessageInfo> testPersonalMessageInfoList) {
        testPersonalMessageInfoList.forEach(item -> {
            if (item.isSendDeleted() || item.isReceiveDeleted()) {
                this.deletePersonalMessage(item);
            }
        });
    }

    private void updateAccountDetail(TestAccountInfo testAccountInfo, AccountInfo accountInfo) {
        AccountInfoUpdate accountInfoUpdate = AccountInfoUpdate.builder()
                .nickname(testAccountInfo.getNickname())
                .description(testAccountInfo.getDescription())
                .build();
        this.accountService.updateAccountDetail(accountInfo.getId(), accountInfoUpdate);
    }

    private void createBoardAdmin(String accountId, String boardId) {
        BoardAdminInfoCreate boardAdminInfoCreate = BoardAdminInfoCreate.builder()
                .accountId(accountId)
                .boardId(boardId)
                .build();
        this.boardAuthorizationService.createBoardAdminAuth(boardAdminInfoCreate);
    }

    private void deleteBoardAdmin(String accountId, String boardId) {
        BoardAdminInfoDelete boardAdminInfoDelete = BoardAdminInfoDelete.builder()
                .accountId(accountId)
                .boardId(boardId)
                .build();
        this.boardAuthorizationService.deleteBoardAdminAuth(boardAdminInfoDelete);
    }

    private void disableBoard(String boardId) {
        BoardInfoUpdate boardInfoUpdate = BoardInfoUpdate.builder()
                .enabled(false)
                .build();
        this.boardService.updateBoardInfo(systemAdmin.getId(), boardId, boardInfoUpdate);
    }

    private void notificationOnlyBoard(String boardId) {
        BoardInfoUpdate boardInfoUpdate = BoardInfoUpdate.builder()
                .notificationOnly(true)
                .build();
        this.boardService.updateBoardInfo(systemAdmin.getId(), boardId, boardInfoUpdate);
    }

    protected void updateDisabledAllowComment(TestPostInfo testPostInfo) {
        PostInfoUpdate postInfoUpdate = PostInfoUpdate.builder()
                .allowComment(false)
                .build();
        this.postService.updatePostInfo(testPostInfo.getCreator().getId(), testPostInfo.getBoard().getId(), testPostInfo.getId(), postInfoUpdate);

        if (testPostInfo.getPostState() == PostState.PUBLISH) {
            this.publishPost(testPostInfo);
        }
    }

    private void publishPost(TestPostInfo testPostInfo) {
        this.postService.publishPostInfo(testPostInfo.getCreator().getId(), testPostInfo.getBoard().getId(), testPostInfo.getId());
    }

    private void deleteComment(TestCommentInfo testCommentInfo) {
        TestAccountInfo testAccountInfo = testCommentInfo.getCreator();
        TestPostInfo testPostInfo = testCommentInfo.getPost();
        TestBoardInfo testBoardInfo = testPostInfo.getBoard();

        this.commentService.deleteComment(testAccountInfo.getId(), testBoardInfo.getId(), testPostInfo.getId(), testCommentInfo.getId());
    }


    private PostReportInfo reportPost(TestPostReportInfo testPostReportInfo) {
        TestPostInfo testPostInfo = testPostReportInfo.getPost();

        String boardId = testPostInfo.getBoard().getId();
        String postId = testPostInfo.getId();
        String accountId = testPostReportInfo.getReportAccount().getId();

        PostReportInfoCreate postReportInfoCreate = PostReportInfoCreate.builder()
                .type(testPostReportInfo.getType())
                .reason(testPostReportInfo.getReason())
                .build();
        return this.reportService.reportPost(accountId, boardId, postId, postReportInfoCreate);
    }

    private PostBlockInfo blockPost(TestPostBlockInfo testPostBlockInfo) {
        TestAccountInfo blockTestAccountInfo = testPostBlockInfo.getAccount();
        TestPostInfo testPostInfo = testPostBlockInfo.getPost();

        PostBlockInfoCreate postBlockInfoCreate = PostBlockInfoCreate.builder()
                .reason(testPostBlockInfo.getReason())
                .build();
        return blockService.blockPost(blockTestAccountInfo.getId(), testPostInfo.getBoard().getId(), testPostInfo.getId(), postBlockInfoCreate);
    }

    private CommentBlockInfo blockComment(TestCommentBlockInfo testCommentBlockInfo) {
        TestAccountInfo testAccountInfo = testCommentBlockInfo.getAccount();
        TestCommentInfo testCommentInfo = testCommentBlockInfo.getComment();

        String accountId = testAccountInfo.getId();
        String commentId = testCommentInfo.getId();
        String postId = testCommentInfo.getPost().getId();
        String boardId = testCommentInfo.getPost().getBoard().getId();

        CommentBlockInfoCreate commentBlockInfoCreate = CommentBlockInfoCreate.builder()
                .reason(testCommentBlockInfo.getReason())
                .build();
        return blockService.blockComment(accountId, boardId, postId, commentId, commentBlockInfoCreate);
    }

    protected void setVoteComment(TestCommentInfo comment, TestAccountInfo account, VoteType voteType) {
        TestPostInfo post = comment.getPost();
        TestBoardInfo board = post.getBoard();
        this.commentService.voteComment(account.getId(), board.getId(), post.getId(), comment.getId(), voteType);
    }

    private PersonalMessageInfo createPersonalMessage(TestPersonalMessageInfo testPersonalMessageInfo) {
        String senderId = testPersonalMessageInfo.getSender().getId();
        String receiverId = testPersonalMessageInfo.getReceiver().getId();

        PersonalMessageInfoCreate personalMessageInfoCreate = PersonalMessageInfoCreate.builder()
                .title(testPersonalMessageInfo.getTitle())
                .message(testPersonalMessageInfo.getMessage())
                .receiveAccountId(receiverId)
                .build();

        return this.personalMessageService.createPersonalMessageInfo(senderId, personalMessageInfoCreate);
    }

    private PersonalMessageInfo deletePersonalMessage(TestPersonalMessageInfo testPersonalMessageInfo) {
        PersonalMessageInfo result = null;
        if (testPersonalMessageInfo.isSendDeleted()) {
            String accountId = testPersonalMessageInfo.getSender().getId();
            String personalMessageId = testPersonalMessageInfo.getId();
            result = this.personalMessageService.deletePersonalMessageInfo(accountId, personalMessageId);
        }

        if (testPersonalMessageInfo.isReceiveDeleted()) {
            String accountId = testPersonalMessageInfo.getReceiver().getId();
            String personalMessageId = testPersonalMessageInfo.getId();
            result = this.personalMessageService.deletePersonalMessageInfo(accountId, personalMessageId);
        }

        return result;
    }

    private void deleteAccountGroup(TestAccountGroupInfo testAccountGroupInfo) {
        String accountGroupId = testAccountGroupInfo.getId();
        this.accountGroupService.deleteAccountGroupInfo(accountGroupId);
    }

    protected void setAuthToken(MockHttpServletRequestBuilder builder, TestAccountInfo testAccountInfo) {
        String token = accountTokenMap.get(testAccountInfo);
        builder.header("Authorization", "Bearer " + token);
    }

    protected String getJsonString(Map<String, Object> map) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(map);
    }

    protected <T> T getObject(String jsonString, Class<T> classType) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(jsonString, classType);
    }

    protected <T> List<T> getAllList(SearchRequest searchRequest, SearchAllListResponse<T> response) {
        List<T> resultList = new LinkedList<>();

        final int unit = 100;
        int skip = 0;

        List<T> tempList;
        do {
            searchRequest.setSkip(skip);
            searchRequest.setLimit(unit);
            tempList = response.func(searchRequest);
            resultList.addAll(tempList);
            skip += unit;
        } while (unit <= tempList.size());
        return resultList;
    }
}
