package com.illdangag.iricom.server.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.illdangag.iricom.server.confgiuration.interceptor.MockAuthInterceptor;
import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.data.entity.AccountDetail;
import com.illdangag.iricom.server.data.entity.type.AccountAuth;
import com.illdangag.iricom.server.data.entity.type.PostState;
import com.illdangag.iricom.server.data.entity.type.PostType;
import com.illdangag.iricom.server.data.entity.type.ReportType;
import com.illdangag.iricom.server.data.request.*;
import com.illdangag.iricom.server.data.response.*;
import com.illdangag.iricom.server.repository.AccountRepository;
import com.illdangag.iricom.server.service.*;
import com.illdangag.iricom.server.test.data.wrapper.*;
import com.illdangag.iricom.server.test.util.SearchAllListResponse;
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
public abstract class IricomTestSuite {
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
    protected static final TestAccountInfo allBoardAdmin = TestAccountInfo.builder()
            .email("all-board@iricom.com").nickname("allBoard").description("all board admin.").build();
    protected static final TestAccountInfo enableBoardAdmin = TestAccountInfo.builder()
            .email("enabled@iricom.com").nickname("enabled").description("enable board admin.").build();
    protected static final TestAccountInfo toEnableBoardAdmin = TestAccountInfo.builder()
            .email("to-enabled@iricom.com").nickname("toEnabled").description("to enable board admin.").build();
    protected static final TestAccountInfo disableBoardAdmin = TestAccountInfo.builder()
            .email("disabled@iricom.com").nickname("disable").description("disable board admin.").build();
    protected static final TestAccountInfo toDisableBoardAdmin = TestAccountInfo.builder()
            .email("to-disabled@iricom.com").nickname("toDisabled").description("to disable board admin.").build();
    protected static final TestAccountInfo common00 = TestAccountInfo.builder()
            .email("common00@iricom.com").nickname("commonAccount00").description("this is common00.").build();
    protected static final TestAccountInfo common01 = TestAccountInfo.builder()
            .email("common01@iricom.com").nickname("commonAccount01").description("this is common01.").build();
    protected static final TestAccountInfo common02 = TestAccountInfo.builder()
            .email("common02@iricom.com").nickname("commonAccount02").description("this is common02.").build();
    protected static final TestAccountInfo common03 = TestAccountInfo.builder()
            .email("common03@iricom.com").nickname("commonAccount03").description("this is common03.").build();
    protected static final TestAccountInfo common04 = TestAccountInfo.builder()
            .email("common04@iricom.com").nickname("commonAccount04").description("this is common04.").build();
    protected static final TestAccountInfo common05 = TestAccountInfo.builder()
            .email("common05@iricom.com").nickname("commonAccount05").description("this is common05.").build();
    protected static final TestAccountInfo common06 = TestAccountInfo.builder()
            .email("common06@iricom.com").nickname("commonAccount06").description("this is common06.").build();
    protected static final TestAccountInfo common07 = TestAccountInfo.builder()
            .email("common07@iricom.com").nickname("commonAccount07").description("this is common07.").build();
    protected static final TestAccountInfo common08 = TestAccountInfo.builder()
            .email("common08@iricom.com").nickname("commonAccount08").description("this is common08.").build();
    protected static final TestAccountInfo common09 = TestAccountInfo.builder()
            .email("common09@iricom.com").nickname("commonAccount09").description("this is common09.").build();
    protected static final TestAccountInfo unknown00 = TestAccountInfo.builder()
            .email("unknown00@iriom.com").nickname("").description("").isUnregistered(true).build();
    protected static final TestAccountInfo unknown01 = TestAccountInfo.builder()
            .email("unknown01@iricom.com").nickname("").description("").isUnregistered(true).build();
    protected static final TestAccountInfo unknown02 = TestAccountInfo.builder()
            .email("unknown02@iricom.com").nickname("").description("").isUnregistered(true).build();

    private static final TestAccountInfo[] testAccountInfos = {
            systemAdmin, // 시스템 관리자
            allBoardAdmin, // 모든 게시판 관리자
            enableBoardAdmin, // 활성화 게시판 관리자
            toEnableBoardAdmin, // 게시판 관리자 추가 테스트용 계정
            disableBoardAdmin, // 비활성화 게시판 관리자
            toDisableBoardAdmin, // 게시판 관리자 삭제 테스트용 계정
            common00, common01, common02, common03, common04, common05, common06, common07, common08, common09, // 일반 계정, 게시판 관리자 계정으로 등록 금지
            unknown00, // 등록되지 않은 계정 권한용 테스트 계정, 테스트 케이스에서 계정 정보를 등록 금지
            unknown01, // 등록되지 않은 계정의 정보 수정 테스트용 계정, 테스트 케이스가 수행 되면 등록된 계정으로 전환
            unknown02, // 등록되지 않은 계정의 정보 수정 테스트용 계정, 테스트 케이스가 수행 되면 등록된 계정으로 전환
    };

    private final List<TestBoardInfo> testBoardInfoList = new ArrayList<>();
    private final List<TestAccountGroupInfo> testAccountGroupInfoList = new ArrayList<>();
    private final List<TestPostInfo> testPostInfoList = new ArrayList<>();
    private final List<TestCommentInfo> testCommentInfoList = new ArrayList<>();
    private final List<TestPostReportInfo> testPostReportInfoList = new ArrayList<>();
    private final List<TestCommentReportInfo> testCommentReportInfoList = new ArrayList<>();
    private final List<TestPostBlockInfo> testPostBlockInfoList = new ArrayList<>();
    private final List<TestCommentBlockInfo> testCommentBlockInfoList = new ArrayList<>();
    private final List<TestPersonalMessageInfo> testPersonalMessageInfoList = new ArrayList<>();

    private static final Map<TestAccountInfo, AccountInfo> accountMap = new HashMap<>();
    private static final Map<TestBoardInfo, BoardInfo> boardMap = new HashMap<>();
    private static final Map<TestPostInfo, PostInfo> postMap = new HashMap<>();
    private static final Map<TestAccountInfo, String> tokenMap = new HashMap<>();
    private static final Map<TestCommentInfo, CommentInfo> commentMap = new HashMap<>();
    private static final Map<TestPostReportInfo, PostReportInfo> postReportMap = new HashMap<>();
    private static final Map<TestCommentReportInfo, CommentReportInfo> commentReportMap = new HashMap<>();
    private static final Map<TestPostBlockInfo, PostBlockInfo> postBlockMap = new HashMap<>();
    private static final Map<TestAccountGroupInfo, AccountGroupInfo> accountGroupMap = new HashMap<>();
    private static final Map<TestCommentBlockInfo, CommentBlockInfo> commentBlockMap = new HashMap<>();
    private static final Map<TestPersonalMessageInfo, PersonalMessageInfo> personalMessageMap = new HashMap<>();

    private static final Map<TestAccountInfo, String> accountTokenMap = new HashMap<>();

    private static boolean isAccountInit = false;

    public IricomTestSuite(ApplicationContext context) {
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
        this.setAccount(Arrays.asList(testAccountInfos));
    }

    /**
     * 계정 생성
     */
    protected List<TestAccountInfo> setRandomAccount(int count) {
        List<TestAccountInfo> accountList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            TestAccountInfo testAccountInfo = this.setRandomAccount();
            accountList.add(testAccountInfo);
        }
        return accountList;
    }

    protected TestAccountInfo setRandomAccount() {
        String randomText = UUID.randomUUID().toString();
        String email = randomText + "@iricom.com";
        String nickname = randomText.substring(0, 20);
        String description = randomText;
        TestAccountInfo testAccountInfo = TestAccountInfo.builder()
                .email(email).nickname(nickname).description(description).build();
        setAccount(testAccountInfo);
        return testAccountInfo;
    }

    protected TestAccountInfo setRandomUnregisteredAccount() {
        String randomText = UUID.randomUUID().toString();
        String email = randomText + "@iricom.com";
        String nickname = randomText.substring(0, 20);
        String description = randomText;
        TestAccountInfo testAccountInfo = TestAccountInfo.builder()
                .email(email).nickname(nickname).description(description).isUnregistered(true).build();
        setAccount(testAccountInfo);
        return testAccountInfo;
    }

    protected void setAccount(TestAccountInfo ... testAccountInfos) {
        for (TestAccountInfo testAccountInfo : testAccountInfos) {
            this.setAccount(testAccountInfo);
        }
    }

    protected void setAccount(List<TestAccountInfo> testAccountInfoList) {
        for (TestAccountInfo testAccountInfo : testAccountInfoList) {
            this.setAccount(testAccountInfo);
        }
    }

    protected void setAccount(TestAccountInfo testAccountInfo) {
//        Set<TestAccountInfo> keySet = accountMap.keySet();
//        if (keySet.contains(testAccountInfo)) {
//            return;
//        }

        AccountInfo accountInfo = this.createAccount(testAccountInfo);
        testAccountInfo.setId(accountInfo.getId());
        accountMap.put(testAccountInfo, accountInfo);

        if (!testAccountInfo.isUnregistered()) {
            this.updateAccountDetail(testAccountInfo, accountInfo);
        }
    }

    /**
     * 게시판 생성
     */
    protected List<TestBoardInfo> setRandomBoard(int count) {
        List<TestBoardInfo> boardList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            TestBoardInfo testBoardInfo = this.setRandomBoard();
            boardList.add(testBoardInfo);
        }
        return boardList;
    }

    protected List<TestBoardInfo> setRandomBoard(List<TestAccountInfo> boardAdminTestAccountInfoList, int count) {
        List<TestBoardInfo> boardList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            TestBoardInfo testBoardInfo = this.setRandomBoard(boardAdminTestAccountInfoList);
            boardList.add(testBoardInfo);
        }
        return boardList;
    }

    protected List<TestBoardInfo> setRandomBoard(List<TestAccountInfo> boardAdminTestAccountInfoList, boolean enabled, boolean unDisclosed, int count) {
        List<TestBoardInfo> boardList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            TestBoardInfo testBoardInfo = this.setRandomBoard(boardAdminTestAccountInfoList, enabled, unDisclosed);
            boardList.add(testBoardInfo);
        }
        return boardList;
    }

    protected TestBoardInfo setRandomBoard() {
        String randomText = UUID.randomUUID().toString();
        String title = randomText.substring(0, 20);
        String description = randomText;
        TestBoardInfo testBoardInfo = TestBoardInfo.builder()
                .title(title).description(description).isEnabled(true).build();
        this.setBoard(testBoardInfo);
        return testBoardInfo;
    }

    protected TestBoardInfo setRandomBoard(List<TestAccountInfo> boardAdminTestAccountInfoList) {
        String randomText = UUID.randomUUID().toString();
        String title = randomText.substring(0, 20);
        String description = randomText;
        TestBoardInfo testBoardInfo = TestBoardInfo.builder()
                .title(title).description(description)
                .adminList(boardAdminTestAccountInfoList)
                .isEnabled(true).build();
        this.setBoard(testBoardInfo);
        return testBoardInfo;
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

    protected void setBoard(TestBoardInfo ... testBoardInfos) {
        for (TestBoardInfo testBoardInfo : testBoardInfos) {
            this.setBoard(testBoardInfo);
        }
    }

    protected void setBoard(List<TestBoardInfo> testBoardInfoList) {
        for (TestBoardInfo testBoardInfo : testBoardInfoList) {
            this.setBoard(testBoardInfo);
        }
    }

    private void setBoard(TestBoardInfo testBoardInfo) {
        BoardInfo boardInfo = this.createBoard(testBoardInfo);
        testBoardInfo.setId(boardInfo.getId());
        boardMap.put(testBoardInfo, boardInfo);

        for (TestAccountInfo testAccountInfo : testBoardInfo.getAdminList()) {
            String accountId = testAccountInfo.getId();
            String boardId = boardInfo.getId();
            this.createBoardAdmin(accountId, boardId);
        }
    }

    /**
     * 게시물 생성
     */
    protected List<TestPostInfo> setRandomPost(TestBoardInfo testBoardInfo, TestAccountInfo testAccountInfo, int count) {
        List<TestPostInfo> postList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            TestPostInfo testPostInfo = this.setRandomPost(testBoardInfo, testAccountInfo);
            postList.add(testPostInfo);
        }
        return postList;
    }

    protected TestPostInfo setRandomPost(TestBoardInfo testBoardInfo, TestAccountInfo testAccountInfo) {
        String randomText = UUID.randomUUID().toString();
        TestPostInfo testPostInfo = TestPostInfo.builder()
                .title(randomText).content(randomText).isAllowComment(true)
                .postType(PostType.POST).postState(PostState.PUBLISH)
                .creator(testAccountInfo).board(testBoardInfo)
                .build();
        this.setPost(testPostInfo);
        return testPostInfo;
    }

    protected void setPost(List<TestPostInfo> testPostInfoList) {
        testPostInfoList.forEach(this::setPost);
    }

    protected void setPost(TestPostInfo ... testPostInfoList) {
        for (TestPostInfo testPostInfo : testPostInfoList) {
            this.setPost(testPostInfo);
        }
    }

    private void setPost(TestPostInfo testPostInfo) {
        PostInfo postInfo = this.createPost(testPostInfo);
        testPostInfo.setId(postInfo.getId());

        postMap.put(testPostInfo, postInfo);

        if (testPostInfo.getPostState() == PostState.PUBLISH) {
            this.publishPost(testPostInfo);
        }
    }

    /**
     * 댓글 생성
     */
    protected void setComment(List<TestCommentInfo> testCommentInfoList) {
        testCommentInfoList.forEach(this::setComment);
    }

    protected void setComment(TestCommentInfo ... testCommentInfoList) {
        for (TestCommentInfo testCommentInfo : testCommentInfoList) {
            this.setComment(testCommentInfo);
        }
    }

    private void setComment(TestCommentInfo testCommentInfo) {
        // 상위 댓글이 존재하는지 확인
        Set<TestCommentInfo> keySet = commentMap.keySet();

        TestCommentInfo referenceTestCommentInfo = testCommentInfo.getReferenceComment();
        if (referenceTestCommentInfo != null && !keySet.contains(referenceTestCommentInfo)) { // 상위 댓글이 아직 생성되지 않은 경우
            this.setComment(referenceTestCommentInfo); // 상위 댓글 생성
        }

        CommentInfo commentInfo = this.createComment(testCommentInfo); // 댓글 생성
        testCommentInfo.setId(commentInfo.getId());
        commentMap.put(testCommentInfo, commentInfo);
    }

    /**
     * 게시물 신고
     */
    protected void setPostReport(List<TestPostReportInfo> testPostReportInfoList) {
        for (TestPostReportInfo testPostReportInfo : testPostReportInfoList) {
            PostReportInfo postReportInfo = this.reportPost(testPostReportInfo);
            postReportMap.put(testPostReportInfo, postReportInfo);
        }
    }

    /**
     * 댓글 신고
     */
    protected void setCommentReport(List<TestCommentReportInfo> testCommentReportInfoList) {
        for (TestCommentReportInfo testCommentReportInfo : testCommentReportInfoList) {
            CommentReportInfo commentReportInfo = this.reportComment(testCommentReportInfo);
            commentReportMap.put(testCommentReportInfo, commentReportInfo);
        }
    }

    /**
     * 댓글 삭제
     */
    protected void setDeletedComment(List<TestCommentInfo> testCommentInfoList) {
        testCommentInfoList.stream()
                .filter(TestCommentInfo::isDeleted)
                .forEach(this::deleteComment);
    }

    /**
     * 게시물에 댓글 설정 비활성화
     */
    protected void setDisabledCommentBoard(List<TestPostInfo> testPostInfoList) {
        testPostInfoList.stream()
                .filter(item -> !item.isAllowComment())
                .forEach(this::updateDisabledAllowComment);
    }

    /**
     * 게시판 비활성화
     */
    protected void setDisabledBoard(List<TestBoardInfo> testBoardInfoList) {
        testBoardInfoList.stream()
                .filter(item -> !item.isEnabled())
                .map(boardMap::get)
                .forEach(this::disableBoard);
    }

    /**
     * 공자 사항 전용 게시판
     */
    protected void setNotificationOnlyBoard(List<TestBoardInfo> testBoardInfoList) {
        testBoardInfoList.stream()
                .filter(TestBoardInfo::isNotificationOnly)
                .map(boardMap::get)
                .forEach(this::notificationOnlyBoard);
    }

    /**
     * 게시물 차단
     */
    protected void setBlockPost(List<TestPostBlockInfo> testPostBlockInfoList) {
        testPostBlockInfoList.forEach(item -> {
            PostBlockInfo postBlockInfo = this.blockPost(item);
            postBlockMap.put(item, postBlockInfo);
        });
    }

    /**
     * 계정 그룹 생성
     */
    protected List<TestAccountGroupInfo> setRandomAccountGroup(int count) {
        List<TestAccountGroupInfo> accountGroupList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String randomText = UUID.randomUUID().toString();
            String title = randomText.substring(0, 20);
            String description = randomText;

            TestAccountGroupInfo testAccountGroupInfo = TestAccountGroupInfo.builder()
                    .title(title).description(description).build();
            accountGroupList.add(testAccountGroupInfo);
            this.setAccountGroup(testAccountGroupInfo);
        }
        return accountGroupList;
    }

    protected void setAccountGroup(List<TestAccountGroupInfo> testAccountGroupInfoList) {
        testAccountGroupInfoList.forEach(this::setAccountGroup);
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
        accountGroupMap.put(testAccountGroupInfo, accountGroupInfo);
    }

    /**
     * 계정 그룹 삭제
     */
    private void deleteAccountGroup(List<TestAccountGroupInfo> testAccountGroupInfoList) {
        testAccountGroupInfoList.stream()
                .filter(TestAccountGroupInfo::getDeleted)
                .forEach(this::deleteAccountGroup);
    }

    /**
     * 게시판 관리자 삭제
     */
    private void deleteBoardAdmin(List<TestBoardInfo> testBoardInfoList) {
        testBoardInfoList.forEach(item -> {
            String boardId = this.getBoardId(item);

            item.getRemoveAdminList()
                    .forEach(account -> {
                        String accountId = accountMap.get(account).getId().toString();
                        this.deleteBoardAdmin(accountId, boardId);
                    });
        });
    }

    /**
     * 댓글 차단
     */
    protected void setBlockComment(List<TestCommentBlockInfo> testCommentBlockInfoList) {
        testCommentBlockInfoList.forEach(item -> {
            CommentBlockInfo commentBlockInfo = this.blockComment(item);
            commentBlockMap.put(item, commentBlockInfo);
        });
    }

    /**
     * 개인 쪽지 전송
     */
    protected void createPersonalMessage(List<TestPersonalMessageInfo> testPersonalMessageInfoList) {
        testPersonalMessageInfoList.forEach(item -> {
            PersonalMessageInfo personalMessageInfo = this.createPersonalMessage(item);
            personalMessageMap.put(item, personalMessageInfo);
        });
    }

    /**
     * 개인 쪽지 삭제
     */
    protected void deletePersonalMessage(List<TestPersonalMessageInfo> testPersonalMessageInfoList) {
        testPersonalMessageInfoList.forEach(item -> {
            if (item.isSendDeleted() || item.isReceiveDeleted()) {
                PersonalMessageInfo personalMessageInfo = this.deletePersonalMessage(item);
                personalMessageMap.put(item, personalMessageInfo);
            }
        });
    }

    protected void init() {
        this.setBoard(testBoardInfoList);
        this.setAccountGroup(testAccountGroupInfoList);
        this.setPost(testPostInfoList);
        this.setComment(testCommentInfoList);
        this.setPostReport(testPostReportInfoList);
        this.setCommentReport(testCommentReportInfoList);

        this.setBlockPost(testPostBlockInfoList);
        this.setBlockComment(testCommentBlockInfoList);
        this.setDeletedComment(testCommentInfoList);
        this.setDisabledCommentBoard(testPostInfoList);
        this.setDisabledBoard(testBoardInfoList);
        this.setNotificationOnlyBoard(testBoardInfoList);
        this.deleteAccountGroup(testAccountGroupInfoList);
        this.deleteBoardAdmin(testBoardInfoList);

        this.createPersonalMessage(testPersonalMessageInfoList);
        this.deletePersonalMessage(testPersonalMessageInfoList);
    }

    private AccountInfo createAccount(TestAccountInfo testAccountInfo) {
        Account.AccountBuilder accountBuilder = Account.builder().email(testAccountInfo.getEmail());
        if (testAccountInfo.isAdmin()) {
            accountBuilder.auth(AccountAuth.SYSTEM_ADMIN);
        }
        Account account = accountBuilder.build();
        this.accountRepository.saveAccount(account);

        AccountDetail accountDetail = AccountDetail.builder().account(account).nickname(testAccountInfo.getNickname())
                .description(testAccountInfo.getDescription())
                .build();
        this.accountRepository.saveAccountDetail(accountDetail);
        account.setAccountDetail(accountDetail);
        this.accountRepository.saveAccount(account);

        String token = this.mockAuthInterceptor.setAccount(account);
        accountTokenMap.put(testAccountInfo, token);

        return this.accountService.getAccountInfo(String.valueOf(account.getId()));
    }

    private void updateAccountDetail(TestAccountInfo testAccountInfo, AccountInfo accountInfo) {
        AccountInfoUpdate accountInfoUpdate = AccountInfoUpdate.builder()
                .nickname(testAccountInfo.getNickname())
                .description(testAccountInfo.getDescription())
                .build();
        this.accountService.updateAccountDetail(accountInfo.getId(), accountInfoUpdate);
    }

    private BoardInfo createBoard(TestBoardInfo testBoardInfo) {
        BoardInfoCreate boardInfoCreate = BoardInfoCreate.builder()
                .title(testBoardInfo.getTitle())
                .description(testBoardInfo.getDescription())
                .enabled(true)
                .undisclosed(testBoardInfo.isUndisclosed())
                .build();

        AccountInfo accountInfo = getAccount(systemAdmin);
        return this.boardService.createBoardInfo(accountInfo.getId(), boardInfoCreate);
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

    private void disableBoard(BoardInfo boardInfo) {
        BoardInfoUpdate boardInfoUpdate = BoardInfoUpdate.builder()
                .enabled(false)
                .build();
        AccountInfo accountInfo = getAccount(systemAdmin);
        this.boardService.updateBoardInfo(accountInfo.getId(), boardInfo.getId(), boardInfoUpdate);
    }

    private void notificationOnlyBoard(BoardInfo boardInfo) {
        BoardInfoUpdate boardInfoUpdate = BoardInfoUpdate.builder()
                .notificationOnly(true)
                .build();
        AccountInfo accountInfo = getAccount(systemAdmin);
        this.boardService.updateBoardInfo(accountInfo.getId(), boardInfo.getId(), boardInfoUpdate);
    }

    private PostInfo createPost(TestPostInfo testPostInfo) {
        AccountInfo account = accountMap.get(testPostInfo.getCreator());
        BoardInfo boardInfo = boardMap.get(testPostInfo.getBoard());

        PostInfoCreate postInfoCreate = PostInfoCreate.builder()
                .title(testPostInfo.getTitle())
                .content(testPostInfo.getContent())
                .allowComment(true)
                .type(testPostInfo.getPostType())
                .build();
        return this.postService.createPostInfo(account.getId(), boardInfo.getId(), postInfoCreate);
    }

    private void updateDisabledAllowComment(TestPostInfo testPostInfo) {
        AccountInfo accountInfo = accountMap.get(testPostInfo.getCreator());
        BoardInfo boardInfo = boardMap.get(testPostInfo.getBoard());
        PostInfo postInfo = postMap.get(testPostInfo);

        PostInfoUpdate postInfoUpdate = PostInfoUpdate.builder()
                .allowComment(false)
                .build();
        this.postService.updatePostInfo(accountInfo.getId(), boardInfo.getId(), postInfo.getId(), postInfoUpdate);

        if (testPostInfo.getPostState() == PostState.PUBLISH) {
            this.publishPost(testPostInfo);
        }
    }

    private void publishPost(TestPostInfo testPostInfo) {
        AccountInfo accountInfo = accountMap.get(testPostInfo.getCreator());
        BoardInfo boardInfo = boardMap.get(testPostInfo.getBoard());
        PostInfo postInfo = postMap.get(testPostInfo);

        this.postService.publishPostInfo(accountInfo.getId(), boardInfo.getId(), postInfo.getId());
    }

    private CommentInfo createComment(TestCommentInfo testCommentInfo) {
        TestAccountInfo testAccountInfo = testCommentInfo.getCreator();
        TestPostInfo testPostInfo = testCommentInfo.getPost();
        TestBoardInfo testBoardInfo = testPostInfo.getBoard();
        TestCommentInfo referenceTestCommentInfo = testCommentInfo.getReferenceComment();

        AccountInfo accountInfo = accountMap.get(testAccountInfo);
        BoardInfo boardInfo = boardMap.get(testBoardInfo);
        PostInfo postInfo = postMap.get(testPostInfo);
        CommentInfo referenceCommentInfo = commentMap.get(referenceTestCommentInfo);

        CommentInfoCreate.CommentInfoCreateBuilder builder = CommentInfoCreate.builder()
                .content(testCommentInfo.getContent());
        if (referenceCommentInfo != null) {
            builder.referenceCommentId(referenceCommentInfo.getId());
        }
        CommentInfoCreate commentInfoCreate = builder.build();

        String boardId = boardInfo.getId();
        String postId = postInfo.getId();
        return this.commentService.createCommentInfo(accountInfo.getId(), boardId, postId, commentInfoCreate);
    }

    private void deleteComment(TestCommentInfo testCommentInfo) {
        TestAccountInfo testAccountInfo = testCommentInfo.getCreator();
        TestPostInfo testPostInfo = testCommentInfo.getPost();
        TestBoardInfo testBoardInfo = testPostInfo.getBoard();
        AccountInfo accountInfo = accountMap.get(testAccountInfo);
        BoardInfo boardInfo = boardMap.get(testBoardInfo);
        PostInfo postInfo = postMap.get(testPostInfo);
        CommentInfo commentInfo = commentMap.get(testCommentInfo);

        this.commentService.deleteComment(accountInfo.getId(), boardInfo.getId(), postInfo.getId(), commentInfo.getId());
    }


    private PostReportInfo reportPost(TestPostReportInfo testPostReportInfo) {
        TestAccountInfo reportTestAccountInfo = testPostReportInfo.getReportAccount();
        TestPostInfo testPostInfo = testPostReportInfo.getPost();

        AccountInfo accountInfo = accountMap.get(reportTestAccountInfo);
        PostInfo postInfo = postMap.get(testPostInfo);
        BoardInfo boardInfo = boardMap.get(testPostInfo.getBoard());

        String boardId = boardInfo.getId();
        String postId = postInfo.getId();

        PostReportInfoCreate postReportInfoCreate = PostReportInfoCreate.builder()
                .type(testPostReportInfo.getType())
                .reason(testPostReportInfo.getReason())
                .build();
        return this.reportService.reportPost(accountInfo.getId(), boardId, postId, postReportInfoCreate);
    }


    private CommentReportInfo reportComment(TestCommentReportInfo testCommentReportInfo) {
        TestAccountInfo reportTestAccountInfo = testCommentReportInfo.getReportAccount();
        TestCommentInfo testCommentInfo = testCommentReportInfo.getComment();

        AccountInfo accountInfo = accountMap.get(reportTestAccountInfo);
        CommentInfo commentInfo = commentMap.get(testCommentInfo);
        PostInfo postInfo = postMap.get(testCommentInfo.getPost());
        BoardInfo boardInfo = boardMap.get(testCommentInfo.getPost().getBoard());

        String boardId = boardInfo.getId();
        String postId = postInfo.getId();
        String commentId = commentInfo.getId();

        CommentReportInfoCreate commentReportInfoCreate = CommentReportInfoCreate.builder()
                .type(testCommentReportInfo.getType())
                .reason(testCommentReportInfo.getReason())
                .build();
        return this.reportService.reportComment(accountInfo.getId(), boardId, postId, commentId, commentReportInfoCreate);
    }

    private PostBlockInfo blockPost(TestPostBlockInfo testPostBlockInfo) {
        TestAccountInfo blockTestAccountInfo = testPostBlockInfo.getAccount();
        TestPostInfo testPostInfo = testPostBlockInfo.getPost();

        AccountInfo accountInfo = accountMap.get(blockTestAccountInfo);
        PostInfo postInfo = postMap.get(testPostInfo);
        BoardInfo boardInfo = boardMap.get(testPostInfo.getBoard());

        PostBlockInfoCreate postBlockInfoCreate = PostBlockInfoCreate.builder()
                .reason(testPostBlockInfo.getReason())
                .build();
        return blockService.blockPost(accountInfo.getId(), boardInfo.getId(), postInfo.getId(), postBlockInfoCreate);
    }

    private CommentBlockInfo blockComment(TestCommentBlockInfo testCommentBlockInfo) {
        TestAccountInfo testAccountInfo = testCommentBlockInfo.getAccount();
        TestCommentInfo testCommentInfo = testCommentBlockInfo.getComment();

        AccountInfo accountInfo = accountMap.get(testAccountInfo);
        String commentId = getCommentId(testCommentInfo);
        String postId = getPostId(testCommentInfo.getPost());
        String boardId = getBoardId(testCommentInfo.getPost().getBoard());

        CommentBlockInfoCreate commentBlockInfoCreate = CommentBlockInfoCreate.builder()
                .reason(testCommentBlockInfo.getReason())
                .build();
        return blockService.blockComment(accountInfo.getId(), boardId, postId, commentId, commentBlockInfoCreate);
    }

    private PersonalMessageInfo createPersonalMessage(TestPersonalMessageInfo testPersonalMessageInfo) {

        AccountInfo accountInfo = accountMap.get(testPersonalMessageInfo.getSender());

        PersonalMessageInfoCreate personalMessageInfoCreate = PersonalMessageInfoCreate.builder()
                .title(testPersonalMessageInfo.getTitle())
                .message(testPersonalMessageInfo.getMessage())
                .receiveAccountId(accountMap.get(testPersonalMessageInfo.getReceiver()).getId())
                .build();

        return this.personalMessageService.createPersonalMessageInfo(accountInfo.getId(), personalMessageInfoCreate);
    }

    private PersonalMessageInfo deletePersonalMessage(TestPersonalMessageInfo testPersonalMessageInfo) {
        PersonalMessageInfo result = null;
        if (testPersonalMessageInfo.isSendDeleted()) {
            String accountId = getAccountId(testPersonalMessageInfo.getSender());
            String personalMessageId = getPersonalMessageId(testPersonalMessageInfo);
            result = this.personalMessageService.deletePersonalMessageInfo(accountId, personalMessageId);
        }

        if (testPersonalMessageInfo.isReceiveDeleted()) {
            String accountId = getAccountId(testPersonalMessageInfo.getReceiver());
            String personalMessageId = getPersonalMessageId(testPersonalMessageInfo);
            result = this.personalMessageService.deletePersonalMessageInfo(accountId, personalMessageId);
        }

        return result;
    }

    private void deleteAccountGroup(TestAccountGroupInfo testAccountGroupInfo) {
        AccountGroupInfo accountGroupInfo = getAccountGroup(testAccountGroupInfo);
        String accountGroupId = accountGroupInfo.getId();
        this.accountGroupService.deleteAccountGroupInfo(accountGroupId);
    }

    protected void setAuthToken(MockHttpServletRequestBuilder builder, TestAccountInfo testAccountInfo) throws Exception {
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

    protected AccountInfo getAccount(TestAccountInfo testAccountInfo) {
        return accountMap.get(testAccountInfo);
    }

    protected String getAccountId(TestAccountInfo testAccountInfo) {
        return this.getAccount(testAccountInfo).getId();
    }

    private BoardInfo getBoard(TestBoardInfo testBoardInfo) {
        return boardMap.get(testBoardInfo);
    }

    protected String getBoardId(TestBoardInfo testBoardInfo) {
        return this.getBoard(testBoardInfo).getId();
    }

    private PostInfo getPost(TestPostInfo testPostInfo) {
        return postMap.get(testPostInfo);
    }

    protected String getPostId(TestPostInfo testPostInfo) {
        return this.getPost(testPostInfo).getId();
    }

    private CommentInfo getComment(TestCommentInfo testCommentInfo) {
        return commentMap.get(testCommentInfo);
    }

    protected String getCommentId(TestCommentInfo testCommentInfo) {
        return this.getComment(testCommentInfo).getId();
    }

    private PostReportInfo getPostReport(TestPostReportInfo testPostReportInfo) {
        return postReportMap.get(testPostReportInfo);
    }

    protected String getPostReportId(TestPostReportInfo testPostReportInfo) {
        return this.getPostReport(testPostReportInfo).getId();
    }

    private CommentReportInfo getCommentReport(TestCommentReportInfo testCommentReportInfo) {
        return commentReportMap.get(testCommentReportInfo);
    }

    protected String getCommentReportId(TestCommentReportInfo testCommentReportInfo) {
        return this.getCommentReport(testCommentReportInfo).getId();
    }

    private PostBlockInfo getPostBlock(TestPostBlockInfo testPostBlockInfo) {
        return postBlockMap.get(testPostBlockInfo);
    }

    protected String getPostBlockId(TestPostBlockInfo testPostBlockInfo) {
        return this.getPostBlock(testPostBlockInfo).getId();
    }

    protected AccountGroupInfo getAccountGroup(TestAccountGroupInfo testAccountGroupInfo) {
        return accountGroupMap.get(testAccountGroupInfo);
    }

    protected PersonalMessageInfo getPersonalMessage(TestPersonalMessageInfo testPersonalMessageInfo) {
        return personalMessageMap.get(testPersonalMessageInfo);
    }

    protected String getPersonalMessageId(TestPersonalMessageInfo testPersonalMessageInfo) {
        return personalMessageMap.get(testPersonalMessageInfo).getId();
    }

    protected List<TestPostReportInfo> createTestPostReportInfo(TestPostInfo testPostInfo) {
        return Arrays.asList(
                TestPostReportInfo.builder().type(ReportType.HATE).reason("hate report").reportAccount(common00).post(testPostInfo).build(),
                TestPostReportInfo.builder().type(ReportType.HATE).reason("hate report").reportAccount(common01).post(testPostInfo).build(),
                TestPostReportInfo.builder().type(ReportType.POLITICAL).reason("political report").reportAccount(common02).post(testPostInfo).build(),
                TestPostReportInfo.builder().type(ReportType.POLITICAL).reason("political report").reportAccount(common03).post(testPostInfo).build(),
                TestPostReportInfo.builder().type(ReportType.PORNOGRAPHY).reason("pornography report").reportAccount(common04).post(testPostInfo).build(),
                TestPostReportInfo.builder().type(ReportType.PORNOGRAPHY).reason("pornography report").reportAccount(common05).post(testPostInfo).build(),
                TestPostReportInfo.builder().type(ReportType.ETC).reason("etc report").reportAccount(common06).post(testPostInfo).build(),
                TestPostReportInfo.builder().type(ReportType.ETC).reason("etc report").reportAccount(common07).post(testPostInfo).build(),
                TestPostReportInfo.builder().type(ReportType.ETC).reason("etc report").reportAccount(common08).post(testPostInfo).build(),
                TestPostReportInfo.builder().type(ReportType.ETC).reason("etc report").reportAccount(common09).post(testPostInfo).build()
        );
    }

    protected List<TestCommentReportInfo> createTestCommentReportInfo(TestCommentInfo testCommentInfo) {
        return Arrays.asList(
                TestCommentReportInfo.builder().type(ReportType.HATE).reason("hate report").reportAccount(common00).comment(testCommentInfo).build(),
                TestCommentReportInfo.builder().type(ReportType.HATE).reason("hate").reportAccount(common01).comment(testCommentInfo).build(),
                TestCommentReportInfo.builder().type(ReportType.POLITICAL).reason("political report").reportAccount(common02).comment(testCommentInfo).build(),
                TestCommentReportInfo.builder().type(ReportType.POLITICAL).reason("political report").reportAccount(common03).comment(testCommentInfo).build(),
                TestCommentReportInfo.builder().type(ReportType.PORNOGRAPHY).reason("pornography report").reportAccount(common04).comment(testCommentInfo).build(),
                TestCommentReportInfo.builder().type(ReportType.PORNOGRAPHY).reason("pornography report").reportAccount(common05).comment(testCommentInfo).build(),
                TestCommentReportInfo.builder().type(ReportType.ETC).reason("etc report").reportAccount(common06).comment(testCommentInfo).build(),
                TestCommentReportInfo.builder().type(ReportType.ETC).reason("etc report").reportAccount(common07).comment(testCommentInfo).build(),
                TestCommentReportInfo.builder().type(ReportType.ETC).reason("etc report").reportAccount(common08).comment(testCommentInfo).build(),
                TestCommentReportInfo.builder().type(ReportType.ETC).reason("etc report").reportAccount(common09).comment(testCommentInfo).build()
        );
    }

    protected void addTestBoardInfo(TestBoardInfo ...testBoardInfos) {
        this.addTestBoardInfo(Arrays.asList(testBoardInfos));
    }

    protected void addTestBoardInfo(List<TestBoardInfo> testBoardInfoList) {
        this.testBoardInfoList.addAll(testBoardInfoList);
    }

    protected void addTestAccountGroupInfo(TestAccountGroupInfo ...testAccountGroupInfos) {
        this.addTestAccountGroupInfo(Arrays.asList(testAccountGroupInfos));
    }

    protected void addTestAccountGroupInfo(List<TestAccountGroupInfo> testAccountGroupInfoList) {
        this.testAccountGroupInfoList.addAll(testAccountGroupInfoList);
    }

    protected void addTestPostInfo(TestPostInfo ...testPostInfos) {
        this.addTestPostInfo(Arrays.asList(testPostInfos));
    }

    protected void addTestPostInfo(List<TestPostInfo> testPostInfoList) {
        this.testPostInfoList.addAll(testPostInfoList);
    }

    protected void addTestCommentInfo(TestCommentInfo ...testCommentInfos) {
        this.addTestCommentInfo(Arrays.asList(testCommentInfos));
    }

    protected void addTestCommentInfo(List<TestCommentInfo> testCommentInfoList) {
        this.testCommentInfoList.addAll(testCommentInfoList);
    }

    protected void addTestPostReportInfo(TestPostReportInfo ...testPostReportInfos) {
        this.addTestPostReportInfo(Arrays.asList(testPostReportInfos));
    }

    protected void addTestPostReportInfo(List<TestPostReportInfo> testPostReportInfoList) {
        this.testPostReportInfoList.addAll(testPostReportInfoList);
    }

    protected void addTestCommentReportInfo(TestCommentReportInfo ...testCommentReportInfos) {
        this.addTestCommentReportInfo(Arrays.asList(testCommentReportInfos));
    }

    protected void addTestCommentReportInfo(List<TestCommentReportInfo> testCommentReportInfoList) {
        this.testCommentReportInfoList.addAll(testCommentReportInfoList);
    }

    protected void addTestPostBlockInfo(TestPostBlockInfo... testPostBlockInfos) {
        this.addTestPostBlockInfo(Arrays.asList(testPostBlockInfos));
    }

    protected void addTestPostBlockInfo(List<TestPostBlockInfo> testPostBlockInfoList) {
        this.testPostBlockInfoList.addAll(testPostBlockInfoList);
    }

    protected void addTestCommentBlockInfo(TestCommentBlockInfo... testCommentBlockInfos) {
        this.testCommentBlockInfoList.addAll(Arrays.asList(testCommentBlockInfos));
    }

    protected void addTestPersonalMessageInfo(List<TestPersonalMessageInfo> testPersonalMessageInfoList) {
        this.testPersonalMessageInfoList.addAll(testPersonalMessageInfoList);
    }

    protected void addTestPersonalMessageInfo(TestPersonalMessageInfo... testPersonalMessageInfos) {
        this.addTestPersonalMessageInfo(Arrays.asList(testPersonalMessageInfos));
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
