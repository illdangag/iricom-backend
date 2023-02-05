package com.illdangag.iricom.server.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.illdangag.iricom.server.data.response.CommentInfo;
import com.illdangag.iricom.server.service.*;
import com.illdangag.iricom.server.test.data.*;
import com.illdangag.iricom.server.data.entity.*;
import com.illdangag.iricom.server.data.request.*;
import com.illdangag.iricom.server.data.response.AccountInfo;
import com.illdangag.iricom.server.data.response.BoardInfo;
import com.illdangag.iricom.server.data.response.PostInfo;
import com.illdangag.iricom.server.test.util.FirebaseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.util.StopWatch;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
public abstract class IricomTestSuite {
    private final AccountService accountService;
    private final BoardService boardService;
    private final BoardAuthorizationService boardAuthorizationService;
    private final PostService postService;
    private final CommentService commentService;

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
            .email("common00@iricom.com").nickname("common00").description("this is common00.").build();

    protected static final TestAccountInfo common01 = TestAccountInfo.builder()
            .email("common01@iricom.com").nickname("common01").description("this is common01.").build();

    protected static final TestAccountInfo unknown00 = TestAccountInfo.builder()
            .email("unknown00@iriom.com").isUnregistered(true).nickname("unknown00").description("this is unknown00.").build();

    protected static final TestAccountInfo unknown01 = TestAccountInfo.builder()
            .email("unknown01@iricom.com").isUnregistered(true).nickname("unknown01").description("this is unknown01.").build();

    protected static final TestAccountInfo unknown02 = TestAccountInfo.builder()
            .email("unknown02@iricom.com").isUnregistered(true).nickname("unknown02").description("this is unknown02.").build();

    private static final TestAccountInfo[] testAccountInfos = {
            systemAdmin, // 시스템 관리자
            allBoardAdmin, // 모든 게시판 관리자
            enableBoardAdmin, // 활성화 게시판 관리자
            toEnableBoardAdmin, // 게시판 관리자 추가 테스트용 계정
            disableBoardAdmin, // 비활성화 게시판 관리자
            toDisableBoardAdmin, // 게시판 관리자 삭제 테스트용 계정
            common00, // 일반 계정, 게시판 관리자 계정으로 등록 금지
            common01,
            unknown00, // 등록되지 않은 계정 권한용 테스트 계정, 테스트 케이스에서 계정 정보를 등록 금지
            unknown01, // 등록되지 않은 계정의 정보 수정 테스트용 계정, 테스트 케이스가 수행 되면 등록된 계정으로 전환
            unknown02, // 등록되지 않은 계정의 정보 수정 테스트용 계정, 테스트 케이스가 수행 되면 등록된 계정으로 전환
    };

    // 게시판 설정
    protected static final TestBoardInfo enableBoard = TestBoardInfo.builder()
            .title("enable").isEnabled(true).adminList(Arrays.asList(allBoardAdmin, enableBoardAdmin, toDisableBoardAdmin)).build();

    protected static final TestBoardInfo disableBoard = TestBoardInfo.builder()
            .title("disable").isEnabled(false).adminList(Arrays.asList(allBoardAdmin, disableBoardAdmin)).build();

    protected static final TestBoardInfo updateBoard = TestBoardInfo.builder()
            .title("update").isEnabled(true).adminList(Arrays.asList(allBoardAdmin)).build();

    protected static final TestBoardInfo createBoard = TestBoardInfo.builder()
            .title("createBoard").isEnabled(true).adminList(Arrays.asList(allBoardAdmin)).build();

    protected static final TestBoardInfo commentBoard = TestBoardInfo.builder()
            .title("commentBoard").isEnabled(true).adminList(Arrays.asList(allBoardAdmin)).build();

    private static final TestBoardInfo[] testBoardInfos = {
            enableBoard, // 활성화 게시판
            disableBoard, // 비활성화 게시판
            updateBoard, // 게시판 정보 수정 테스트용 게시판
            createBoard, // 문서 생성 테스트용 게시판
            commentBoard, // 댓글 테스트용 게시판
    };

    // 게시물 설정
    protected static final TestPostInfo enableBoardPost00 = TestPostInfo.builder()
            .title("enableBoardPost00").content("enableBoardPost00").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.POST)
            .creator(allBoardAdmin).board(enableBoard).build();

    protected static final TestPostInfo enableBoardPost01 = TestPostInfo.builder()
            .title("enableBoardPost01").content("enableBoardPost01").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.POST)
            .creator(allBoardAdmin).board(enableBoard).isAllowComment(false).build();

    protected static final TestPostInfo enableBoardPost02 = TestPostInfo.builder()
            .title("enableBoardPost02").content("enableBoardPost02").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.POST)
            .creator(allBoardAdmin).board(enableBoard).build();

    protected static final TestPostInfo enableBoardPost03 = TestPostInfo.builder()
            .title("enableBoardPost03").content("enableBoardPost03").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.TEMPORARY)
            .creator(allBoardAdmin).board(enableBoard).build();

    protected static final TestPostInfo enableBoardNotification00 = TestPostInfo.builder()
            .title("enableBoardNotification00").content("enableBoardNotification00").isAllowComment(true)
            .postType(PostType.NOTIFICATION).postState(PostState.POST)
            .creator(allBoardAdmin).board(enableBoard).build();

    protected static final TestPostInfo disableBoardPost00 = TestPostInfo.builder()
            .title("disableBoardPost00").content("content").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.POST)
            .creator(common00).board(disableBoard).build();

    protected static final TestPostInfo disableBoardPost01 = TestPostInfo.builder()
            .title("disableBoardPost01").content("content").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.POST)
            .creator(allBoardAdmin).board(disableBoard).build();

    protected static final TestPostInfo disableBoardPost02 = TestPostInfo.builder()
            .title("disableBoardPost02").content("content").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.POST)
            .creator(allBoardAdmin).board(disableBoard).build();

    protected static final TestPostInfo disableBoardNotification00 = TestPostInfo.builder()
            .title("disableBoardNotification00").content("disableBoardNotification00").isAllowComment(true)
            .postType(PostType.NOTIFICATION).postState(PostState.TEMPORARY)
            .creator(allBoardAdmin).board(disableBoard).build();

    protected static final TestPostInfo updateBoardPost00 = TestPostInfo.builder()
            .title("updateBoardPost00").content("content").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.TEMPORARY)
            .creator(common00).board(updateBoard).build();

    protected static final TestPostInfo updateBoardPost01 = TestPostInfo.builder()
            .title("updateBoardPost01").content("content").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.TEMPORARY)
            .creator(common00).board(updateBoard).build();

    protected static final TestPostInfo updateBoardPost02 = TestPostInfo.builder()
            .title("updateBoardPost02").content("content").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.TEMPORARY)
            .creator(common00).board(updateBoard).build();

    protected static final TestPostInfo updateBoardPost03 = TestPostInfo.builder()
            .title("updateBoardPost03").content("content").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.TEMPORARY)
            .creator(common00).board(updateBoard).build();

    protected static final TestPostInfo updateBoardPost04 = TestPostInfo.builder()
            .title("updateBoardPost04").content("content").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.TEMPORARY)
            .creator(allBoardAdmin).board(updateBoard).build();

    protected static final TestPostInfo updateBoardPost05 = TestPostInfo.builder()
            .title("updateBoardPost05").content("content").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.POST)
            .creator(common00).board(updateBoard).build();

    protected static final TestPostInfo updateBoardPost06 = TestPostInfo.builder()
            .title("updateBoardPost06").content("content").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.POST)
            .creator(common00).board(updateBoard).build();

    protected static final TestPostInfo updateBoardPost07 = TestPostInfo.builder()
            .title("updateBoardPost07").content("content").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.POST)
            .creator(allBoardAdmin).board(updateBoard).build();

    protected static final TestPostInfo updateBoardPost08 = TestPostInfo.builder()
            .title("updateBoardPost08").content("content").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.TEMPORARY)
            .creator(common00).board(updateBoard).build();

    protected static final TestPostInfo updateBoardPost09 = TestPostInfo.builder()
            .title("updateBoardPost09").content("content").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.POST)
            .creator(common00).board(updateBoard).build();

    protected static final TestPostInfo updateBoardPost10 = TestPostInfo.builder()
            .title("updateBoardPost10").content("content").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.TEMPORARY)
            .creator(common00).board(updateBoard).build();

    protected static final TestPostInfo updateBoardNotification00 = TestPostInfo.builder()
            .title("updateBoardNotification00").content("content").isAllowComment(true)
            .postType(PostType.NOTIFICATION).postState(PostState.TEMPORARY)
            .creator(allBoardAdmin).board(updateBoard).build();

    protected static final TestPostInfo commentGetPost00 = TestPostInfo.builder()
            .title("commentUpdatePost00").content("content").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.POST)
            .creator(common00).board(commentBoard).build();

    protected static final TestPostInfo commentUpdatePost00 = TestPostInfo.builder()
            .title("commentUpdatePost00").content("content").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.POST)
            .creator(common00).board(commentBoard).build();

    protected static final TestPostInfo commentUpdatePost01 = TestPostInfo.builder()
            .title("commentUpdatePost01").content("content").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.TEMPORARY)
            .creator(common00).board(commentBoard).build();

    protected static final TestPostInfo commentUpdatePost02 = TestPostInfo.builder()
            .title("commentUpdatePost02").content("content").isAllowComment(false)
            .postType(PostType.POST).postState(PostState.POST)
            .creator(common00).board(commentBoard).build();

    private static final TestPostInfo[] testPostInfos = {
            enableBoardPost00, enableBoardPost01, enableBoardPost02, enableBoardPost03,
            enableBoardNotification00,

            disableBoardPost00, disableBoardPost01, disableBoardPost02,
            disableBoardNotification00,

            updateBoardPost00, updateBoardPost01, updateBoardPost02, updateBoardPost03, updateBoardPost04,
            updateBoardPost05, updateBoardPost06, updateBoardPost07, updateBoardPost08, updateBoardPost09,
            updateBoardPost10,
            updateBoardNotification00,

            commentGetPost00,
            commentUpdatePost00,
            commentUpdatePost01, // 발행되지 않음
            commentUpdatePost02, // 댓글을 허용하지 않음
    };

    // 댓글 설정
    protected static final TestCommentInfo enableBoardComment00 = TestCommentInfo.builder()
            .content("enableBoardComment00")
            .creator(common00).post(enableBoardPost00)
            .build();
    protected static final TestCommentInfo disableBoardComment00 = TestCommentInfo.builder()
            .content("disableBoardComment00")
            .creator(common00).post(disableBoardPost00)
            .build();

    protected static final TestCommentInfo commentGetComment00 = TestCommentInfo.builder()
            .content("commentGetComment00")
            .creator(common00).post(commentGetPost00)
            .build();
    protected static final TestCommentInfo commentGetComment01 = TestCommentInfo.builder()
            .content("commentGetComment01")
            .creator(common00).post(commentGetPost00)
            .build();
    protected static final TestCommentInfo commentGetComment03 = TestCommentInfo.builder()
            .content("commentGetComment03")
            .creator(common00).post(commentGetPost00)
            .build();
    protected static final TestCommentInfo commentGetComment04 = TestCommentInfo.builder()
            .content("commentGetComment04")
            .creator(common00).post(commentGetPost00).referenceComment(commentGetComment00)
            .build();
    protected static final TestCommentInfo commentGetComment05 = TestCommentInfo.builder()
            .content("commentGetComment05")
            .creator(common00).post(commentGetPost00).referenceComment(commentGetComment00)
            .build();
    protected static final TestCommentInfo commentGetComment06 = TestCommentInfo.builder()
            .content("commentGetComment06")
            .creator(common00).post(commentGetPost00).referenceComment(commentGetComment00)
            .build();
    protected static final TestCommentInfo commentGetComment07 = TestCommentInfo.builder()
            .content("commentGetComment07")
            .creator(common00).post(commentGetPost00).referenceComment(commentGetComment00)
            .build();
    protected static final TestCommentInfo commentGetComment08 = TestCommentInfo.builder()
            .content("commentGetComment08")
            .creator(common00).post(commentGetPost00).referenceComment(commentGetComment04)
            .build();
    protected static final TestCommentInfo commentGetComment09 = TestCommentInfo.builder()
            .content("commentGetComment09")
            .creator(common00).post(commentGetPost00).referenceComment(commentGetComment04)
            .build();
    protected static final TestCommentInfo commentUpdateComment00 = TestCommentInfo.builder()
            .content("commentUpdateComment00")
            .creator(common00).post(commentUpdatePost00)
            .build();
    protected static final TestCommentInfo commentDeleteComment00 = TestCommentInfo.builder()
            .content("commentDeleteComment00")
            .creator(common00).post(commentUpdatePost00)
            .build();
    protected static final TestCommentInfo commentDeleteComment01 = TestCommentInfo.builder()
            .content("commentDeleteComment01")
            .creator(common00).post(commentUpdatePost00).referenceComment(commentDeleteComment00)
            .build();
    protected static final TestCommentInfo commentDeleteComment02 = TestCommentInfo.builder()
            .content("commentDeleteComment02")
            .creator(common00).post(commentUpdatePost00).referenceComment(commentDeleteComment00).deleted(true)
            .build();
    protected static final TestCommentInfo commentDeleteComment03 = TestCommentInfo.builder()
            .content("commentDeleteComment03")
            .creator(common00).post(commentUpdatePost00)
            .build();

    private static final TestCommentInfo[] testCommentInfos = {
            enableBoardComment00,
            disableBoardComment00,

            commentGetComment00, commentGetComment01, commentGetComment03, commentGetComment04,
            commentGetComment05, commentGetComment06, commentGetComment07, commentGetComment08,
            commentGetComment09,
            commentUpdateComment00,
            commentDeleteComment00, commentDeleteComment01, commentDeleteComment02, commentDeleteComment03,
    };

    private static final Map<TestAccountInfo, Account> accountMap = new HashMap<>();
    private static final Map<TestBoardInfo, Board> boardMap = new HashMap<>();
    private static final Map<TestPostInfo, Post> postMap = new HashMap<>();
    private static final Map<TestAccountInfo, String> tokenMap = new HashMap<>();
    private static final Map<TestCommentInfo, Comment> commentMap = new HashMap<>();

    private static boolean isInit = false;

    public IricomTestSuite(ApplicationContext context) {
        this.accountService = context.getBean(AccountService.class);
        this.boardService = context.getBean(BoardService.class);
        this.boardAuthorizationService = context.getBean(BoardAuthorizationService.class);
        this.postService = context.getBean(PostService.class);
        this.commentService = context.getBean(CommentService.class);

        if (!isInit) {
            this.init();
            isInit = true;
        }
    }

    protected void init() {
        // 계정 생성
        for (TestAccountInfo testAccountInfo : testAccountInfos) {
            if (!testAccountInfo.isUnregistered()) {
                Account account = this.createAccount(testAccountInfo);
                accountMap.put(testAccountInfo, account);
            }
        }

        // 게시판 생성
        for (TestBoardInfo testBoardInfo : testBoardInfos) {
            Board board = this.createBoard(testBoardInfo);
            boardMap.put(testBoardInfo, board);

            for (TestAccountInfo testAccountInfo : testBoardInfo.getAdminList()) {
                String accountId = accountMap.get(testAccountInfo).getId().toString();
                String boardId = board.getId().toString();
                this.createBoardAdmin(accountId, boardId);
            }
        }

        // 게시물 생성
        for (TestPostInfo testPostInfo : testPostInfos) {
            Post post = this.createPost(testPostInfo);
            postMap.put(testPostInfo, post);

            if (testPostInfo.getPostState() == PostState.POST) {
                this.publishPost(testPostInfo);
            }
        }

        // 댓글 생성
        // 대댓글이 아닌 댓글
        for (TestCommentInfo testCommentInfo : testCommentInfos) {
            if (testCommentInfo.getReferenceComment() == null) {
                Comment comment = this.createComment(testCommentInfo);
                commentMap.put(testCommentInfo, comment);
            }
        }
        // 대댓글
        for (TestCommentInfo testCommentInfo : testCommentInfos) {
            if (testCommentInfo.getReferenceComment() != null) {
                Comment comment = this.createComment(testCommentInfo);
                commentMap.put(testCommentInfo, comment);
            }
        }
        // 삭제 할 댓글
        Arrays.stream(testCommentInfos)
                .filter(TestCommentInfo::isDeleted)
                .forEach(this::deleteComment);

        // 게시판 비활성화
        for (TestBoardInfo testBoardInfo : testBoardInfos) {
            Board board = boardMap.get(testBoardInfo);
            if (!testBoardInfo.isEnabled()) {
                this.disableBoard(board);
            }
        }
    }

    private Account createAccount(TestAccountInfo testAccountInfo) {
        AccountInfoCreate accountInfoCreate = AccountInfoCreate.builder()
                .email(testAccountInfo.getEmail())
                .isAdmin(testAccountInfo.isAdmin())
                .nickname(testAccountInfo.getNickname())
                .description(testAccountInfo.getDescription())
                .build();
        AccountInfo accountInfo = this.accountService.createAccountInfo(accountInfoCreate);
        return this.accountService.getAccount(accountInfo.getId());
    }

    private Board createBoard(TestBoardInfo testBoardInfo) {
        BoardInfoCreate boardInfoCreate = BoardInfoCreate.builder()
                .title(testBoardInfo.getTitle())
                .description(testBoardInfo.getDescription())
                .enabled(true)
                .build();
        BoardInfo boardInfo = this.boardService.createBoardInfo(boardInfoCreate);
        return this.boardService.getBoard(boardInfo.getId());
    }

    private void createBoardAdmin(String accountId, String boardId) {
        BoardAdminInfoCreate boardAdminInfoCreate = BoardAdminInfoCreate.builder()
                .accountId(accountId)
                .boardId(boardId)
                .build();
        this.boardAuthorizationService.createBoardAdminAuth(boardAdminInfoCreate);
    }

    private void disableBoard(Board board) {
        BoardInfoUpdate boardInfoUpdate = BoardInfoUpdate.builder()
                .enabled(false)
                .build();
        this.boardService.updateBoardInfo(board, boardInfoUpdate);
    }

    private Post createPost(TestPostInfo testPostInfo) {
        Account account = accountMap.get(testPostInfo.getCreator());
        Board board = boardMap.get(testPostInfo.getBoard());

        PostInfoCreate postInfoCreate = PostInfoCreate.builder()
                .title(testPostInfo.getTitle())
                .content(testPostInfo.getContent())
                .isAllowComment(testPostInfo.isAllowComment())
                .type(testPostInfo.getPostType())
                .build();
        PostInfo postInfo = this.postService.createPostInfo(account, board, postInfoCreate);
        return this.postService.getPost(postInfo.getId());
    }

    private void publishPost(TestPostInfo testPostInfo) {
        Account account = accountMap.get(testPostInfo.getCreator());
        Board board = boardMap.get(testPostInfo.getBoard());
        Post post = postMap.get(testPostInfo);

        this.postService.publishPostInfo(account, board, post);
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

        CommentInfo commentInfo = this.commentService.createCommentInfo(account, board, post, commentInfoCreate);
        return this.commentService.getComment(commentInfo.getId());
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

    protected Board getBoard(TestBoardInfo testBoardInfo) {
        return boardMap.get(testBoardInfo);
    }

    protected Post getPost(TestPostInfo testPostInfo) {
        return postMap.get(testPostInfo);
    }

    protected Comment getComment(TestCommentInfo testCommentInfo) {
        return commentMap.get(testCommentInfo);
    }
}
