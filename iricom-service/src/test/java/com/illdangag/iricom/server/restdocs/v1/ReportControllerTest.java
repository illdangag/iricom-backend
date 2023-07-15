package com.illdangag.iricom.server.restdocs.v1;

import com.illdangag.iricom.server.data.entity.*;
import com.illdangag.iricom.server.restdocs.snippet.IricomFieldsSnippet;
import com.illdangag.iricom.server.test.IricomTestSuite;
import com.illdangag.iricom.server.test.data.wrapper.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.*;

import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("restdoc: 신고")
public class ReportControllerTest extends IricomTestSuite {
    @Autowired
    private MockMvc mockMvc;

    private final TestBoardInfo testBoardInfo00 = TestBoardInfo.builder()
            .title("testBoardInfo00").isEnabled(true).adminList(Collections.singletonList(allBoardAdmin)).build();

    private final TestPostInfo testPostInfo00 = TestPostInfo.builder()
            .title("testPostInfo00").content("testPostInfo00").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .creator(allBoardAdmin).board(testBoardInfo00).build();

    private final TestPostInfo testPostInfo01 = TestPostInfo.builder()
            .title("testPostInfo01").content("testPostInfo01").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .creator(allBoardAdmin).board(testBoardInfo00).build();

    private final TestPostInfo testPostInfo02 = TestPostInfo.builder()
            .title("testPostInfo02").content("testPostInfo02").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .creator(allBoardAdmin).board(testBoardInfo00).build();

    private final TestPostReportInfo testPostReportInfo00 = TestPostReportInfo.builder()
            .type(ReportType.HATE).reason("hate report")
            .reportAccount(common00).post(testPostInfo02).build();

    private final TestCommentInfo testCommentInfo00 = TestCommentInfo.builder()
            .content("testCommentInfo00")
            .creator(common00).post(testPostInfo02)
            .build();
    private final TestCommentInfo testCommentInfo01 = TestCommentInfo.builder()
            .content("testCommentInfo01")
            .creator(common00).post(testPostInfo02).referenceComment(testCommentInfo00)
            .build();
    private final TestCommentInfo testCommentInfo02 = TestCommentInfo.builder()
            .content("testCommentInfo02")
            .creator(common00).post(testPostInfo02)
            .build();

    private final TestCommentReportInfo testCommentReportInfo00 = TestCommentReportInfo.builder()
            .type(ReportType.HATE).reason("hate report")
            .reportAccount(common00).comment(testCommentInfo01).build();

    @Autowired
    public ReportControllerTest(ApplicationContext context) {
        super(context);

        List<TestBoardInfo> testBoardInfoList = Arrays.asList(testBoardInfo00);
        List<TestPostInfo> testPostInfoList = Arrays.asList(testPostInfo00, testPostInfo01, testPostInfo02);

        List<TestPostReportInfo> testPostReportInfoList = new ArrayList<>();
        testPostReportInfoList.addAll(super.createTestPostReportInfo(testPostInfo01));
        testPostReportInfoList.add(testPostReportInfo00);

        List<TestCommentInfo> testCommentInfoList = Arrays.asList(testCommentInfo00, testCommentInfo01, testCommentInfo02);

        List<TestCommentReportInfo> testCommentReportInfoList = new ArrayList<>();
        testCommentReportInfoList.addAll(super.createTestCommentReportInfo(testCommentInfo02));
        testCommentReportInfoList.add(testCommentReportInfo00);

        super.setBoard(testBoardInfoList);
        super.setPost(testPostInfoList);
        super.setComment(testCommentInfoList);

        super.setPostReport(testPostReportInfoList);
        super.setCommentReport(testCommentReportInfoList);
    }

    @Test
    @DisplayName("게시물 신고")
    public void rp001() throws Exception {
        Post post = getPost(testPostInfo00);
        Board board = post.getBoard();

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("type", "hate");
        requestBody.put("reason", "This is a hateful post.");

        MockHttpServletRequestBuilder requestBuilder = post("/v1/report/post/boards/{boardId}/posts/{postId}", board.getId(), post.getId())
                .content(getJsonString(requestBody))
                .contentType(MediaType.APPLICATION_JSON);
        setAuthToken(requestBuilder, common00);

        List<FieldDescriptor> fieldDescriptorList = new LinkedList<>();
        fieldDescriptorList.addAll(IricomFieldsSnippet.getPostReport(""));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getPost("post."));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getAccount("post.account."));

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(200))
                .andDo(print())
                .andDo(document("RP_001",
                        preprocessRequest(
                                removeHeaders("Authorization"),
                                prettyPrint()
                        ),
                        preprocessResponse(
                                prettyPrint()
                        ),
                        pathParameters(
                                parameterWithName("boardId").description("게시판 아이디"),
                                parameterWithName("postId").description("게시물 아이디")
                        ),
                        requestHeaders(
//                                headerWithName("Authorization").description("firebase 토큰")
                        ),
                        requestFields(
                                fieldWithPath("type").description("종류"),
                                fieldWithPath("reason").description("사유")
                        ),
                        responseFields(fieldDescriptorList.toArray(FieldDescriptor[]::new))
                ));
    }

    @Test
    @DisplayName("게시물 신고 목록 조회 (게시판)")
    public void rp002() throws Exception {
        Board board = getBoard(testBoardInfo00);

        MockHttpServletRequestBuilder requestBuilder = get("/v1/report/post/boards/{boardId}", board.getId())
                .param("skip", "0")
                .param("limit", "5")
                .param("type", "hate")
                .param("reason", "report");
        setAuthToken(requestBuilder, allBoardAdmin);

        List<FieldDescriptor> fieldDescriptorList = new LinkedList<>();
        fieldDescriptorList.addAll(IricomFieldsSnippet.getSearchList(""));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getPostReport("reports.[]."));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getPost("reports.[].post."));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getAccount("reports.[].post.account."));

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(200))
                .andDo(print())
                .andDo(document("RP_002",
                        preprocessRequest(
                                removeHeaders("Authorization"),
                                prettyPrint()
                        ),
                        preprocessResponse(
                                prettyPrint()
                        ),
                        pathParameters(
                                parameterWithName("boardId").description("게시판 아이디")
                        ),
                        requestHeaders(
//                                headerWithName("Authorization").description("firebase 토큰")
                        ),
                        requestParameters(
                                parameterWithName("skip").description("건너 뛸 수"),
                                parameterWithName("limit").description("최대 조회 수"),
                                parameterWithName("type").description("게시물의 종류 (hate: 증오, pornography: 음란물, political: 정치, etc: 기타)"),
                                parameterWithName("reason").description("신고 사유")
                        ),
                        responseFields(
                                fieldDescriptorList.toArray(FieldDescriptor[]::new)
                        )
                ));
    }

    @Test
    @DisplayName("게시물 신고 목록 조회 (게시판, 게시물)")
    public void rp003() throws Exception {
        Post post = getPost(testPostInfo01);
        Board board = post.getBoard();

        MockHttpServletRequestBuilder requestBuilder = get("/v1/report/post/boards/{boardId}/posts/{postId}", board.getId(), post.getId())
                .param("skip", "0")
                .param("limit", "5")
                .param("type", "hate")
                .param("reason", "report");
        setAuthToken(requestBuilder, allBoardAdmin);

        List<FieldDescriptor> fieldDescriptorList = new LinkedList<>();
        fieldDescriptorList.addAll(IricomFieldsSnippet.getSearchList(""));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getPostReport("reports.[]."));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getPost("reports.[].post."));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getAccount("reports.[].post.account."));

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(200))
                .andDo(print())
                .andDo(document("RP_003",
                        preprocessRequest(
                                removeHeaders("Authorization"),
                                prettyPrint()
                        ),
                        preprocessResponse(
                                prettyPrint()
                        ),
                        pathParameters(
                                parameterWithName("boardId").description("게시판 아이디"),
                                parameterWithName("postId").description("게시물 아이디")
                        ),
                        requestHeaders(
//                                headerWithName("Authorization").description("firebase 토큰")
                        ),
                        requestParameters(
                                parameterWithName("skip").description("건너 뛸 수"),
                                parameterWithName("limit").description("최대 조회 수"),
                                parameterWithName("type").description("게시물의 종류 (hate: 증오, pornography: 음란물, political: 정치, etc: 기타)"),
                                parameterWithName("reason").description("신고 사유")
                        ),
                        responseFields(
                                fieldDescriptorList.toArray(FieldDescriptor[]::new)
                        )
                ));
    }

    @Test
    @DisplayName("게시물 신고 정보 조회")
    public void rp004() throws Exception {
        PostReport postReport = getPostReport(testPostReportInfo00);
        Post post = postReport.getPost();
        Board board = post.getBoard();

        MockHttpServletRequestBuilder requestBuilder = get("/v1/report/post/boards/{boardId}/posts/{postId}/reports/{reportId}", board.getId(), post.getId(), postReport.getId());
        setAuthToken(requestBuilder, allBoardAdmin);

        List<FieldDescriptor> fieldDescriptorList = new LinkedList<>();
        fieldDescriptorList.addAll(IricomFieldsSnippet.getPostReport(""));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getPost("post."));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getAccount("post.account."));

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(200))
                .andDo(print())
                .andDo(document("RP_004",
                        preprocessRequest(
                                removeHeaders("Authorization"),
                                prettyPrint()
                        ),
                        preprocessResponse(
                                prettyPrint()
                        ),
                        pathParameters(
                                parameterWithName("boardId").description("게시판 아이디"),
                                parameterWithName("postId").description("게시물 아이디"),
                                parameterWithName("reportId").description("신고 아이디")
                        ),
                        requestHeaders(
//                                headerWithName("Authorization").description("firebase 토큰")
                        ),
                        responseFields(
                                fieldDescriptorList.toArray(FieldDescriptor[]::new)
                        )
                ));
    }

    @Test
    @DisplayName("댓글 신고")
    public void rc001() throws Exception {
        Comment comment = getComment(testCommentInfo02);
        Post post = comment.getPost();
        Board board = post.getBoard();

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("type", "hate");
        requestBody.put("reason", "This is a hateful post.");

        MockHttpServletRequestBuilder requestBuilder = post("/v1/report/comment/boards/{boardId}/posts/{postId}/comments/{commentId}", board.getId(), post.getId(), comment.getId())
                .content(getJsonString(requestBody))
                .contentType(MediaType.APPLICATION_JSON);
        setAuthToken(requestBuilder, allBoardAdmin);

        List<FieldDescriptor> fieldDescriptorList = new LinkedList<>();
        fieldDescriptorList.addAll(IricomFieldsSnippet.getCommentReport(""));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getComment("comment."));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getAccount("comment.account."));

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(200))
                .andDo(print())
                .andDo(document("RC_001",
                        preprocessRequest(
                                removeHeaders("Authorization"),
                                prettyPrint()
                        ),
                        preprocessResponse(
                                prettyPrint()
                        ),
                        pathParameters(
                                parameterWithName("boardId").description("게시판 아이디"),
                                parameterWithName("postId").description("게시물 아이디"),
                                parameterWithName("commentId").description("댓글 아이디")
                        ),
                        requestHeaders(
//                                headerWithName("Authorization").description("firebase 토큰")
                        ),
                        requestFields(
                                fieldWithPath("type").description("종류"),
                                fieldWithPath("reason").description("사유")
                        ),
                        responseFields(
                                fieldDescriptorList.toArray(FieldDescriptor[]::new)
                        )
                ));
    }

    @Test
    @DisplayName("게시물 신고 목록 조회 (게시판)")
    public void rc002() throws Exception {
        Comment comment = getComment(testCommentInfo02);
        Post post = comment.getPost();
        Board board = post.getBoard();

        MockHttpServletRequestBuilder requestBuilder = get("/v1/report/comment/boards/{boardId}", board.getId())
                .param("skip", "0")
                .param("limit", "5")
                .param("type", "hate")
                .param("reason", "report");
        setAuthToken(requestBuilder, allBoardAdmin);

        List<FieldDescriptor> fieldDescriptorList = new LinkedList<>();
        fieldDescriptorList.addAll(IricomFieldsSnippet.getSearchList(""));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getCommentReport("reports.[]."));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getComment("reports.[].comment."));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getAccount("reports.[].comment.account."));

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(200))
                .andDo(print())
                .andDo(document("RC_002",
                        preprocessRequest(
                                removeHeaders("Authorization"),
                                prettyPrint()
                        ),
                        preprocessResponse(
                                prettyPrint()
                        ),
                        pathParameters(
                                parameterWithName("boardId").description("게시판 아이디")
                        ),
                        requestHeaders(
//                                headerWithName("Authorization").description("firebase 토큰")
                        ),
                        requestParameters(
                                parameterWithName("skip").description("건너 뛸 수"),
                                parameterWithName("limit").description("최대 조회 수"),
                                parameterWithName("type").description("게시물의 종류 (hate: 증오, pornography: 음란물, political: 정치, etc: 기타)"),
                                parameterWithName("reason").description("신고 사유")
                        ),
                        responseFields(
                                fieldDescriptorList.toArray(FieldDescriptor[]::new)
                        )
                ));
    }

    @Test
    @DisplayName("게시물 신고 목록 조회 (게시판, 게시물)")
    public void rc003() throws Exception {
        Comment comment = getComment(testCommentInfo02);
        Post post = comment.getPost();
        Board board = post.getBoard();

        MockHttpServletRequestBuilder requestBuilder = get("/v1/report/comment/boards/{boardId}/posts/{postId}", board.getId(), post.getId())
                .param("skip", "0")
                .param("limit", "5")
                .param("type", "hate")
                .param("reason", "report");
        setAuthToken(requestBuilder, allBoardAdmin);

        List<FieldDescriptor> fieldDescriptorList = new LinkedList<>();
        fieldDescriptorList.addAll(IricomFieldsSnippet.getSearchList(""));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getCommentReport("reports.[]."));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getComment("reports.[].comment."));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getAccount("reports.[].comment.account."));

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(200))
                .andDo(print())
                .andDo(document("RC_003",
                        preprocessRequest(
                                removeHeaders("Authorization"),
                                prettyPrint()
                        ),
                        preprocessResponse(
                                prettyPrint()
                        ),
                        pathParameters(
                                parameterWithName("boardId").description("게시판 아이디"),
                                parameterWithName("postId").description("게시물 아이디")
                        ),
                        requestHeaders(
//                                headerWithName("Authorization").description("firebase 토큰")
                        ),
                        requestParameters(
                                parameterWithName("skip").description("건너 뛸 수"),
                                parameterWithName("limit").description("최대 조회 수"),
                                parameterWithName("type").description("게시물의 종류 (hate: 증오, pornography: 음란물, political: 정치, etc: 기타)"),
                                parameterWithName("reason").description("신고 사유")
                        ),
                        responseFields(
                                fieldDescriptorList.toArray(FieldDescriptor[]::new)
                        )
                ));
    }

    @Test
    @DisplayName("게시물 신고 목록 조회 (게시판, 게시물, 댓글)")
    public void rc004() throws Exception {
        Comment comment = getComment(testCommentInfo02);
        Post post = comment.getPost();
        Board board = post.getBoard();

        MockHttpServletRequestBuilder requestBuilder = get("/v1/report/comment/boards/{boardId}/posts/{postId}/comments/{commentId}", board.getId(), post.getId(), comment.getId())
                .param("skip", "0")
                .param("limit", "5")
                .param("type", "hate")
                .param("reason", "report");
        setAuthToken(requestBuilder, allBoardAdmin);

        List<FieldDescriptor> fieldDescriptorList = new LinkedList<>();
        fieldDescriptorList.addAll(IricomFieldsSnippet.getSearchList(""));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getCommentReport("reports.[]."));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getComment("reports.[].comment."));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getAccount("reports.[].comment.account."));

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(200))
                .andDo(print())
                .andDo(document("RC_004",
                        preprocessRequest(
                                removeHeaders("Authorization"),
                                prettyPrint()
                        ),
                        preprocessResponse(
                                prettyPrint()
                        ),
                        pathParameters(
                                parameterWithName("boardId").description("게시판 아이디"),
                                parameterWithName("postId").description("게시물 아이디"),
                                parameterWithName("commentId").description("댓글 아이디")
                        ),
                        requestHeaders(
//                                headerWithName("Authorization").description("firebase 토큰")
                        ),
                        requestParameters(
                                parameterWithName("skip").description("건너 뛸 수"),
                                parameterWithName("limit").description("최대 조회 수"),
                                parameterWithName("type").description("게시물의 종류 (hate: 증오, pornography: 음란물, political: 정치, etc: 기타)"),
                                parameterWithName("reason").description("신고 사유")
                        ),
                        responseFields(
                                fieldDescriptorList.toArray(FieldDescriptor[]::new)
                        )
                ));
    }

    @Test
    @DisplayName("게시물 신고 정보 조회")
    public void rc005() throws Exception {
        CommentReport commentReport = getCommentReport(testCommentReportInfo00);
        Comment comment = commentReport.getComment();
        Post post = comment.getPost();
        Board board = post.getBoard();

        MockHttpServletRequestBuilder requestBuilder = get("/v1/report/comment/boards/{boardId}/posts/{postId}/comments/{commentId}/reports/{reportId}", board.getId(), post.getId(), comment.getId(), commentReport.getId());
        setAuthToken(requestBuilder, allBoardAdmin);

        List<FieldDescriptor> fieldDescriptorList = new LinkedList<>();
        fieldDescriptorList.addAll(IricomFieldsSnippet.getCommentReport(""));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getComment("comment."));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getAccount("comment.account."));

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(200))
                .andDo(print())
                .andDo(document("RC_005",
                        preprocessRequest(
                                removeHeaders("Authorization"),
                                prettyPrint()
                        ),
                        preprocessResponse(
                                prettyPrint()
                        ),
                        pathParameters(
                                parameterWithName("boardId").description("게시판 아이디"),
                                parameterWithName("postId").description("게시물 아이디"),
                                parameterWithName("commentId").description("댓글 아이디"),
                                parameterWithName("reportId").description("신고 아이디")
                        ),
                        requestHeaders(
//                                headerWithName("Authorization").description("firebase 토큰")
                        ),
                        responseFields(
                                fieldDescriptorList.toArray(FieldDescriptor[]::new)
                        )
                ));
    }
}
