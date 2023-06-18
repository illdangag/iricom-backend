package com.illdangag.iricom.server.restdocs.v1;

import com.illdangag.iricom.server.data.entity.*;
import com.illdangag.iricom.server.restdocs.snippet.IricomFieldsSnippet;
import com.illdangag.iricom.server.test.IricomTestSuite;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
    MockMvc mockMvc;

    @Autowired
    public ReportControllerTest(ApplicationContext context) {
        super(context);
    }

    @Test
    @DisplayName("게시물 신고")
    public void rp001() throws Exception {
        Post post = getPost(reportPost02);
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
        fieldDescriptorList.addAll(IricomFieldsSnippet.getPost("post.", false));
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
//                                        headerWithName("Authorization").description("firebase 토큰")
                                ),
                                requestFields(
                                        fieldWithPath("type").description("종류"),
                                        fieldWithPath("reason").description("사유")
                                ),
                                responseFields(fieldDescriptorList.toArray(FieldDescriptor[]::new))
                        )
                );
    }

    @Test
    @DisplayName("게시물 신고 목록 조회 (게시판)")
    public void rp002() throws Exception {
        Board board = getBoard(reportSearchBoard);

        MockHttpServletRequestBuilder requestBuilder = get("/v1/report/post/boards/{boardId}", board.getId())
                .param("skip", "0")
                .param("limit", "20")
                .param("type", "hate")
                .param("reason", "report");
        setAuthToken(requestBuilder, allBoardAdmin);

        List<FieldDescriptor> fieldDescriptorList = new LinkedList<>();
        fieldDescriptorList.addAll(IricomFieldsSnippet.getSearchList(""));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getPostReport("reports.[]."));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getPost("reports.[].post.", false));
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
//                                        headerWithName("Authorization").description("firebase 토큰")
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
                        )
                );
    }

    @Test
    @DisplayName("게시물 신고 목록 조회 (게시판, 게시물)")
    public void rp003() throws Exception {
        Post post = getPost(reportedSearchPost00);
        Board board = post.getBoard();

        MockHttpServletRequestBuilder requestBuilder = get("/v1/report/post/boards/{boardId}/posts/{postId}", board.getId(), post.getId())
                .param("skip", "0")
                .param("limit", "20")
                .param("type", "hate")
                .param("reason", "report");
        setAuthToken(requestBuilder, allBoardAdmin);

        List<FieldDescriptor> fieldDescriptorList = new LinkedList<>();
        fieldDescriptorList.addAll(IricomFieldsSnippet.getSearchList(""));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getPostReport("reports.[]."));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getPost("reports.[].post.", false));
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
//                                        headerWithName("Authorization").description("firebase 토큰")
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
                        )
                );
    }

    @Test
    @DisplayName("게시물 신고 정보 조회")
    public void rp004() throws Exception {
        PostReport postReport = getPostReport(postReportSearch00);
        Post post = postReport.getPost();
        Board board = post.getBoard();

        MockHttpServletRequestBuilder requestBuilder = get("/v1/report/post/boards/{boardId}/posts/{postId}/reports/{reportId}", board.getId(), post.getId(), postReport.getId())
                .param("skip", "0")
                .param("limit", "20")
                .param("type", "hate")
                .param("reason", "report");
        setAuthToken(requestBuilder, allBoardAdmin);

        List<FieldDescriptor> fieldDescriptorList = new LinkedList<>();
        fieldDescriptorList.addAll(IricomFieldsSnippet.getPostReport(""));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getPost("post.", false));
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
//                                        headerWithName("Authorization").description("firebase 토큰")
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
                        )
                );
    }

    @Test
    @DisplayName("댓글 신고")
    public void rc001() throws Exception {
        Comment comment = getComment(reportComment00);
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
        fieldDescriptorList.addAll(IricomFieldsSnippet.getComment("comment.", true));
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
//                                        headerWithName("Authorization").description("firebase 토큰")
                                ),
                                requestFields(
                                        fieldWithPath("type").description("종류"),
                                        fieldWithPath("reason").description("사유")
                                ),
                                responseFields(
                                        fieldDescriptorList.toArray(FieldDescriptor[]::new)
                                )
                        )
                );
    }

    @Test
    @DisplayName("게시물 신고 목록 조회 (게시판)")
    public void rc002() throws Exception {
        CommentReport commentReport = getCommentReport(commentReportSearch00);
        Comment comment = commentReport.getComment();
        Post post = comment.getPost();
        Board board = post.getBoard();

        MockHttpServletRequestBuilder requestBuilder = get("/v1/report/comment/boards/{boardId}", board.getId())
                .param("skip", "0")
                .param("limit", "20")
                .param("type", "hate")
                .param("reason", "comment");
        setAuthToken(requestBuilder, allBoardAdmin);

        List<FieldDescriptor> fieldDescriptorList = new LinkedList<>();
        fieldDescriptorList.addAll(IricomFieldsSnippet.getSearchList(""));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getCommentReport("reports.[]."));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getComment("reports.[].comment.", true));
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
//                                        headerWithName("Authorization").description("firebase 토큰")
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
                        )
                );
    }

    @Test
    @DisplayName("게시물 신고 목록 조회 (게시판, 게시물)")
    public void rc003() throws Exception {
        CommentReport commentReport = getCommentReport(commentReportSearch00);
        Comment comment = commentReport.getComment();
        Post post = comment.getPost();
        Board board = post.getBoard();

        MockHttpServletRequestBuilder requestBuilder = get("/v1/report/comment/boards/{boardId}/posts/{postId}", board.getId(), post.getId())
                .param("skip", "0")
                .param("limit", "20")
                .param("type", "hate")
                .param("reason", "comment");
        setAuthToken(requestBuilder, allBoardAdmin);

        List<FieldDescriptor> fieldDescriptorList = new LinkedList<>();
        fieldDescriptorList.addAll(IricomFieldsSnippet.getSearchList(""));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getCommentReport("reports.[]."));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getComment("reports.[].comment.", true));
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
//                                        headerWithName("Authorization").description("firebase 토큰")
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
                        )
                );
    }

    @Test
    @DisplayName("게시물 신고 목록 조회 (게시판, 게시물, 댓글)")
    public void rc004() throws Exception {
        CommentReport commentReport = getCommentReport(commentReportSearch00);
        Comment comment = commentReport.getComment();
        Post post = comment.getPost();
        Board board = post.getBoard();

        MockHttpServletRequestBuilder requestBuilder = get("/v1/report/comment/boards/{boardId}/posts/{postId}/comments/{commentId}", board.getId(), post.getId(), comment.getId())
                .param("skip", "0")
                .param("limit", "20")
                .param("type", "hate")
                .param("reason", "comment");
        setAuthToken(requestBuilder, allBoardAdmin);

        List<FieldDescriptor> fieldDescriptorList = new LinkedList<>();
        fieldDescriptorList.addAll(IricomFieldsSnippet.getSearchList(""));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getCommentReport("reports.[]."));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getComment("reports.[].comment.", true));
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
//                                        headerWithName("Authorization").description("firebase 토큰")
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
                        )
                );
    }
}
