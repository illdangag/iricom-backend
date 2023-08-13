package com.illdangag.iricom.server.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.illdangag.iricom.server.data.entity.*;
import com.illdangag.iricom.server.data.request.*;
import com.illdangag.iricom.server.data.response.*;
import com.illdangag.iricom.server.exception.IricomErrorCode;
import com.illdangag.iricom.server.exception.IricomException;
import com.illdangag.iricom.server.repository.*;
import com.illdangag.iricom.server.service.*;
import com.illdangag.iricom.server.test.data.FirebaseTokenResponse;
import com.illdangag.iricom.server.test.data.wrapper.*;
import com.illdangag.iricom.server.test.util.FirebaseUtils;
import com.illdangag.iricom.server.test.util.SearchAllListResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.util.StopWatch;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
public abstract class IricomTestSuite {
    private final AccountService accountService;
    private final BoardService boardService;
    private final BoardAuthorizationService boardAuthorizationService;
    private final PostService postService;
    private final CommentService commentService;
    private final ReportService reportService;
    private final BanService banService;
    private final AccountGroupService accountGroupService;

    private final AccountRepository accountRepository;
    private final BoardRepository boardRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final ReportRepository reportRepository;
    private final BanRepository banRepository;
    private final AccountGroupRepository accountGroupRepository;

    // 계정 설정
    private static final String ACCOUNT_PASSWORD = "111111";

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
    private final List<TestPostBanInfo> testPostBanInfoList = new ArrayList<>();

    private static final Map<TestAccountInfo, Account> accountMap = new HashMap<>();
    private static final Map<TestBoardInfo, Board> boardMap = new HashMap<>();
    private static final Map<TestPostInfo, Post> postMap = new HashMap<>();
    private static final Map<TestAccountInfo, String> tokenMap = new HashMap<>();
    private static final Map<TestCommentInfo, Comment> commentMap = new HashMap<>();
    private static final Map<TestPostReportInfo, PostReport> postReportMap = new HashMap<>();
    private static final Map<TestCommentReportInfo, CommentReport> commentReportMap = new HashMap<>();
    private static final Map<TestPostBanInfo, PostBan> postBanMap = new HashMap<>();
    private static final Map<TestAccountGroupInfo, AccountGroup> accountGroupMap = new HashMap<>();

    private static boolean isAccountInit = false;

    public IricomTestSuite(ApplicationContext context) {
        this.accountService = context.getBean(AccountService.class);
        this.boardService = context.getBean(BoardService.class);
        this.boardAuthorizationService = context.getBean(BoardAuthorizationService.class);
        this.postService = context.getBean(PostService.class);
        this.commentService = context.getBean(CommentService.class);
        this.reportService = context.getBean(ReportService.class);
        this.banService = context.getBean(BanService.class);
        this.accountGroupService = context.getBean(AccountGroupService.class);

        this.accountRepository = context.getBean(AccountRepository.class);
        this.boardRepository = context.getBean(BoardRepository.class);
        this.postRepository = context.getBean(PostRepository.class);
        this.commentRepository = context.getBean(CommentRepository.class);
        this.reportRepository = context.getBean(ReportRepository.class);
        this.banRepository = context.getBean(BanRepository.class);
        this.accountGroupRepository = context.getBean(AccountGroupRepository.class);

        if (isAccountInit) {
            return;
        }

        isAccountInit = true;
        this.setAccount(Arrays.asList(testAccountInfos));
    }

    /**
     * 계정 생성
     */
    protected void setAccount(List<TestAccountInfo> testAccountInfoList) {
        for (TestAccountInfo testAccountInfo : testAccountInfoList) {
            Set<TestAccountInfo> keySet = accountMap.keySet();
            if (keySet.contains(testAccountInfo)) {
                continue;
            }

            Account account = this.createAccount(testAccountInfo);
            accountMap.put(testAccountInfo, account);

            if (!testAccountInfo.isUnregistered()) {
                this.updateAccountDetail(testAccountInfo, account);
            }
        }
    }

    /**
     * 게시판 생성
     */
    protected void setBoard(List<TestBoardInfo> testBoardInfoList) {
        for (TestBoardInfo testBoardInfo : testBoardInfoList) {
            Board board = this.createBoard(testBoardInfo);
            boardMap.put(testBoardInfo, board);

            for (TestAccountInfo testAccountInfo : testBoardInfo.getAdminList()) {
                String accountId = accountMap.get(testAccountInfo).getId().toString();
                String boardId = board.getId().toString();
                this.createBoardAdmin(accountId, boardId);
            }
        }
    }

    /**
     * 게시물 생성
     */
    protected void setPost(List<TestPostInfo> testPostInfoList) {
        for (TestPostInfo testPostInfo : testPostInfoList) {
            Post post = this.createPost(testPostInfo);
            postMap.put(testPostInfo, post);

            if (testPostInfo.getPostState() == PostState.PUBLISH) {
                this.publishPost(testPostInfo);
            }
        }
    }

    /**
     * 댓글 생성
     */
    protected void setComment(List<TestCommentInfo> testCommentInfoList) {
        List<TestCommentInfo> filteredList = testCommentInfoList.stream()
                .filter(item -> {
                    TestCommentInfo referenceTestCommentInfo = item.getReferenceComment();
                    Set<TestCommentInfo> keySet = commentMap.keySet();
                    if (item.getReferenceComment() == null || keySet.contains(referenceTestCommentInfo)) {
                        Comment comment = this.createComment(item);
                        commentMap.put(item, comment);
                        return false;
                    } else {
                        return true;
                    }
                }).collect(Collectors.toList());

        if (!filteredList.isEmpty()) {
            setComment(filteredList);
        }
    }

    /**
     * 게시물 신고
     */
    protected void setPostReport(List<TestPostReportInfo> testPostReportInfoList) {
        for (TestPostReportInfo testPostReportInfo : testPostReportInfoList) {
            PostReport postReport = this.reportPost(testPostReportInfo);
            postReportMap.put(testPostReportInfo, postReport);
        }
    }

    /**
     * 댓글 신고
     */
    protected void setCommentReport(List<TestCommentReportInfo> testCommentReportInfoList) {
        for (TestCommentReportInfo testCommentReportInfo : testCommentReportInfoList) {
            CommentReport commentReport = this.reportComment(testCommentReportInfo);
            commentReportMap.put(testCommentReportInfo, commentReport);
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
    protected void setBanPost(List<TestPostBanInfo> testPostBanInfoList) {
        testPostBanInfoList.forEach(item -> {
            PostBan postBan = this.banPost(item);
            postBanMap.put(item, postBan);
        });
    }

    /**
     * 계정 그룹 생성
     */
    protected void setAccountGroup(List<TestAccountGroupInfo> testAccountGroupInfoList) {
        testAccountGroupInfoList.forEach(item -> {
            AccountGroup accountGroup = this.setAccountGroup(item);
            accountGroupMap.put(item, accountGroup);
        });
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

    protected void init() {
        this.setBoard(testBoardInfoList);
        this.setAccountGroup(testAccountGroupInfoList);
        this.setPost(testPostInfoList);
        this.setComment(testCommentInfoList);
        this.setPostReport(testPostReportInfoList);
        this.setCommentReport(testCommentReportInfoList);

        this.setBanPost(testPostBanInfoList);
        this.setDeletedComment(testCommentInfoList);
        this.setDisabledCommentBoard(testPostInfoList);
        this.setDisabledBoard(testBoardInfoList);
        this.setNotificationOnlyBoard(testBoardInfoList);
        this.deleteAccountGroup(testAccountGroupInfoList);
        this.deleteBoardAdmin(testBoardInfoList);
    }

    private Account createAccount(TestAccountInfo testAccountInfo) {
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

        Optional<Account> accountOptional = this.accountRepository.getAccount(account.getId());
        return accountOptional.get();
    }

    private void updateAccountDetail(TestAccountInfo testAccountInfo, Account account) {
        AccountInfoUpdate accountInfoUpdate = AccountInfoUpdate.builder()
                .nickname(testAccountInfo.getNickname())
                .description(testAccountInfo.getDescription())
                .build();
        this.accountService.updateAccountDetail(account, accountInfoUpdate);
    }

    private Board getBoard(String id) {
        long boardId;
        try {
            boardId = Long.parseLong(id);
        } catch (Exception exception) {
            throw new IricomException(IricomErrorCode.NOT_EXIST_BOARD);
        }
        Optional<Board> boardOptional = this.boardRepository.getBoard(boardId);
        return boardOptional.orElseThrow(() -> new IricomException(IricomErrorCode.NOT_EXIST_BOARD));
    }

    private Board createBoard(TestBoardInfo testBoardInfo) {
        BoardInfoCreate boardInfoCreate = BoardInfoCreate.builder()
                .title(testBoardInfo.getTitle())
                .description(testBoardInfo.getDescription())
                .enabled(true)
                .undisclosed(testBoardInfo.isUndisclosed())
                .build();

        BoardInfo boardInfo = this.boardService.createBoardInfo(boardInfoCreate);
        return this.getBoard(boardInfo.getId());
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

    private void disableBoard(Board board) {
        BoardInfoUpdate boardInfoUpdate = BoardInfoUpdate.builder()
                .enabled(false)
                .build();
        this.boardService.updateBoardInfo(board, boardInfoUpdate);
    }

    private void notificationOnlyBoard(Board board) {
        BoardInfoUpdate boardInfoUpdate = BoardInfoUpdate.builder()
                .notificationOnly(true)
                .build();
        this.boardService.updateBoardInfo(board, boardInfoUpdate);
    }

    private Post getPost(String id) {
        Optional<Post> postOptional = this.postRepository.getPost(id);
        return postOptional.orElseThrow(() -> new IricomException(IricomErrorCode.NOT_EXIST_POST));
    }

    private Post createPost(TestPostInfo testPostInfo) {
        Account account = accountMap.get(testPostInfo.getCreator());
        Board board = boardMap.get(testPostInfo.getBoard());

        PostInfoCreate postInfoCreate = PostInfoCreate.builder()
                .title(testPostInfo.getTitle())
                .content(testPostInfo.getContent())
                .allowComment(true)
                .type(testPostInfo.getPostType())
                .build();
        PostInfo postInfo = this.postService.createPostInfo(account, board, postInfoCreate);
        return this.getPost(postInfo.getId());
    }

    private void updateDisabledAllowComment(TestPostInfo testPostInfo) {
        Account account = accountMap.get(testPostInfo.getCreator());
        Board board = boardMap.get(testPostInfo.getBoard());
        Post post = postMap.get(testPostInfo);

        PostInfoUpdate postInfoUpdate = PostInfoUpdate.builder()
                .allowComment(false)
                .build();
        this.postService.updatePostInfo(account, board, post, postInfoUpdate);

        if (testPostInfo.getPostState() == PostState.PUBLISH) {
            this.publishPost(testPostInfo);
        }
    }

    private void publishPost(TestPostInfo testPostInfo) {
        Account account = accountMap.get(testPostInfo.getCreator());
        Board board = boardMap.get(testPostInfo.getBoard());
        Post post = postMap.get(testPostInfo);

        this.postService.publishPostInfo(account, board, post);
    }

    private Comment getComment(String id) {
        Optional<Comment> commentOptional = this.commentRepository.getComment(id);
        return commentOptional.orElseThrow(() -> new IricomException(IricomErrorCode.NOT_EXIST_COMMENT));
    }

    private Comment createComment(TestCommentInfo testCommentInfo) {
        TestAccountInfo testAccountInfo = testCommentInfo.getCreator();
        TestPostInfo testPostInfo = testCommentInfo.getPost();
        TestBoardInfo testBoardInfo = testPostInfo.getBoard();
        TestCommentInfo referenceTestCommentInfo = testCommentInfo.getReferenceComment();

        Account account = accountMap.get(testAccountInfo);
        Board board = boardMap.get(testBoardInfo);
        Post post = postMap.get(testPostInfo);
        Comment referenceComment = commentMap.get(referenceTestCommentInfo);

        CommentInfoCreate.CommentInfoCreateBuilder builder = CommentInfoCreate.builder()
                .content(testCommentInfo.getContent());
        if (referenceComment != null) {
            builder.referenceCommentId(referenceComment.getId().toString());
        }
        CommentInfoCreate commentInfoCreate = builder.build();

        String boardId = String.valueOf(board.getId());
        String postId = String.valueOf(post.getId());
        CommentInfo commentInfo = this.commentService.createCommentInfo(account, boardId, postId, commentInfoCreate);
        return this.getComment(commentInfo.getId());
    }

    private void deleteComment(TestCommentInfo testCommentInfo) {
        TestAccountInfo testAccountInfo = testCommentInfo.getCreator();
        TestPostInfo testPostInfo = testCommentInfo.getPost();
        TestBoardInfo testBoardInfo = testPostInfo.getBoard();
        Account account = accountMap.get(testAccountInfo);
        Board board = boardMap.get(testBoardInfo);
        Post post = postMap.get(testPostInfo);
        Comment comment = commentMap.get(testCommentInfo);

        this.commentService.deleteComment(account, board, post, comment);
    }

    private PostReport getPostReport(String id) {
        Optional<PostReport> postReportOptional = this.reportRepository.getPostReport(id);
        return postReportOptional.orElseThrow(() -> new IricomException(IricomErrorCode.NOT_EXIST_POST_REPORT));
    }

    private PostBan getPostBan(String id) {
        Optional<PostBan> postBanOptional = this.banRepository.getPostBan(id);
        return postBanOptional.orElseThrow(() -> new IricomException(IricomErrorCode.NOT_EXIST_POST_BAN));
    }

    private CommentReport getCommentReport(String id) {
        Optional<CommentReport> commentReportOptional = this.reportRepository.getCommentReport(id);
        return commentReportOptional.orElseThrow(() -> new IricomException(IricomErrorCode.NOT_EXIST_COMMENT_REPORT));
    }

    private PostReport reportPost(TestPostReportInfo testPostReportInfo) {
        TestAccountInfo reportTestAccountInfo = testPostReportInfo.getReportAccount();
        TestPostInfo testPostInfo = testPostReportInfo.getPost();

        Account reportAccount = accountMap.get(reportTestAccountInfo);
        Post post = postMap.get(testPostInfo);
        Board board = post.getBoard();

        String boardId = String.valueOf(board.getId());
        String postId = String.valueOf(post.getId());

        PostReportInfoCreate postReportInfoCreate = PostReportInfoCreate.builder()
                .type(testPostReportInfo.getType())
                .reason(testPostReportInfo.getReason())
                .build();
        PostReportInfo postReportInfo = this.reportService.reportPost(reportAccount, boardId, postId, postReportInfoCreate);
        return this.getPostReport(postReportInfo.getId());
    }


    private CommentReport reportComment(TestCommentReportInfo testCommentReportInfo) {
        TestAccountInfo reportTestAccountInfo = testCommentReportInfo.getReportAccount();
        TestCommentInfo testCommentInfo = testCommentReportInfo.getComment();

        Account reportAccount = accountMap.get(reportTestAccountInfo);
        Comment comment = commentMap.get(testCommentInfo);
        Post post = comment.getPost();
        Board board = post.getBoard();

        String boardId = String.valueOf(board.getId());
        String postId = String.valueOf(post.getId());
        String commentId = String.valueOf(comment.getId());

        CommentReportInfoCreate commentReportInfoCreate = CommentReportInfoCreate.builder()
                .type(testCommentReportInfo.getType())
                .reason(testCommentReportInfo.getReason())
                .build();
        CommentReportInfo commentReportInfo = this.reportService.reportComment(reportAccount, boardId, postId, commentId, commentReportInfoCreate);
        return this.getCommentReport(commentReportInfo.getId());
    }

    private PostBan banPost(TestPostBanInfo testPostBanInfo) {
        TestAccountInfo banTestAccountInfo = testPostBanInfo.getBanAccount();
        TestPostInfo testPostInfo = testPostBanInfo.getPost();

        Account banAccount = accountMap.get(banTestAccountInfo);
        Post post = postMap.get(testPostInfo);
        Board board = post.getBoard();

        PostBanInfoCreate postBanInfoCreate = PostBanInfoCreate.builder()
                .reason(testPostBanInfo.getReason())
                .build();
        PostBanInfo postBanInfo = banService.banPost(banAccount, board, post, postBanInfoCreate);
        return this.getPostBan(postBanInfo.getId());
    }

    private AccountGroup setAccountGroup(TestAccountGroupInfo testAccountGroupInfo) {
        AccountGroup accountGroup = AccountGroup.builder()
                .title(testAccountGroupInfo.getTitle())
                .description(testAccountGroupInfo.getDescription())
                .enabled(testAccountGroupInfo.getEnabled())
                .deleted(false)
                .build();
        List<AccountInAccountGroup> accountInAccountGroupList = testAccountGroupInfo.getAccountList().stream()
                .map(this::getAccount)
                .map(item -> AccountInAccountGroup.builder()
                        .accountGroup(accountGroup)
                        .account(item)
                        .build())
                .collect(Collectors.toList());
        List<BoardInAccountGroup> boardInAccountGroupList = testAccountGroupInfo.getBoardList().stream()
                .map(this::getBoard)
                .map(item -> BoardInAccountGroup.builder()
                        .accountGroup(accountGroup)
                        .board(item)
                        .build())
                .collect(Collectors.toList());
        this.accountGroupRepository.saveAccountGroup(accountGroup, accountInAccountGroupList, boardInAccountGroupList);
        return accountGroup;
    }

    private void deleteAccountGroup(TestAccountGroupInfo testAccountGroupInfo) {
        AccountGroup accountGroup = getAccountGroup(testAccountGroupInfo);
        String accountGroupId = String.valueOf(accountGroup.getId());
        this.accountGroupService.deleteAccountGroupInfo(accountGroupId);
    }

    protected void setAuthToken(MockHttpServletRequestBuilder builder, TestAccountInfo testAccountInfo) throws Exception {
        String token = tokenMap.get(testAccountInfo);
        if (token == null) {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            FirebaseTokenResponse tokenResponse = FirebaseUtils.getToken(testAccountInfo.getEmail(), ACCOUNT_PASSWORD);
            stopWatch.stop();
            log.info("Create firebase token - account: {}, execute time: {}", testAccountInfo.getEmail(), stopWatch.getTotalTimeMillis());
            token = tokenResponse.getIdToken();
            tokenMap.put(testAccountInfo, token);
        }

        builder.header("Authorization", "Bearer " + token);
    }

    protected String getJsonString(Map<String, Object> map) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(map);
    }

    protected Account getAccount(TestAccountInfo testAccountInfo) {
        return accountMap.get(testAccountInfo);
    }

    protected String getAccountId(TestAccountInfo testAccountInfo) {
        return String.valueOf(this.getAccount(testAccountInfo).getId());
    }

    private Board getBoard(TestBoardInfo testBoardInfo) {
        return boardMap.get(testBoardInfo);
    }

    protected String getBoardId(TestBoardInfo testBoardInfo) {
        return String.valueOf(this.getBoard(testBoardInfo).getId());
    }

    private Post getPost(TestPostInfo testPostInfo) {
        return postMap.get(testPostInfo);
    }

    protected String getPostId(TestPostInfo testPostInfo) {
        return String.valueOf(this.getPost(testPostInfo).getId());
    }

    private Comment getComment(TestCommentInfo testCommentInfo) {
        return commentMap.get(testCommentInfo);
    }

    protected String getCommentId(TestCommentInfo testCommentInfo) {
        return String.valueOf(this.getComment(testCommentInfo).getId());
    }

    private PostReport getPostReport(TestPostReportInfo testPostReportInfo) {
        return postReportMap.get(testPostReportInfo);
    }

    protected String getPostReportId(TestPostReportInfo testPostReportInfo) {
        return String.valueOf(this.getPostReport(testPostReportInfo).getId());
    }

    private CommentReport getCommentReport(TestCommentReportInfo testCommentReportInfo) {
        return commentReportMap.get(testCommentReportInfo);
    }

    protected String getCommentReportId(TestCommentReportInfo testCommentReportInfo) {
        return String.valueOf(this.getCommentReport(testCommentReportInfo).getId());
    }

    private PostBan getPostBan(TestPostBanInfo testPostBanInfo) {
        return postBanMap.get(testPostBanInfo);
    }

    protected String getPostBanId(TestPostBanInfo testPostBanInfo) {
        return String.valueOf(this.getPostBan(testPostBanInfo).getId());
    }

    protected AccountGroup getAccountGroup(TestAccountGroupInfo testAccountGroupInfo) {
        return accountGroupMap.get(testAccountGroupInfo);
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

    protected void addTestPostBanInfo(TestPostBanInfo ...testPostBanInfos) {
        this.addTestPostBanInfo(Arrays.asList(testPostBanInfos));
    }

    protected void addTestPostBanInfo(List<TestPostBanInfo> testPostBanInfoList) {
        this.testPostBanInfoList.addAll(testPostBanInfoList);
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