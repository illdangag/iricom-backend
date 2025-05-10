package com.illdangag.iricom.server.restdocs.v1;

import com.illdangag.iricom.core.restdocs.snippet.IricomFieldsSnippet;
import com.illdangag.iricom.core.test.data.wrapper.TestAccountInfo;
import com.illdangag.iricom.core.test.data.wrapper.TestBoardInfo;
import com.illdangag.iricom.core.test.data.wrapper.TestCommentInfo;
import com.illdangag.iricom.core.test.data.wrapper.TestPostInfo;
import com.illdangag.iricom.server.IricomTestServiceSuite;
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
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("restdoc: 차단")
public class BlockControllerTestCore extends IricomTestServiceSuite {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    public BlockControllerTestCore(ApplicationContext context) {
        super(context);
    }

    @Test
    @DisplayName("게시물 차단")
    public void bp001() throws Exception {
        // 계정 생성
        TestAccountInfo account = setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = setRandomBoard();
        // 게시물 생성
        TestPostInfo post = setRandomPost(board, account);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("reason", "This is blocked.");

        MockHttpServletRequestBuilder requestBuilder = post("/v1/block/post/boards/{boardId}/posts/{postId}", board.getId(), post.getId())
                .content(getJsonString(requestBody))
                .contentType(MediaType.APPLICATION_JSON);
        setAuthToken(requestBuilder, systemAdmin);

        List<FieldDescriptor> fieldDescriptorList = new LinkedList<>();
        fieldDescriptorList.addAll(IricomFieldsSnippet.getPostBlock(""));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getPost("post."));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getAccount("post.account."));

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(200))
                .andDo(print())
                .andDo(document("BP_001",
                        preprocessRequest(
                                modifyUris().scheme("https").host("api.iricom.com").removePort(),
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
                                fieldWithPath("reason").description("사유")
                        ),
                        responseFields(fieldDescriptorList.toArray(FieldDescriptor[]::new))
                ));
    }

    @Test
    @DisplayName("차단된 게시물 목록 조회")
    public void bp002() throws Exception {
        // 계정 생성
        TestAccountInfo account = setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = setRandomBoard();
        // 게시물 생성
        List<TestPostInfo> postList = setRandomPost(board, account, 5);
        // 게시물 차단
        for (TestPostInfo post : postList) {
            setRandomPostBlock(post, "already blocked");
        }

        MockHttpServletRequestBuilder requestBuilder = get("/v1/block/post/boards")
                .param("skip", "0")
                .param("limit", "5")
                .param("reason", "already");
        setAuthToken(requestBuilder, systemAdmin);

        List<FieldDescriptor> fieldDescriptorList = new LinkedList<>();
        fieldDescriptorList.addAll(IricomFieldsSnippet.getSearchList(""));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getPostBlock("blocks.[]."));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getPost("blocks.[].post."));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getAccount("blocks.[].post.account."));

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(200))
                .andDo(print())
                .andDo(document("BP_002",
                        preprocessRequest(
                                modifyUris().scheme("https").host("api.iricom.com").removePort(),
                                removeHeaders("Authorization"),
                                prettyPrint()
                        ),
                        preprocessResponse(
                                prettyPrint()
                        ),
                        requestHeaders(
//                                headerWithName("Authorization").description("firebase 토큰")
                        ),
                        requestParameters(
                                parameterWithName("skip").description("건너 뛸 수"),
                                parameterWithName("limit").description("최대 조회 수"),
                                parameterWithName("reason").description("사유")
                        ),
                        responseFields(fieldDescriptorList.toArray(FieldDescriptor[]::new))
                ));
    }

    @Test
    @DisplayName("차단된 게시물 목록 조회")
    public void bp003() throws Exception {
        // 계정 생성
        TestAccountInfo account = setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = setRandomBoard();
        // 게시물 생성
        List<TestPostInfo> postList = setRandomPost(board, account, 5);
        // 게시물 차단
        for (TestPostInfo post : postList) {
            setRandomPostBlock(post, "already blocked");
        }

        MockHttpServletRequestBuilder requestBuilder = get("/v1/block/post/boards/{boardId}", board.getId())
                .param("skip", "0")
                .param("limit", "20")
                .param("reason", "already");
        setAuthToken(requestBuilder, systemAdmin);

        List<FieldDescriptor> fieldDescriptorList = new LinkedList<>();
        fieldDescriptorList.addAll(IricomFieldsSnippet.getSearchList(""));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getPostBlock("blocks.[]."));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getPost("blocks.[].post."));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getAccount("blocks.[].post.account."));

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(200))
                .andDo(print())
                .andDo(document("BP_003",
                        preprocessRequest(
                                modifyUris().scheme("https").host("api.iricom.com").removePort(),
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
                                parameterWithName("reason").description("사유")
                        ),
                        responseFields(fieldDescriptorList.toArray(FieldDescriptor[]::new))
                ));
    }

    @Test
    @DisplayName("차단된 게시물 정보 조회")
    public void bp004() throws Exception {
        // 계정 생성
        TestAccountInfo account = setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = setRandomBoard();
        // 게시물 생성
        TestPostInfo post = setRandomPost(board, account);
        // 게시물 차단
        setRandomPostBlock(post, "already blocked");

        MockHttpServletRequestBuilder requestBuilder = get("/v1/block/post/boards/{boardId}/posts/{postId}", board.getId(), post.getId());
        setAuthToken(requestBuilder, systemAdmin);

        List<FieldDescriptor> fieldDescriptorList = new LinkedList<>();
        fieldDescriptorList.addAll(IricomFieldsSnippet.getPostBlock(""));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getPost("post."));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getAccount("post.account."));

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(200))
                .andDo(print())
                .andDo(document("BP_004",
                        preprocessRequest(
                                modifyUris().scheme("https").host("api.iricom.com").removePort(),
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
                        responseFields(fieldDescriptorList.toArray(FieldDescriptor[]::new))
                ));
    }

    @Test
    @DisplayName("게시물 차단 정보 수정")
    public void bp005() throws Exception {
        // 계정 생성
        TestAccountInfo account = setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = setRandomBoard();
        // 게시물 생성
        TestPostInfo post = setRandomPost(board, account);
        // 게시물 차단
        setRandomPostBlock(post, "already blocked");

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("reason", "Reason update.");

        MockHttpServletRequestBuilder requestBuilder = patch("/v1/block/post/boards/{boardId}/posts/{postId}", board.getId(), post.getId())
                .content(getJsonString(requestBody))
                .contentType(MediaType.APPLICATION_JSON);
        setAuthToken(requestBuilder, systemAdmin);

        List<FieldDescriptor> fieldDescriptorList = new LinkedList<>();
        fieldDescriptorList.addAll(IricomFieldsSnippet.getPostBlock(""));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getPost("post."));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getAccount("post.account."));

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(200))
                .andDo(print())
                .andDo(document("BP_005",
                        preprocessRequest(
                                modifyUris().scheme("https").host("api.iricom.com").removePort(),
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
                                fieldWithPath("reason").description("사유")
                        ),
                        responseFields(fieldDescriptorList.toArray(FieldDescriptor[]::new))
                ));
    }

    @Test
    @DisplayName("게시물 차단 해제")
    public void bp006() throws Exception {
        // 계정 생성
        TestAccountInfo account = setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = setRandomBoard();
        // 게시물 생성
        TestPostInfo post = setRandomPost(board, account);
        // 게시물 차단
        setRandomPostBlock(post, "already blocked");

        MockHttpServletRequestBuilder requestBuilder = delete("/v1/block/post/boards/{boardId}/posts/{postId}", board.getId(), post.getId());
        setAuthToken(requestBuilder, systemAdmin);

        List<FieldDescriptor> fieldDescriptorList = new LinkedList<>();
        fieldDescriptorList.addAll(IricomFieldsSnippet.getPostBlock(""));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getPost("post."));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getAccount("post.account."));

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(200))
                .andDo(print())
                .andDo(document("BP_006",
                        preprocessRequest(
                                modifyUris().scheme("https").host("api.iricom.com").removePort(),
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
                        responseFields(fieldDescriptorList.toArray(FieldDescriptor[]::new))
                ));
    }

    @Test
    @DisplayName("댓글 차단")
    public void bc001() throws Exception {
        // 계정 생성
        TestAccountInfo account = setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = setRandomBoard();
        // 게시물 생성
        TestPostInfo post = setRandomPost(board, account);
        // 댓글 생성
        TestCommentInfo comment = setRandomComment(post, account);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("reason", "This is block.");

        MockHttpServletRequestBuilder requestBuilder = post("/v1/block/comment/boards/{boardId}/posts/{postId}/comments/{commentId}", board.getId(), post.getId(), comment.getId())
                .content(getJsonString(requestBody))
                .contentType(MediaType.APPLICATION_JSON);
        setAuthToken(requestBuilder, systemAdmin);

        List<FieldDescriptor> fieldDescriptorList = new LinkedList<>();
        fieldDescriptorList.addAll(IricomFieldsSnippet.getCommentBlock(""));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getComment("comment."));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getAccount("comment.account."));

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(200))
                .andDo(print())
                .andDo(document("BC_001",
                        preprocessRequest(
                                modifyUris().scheme("https").host("api.iricom.com").removePort(),
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
                                fieldWithPath("reason").description("사유")
                        ),
                        responseFields(fieldDescriptorList.toArray(FieldDescriptor[]::new))
                ));
    }
}
