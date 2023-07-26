package com.illdangag.iricom.server.restdocs.v1;

import com.illdangag.iricom.server.data.entity.*;
import com.illdangag.iricom.server.restdocs.snippet.IricomFieldsSnippet;
import com.illdangag.iricom.server.test.IricomTestSuite;
import com.illdangag.iricom.server.test.data.wrapper.TestBoardInfo;
import com.illdangag.iricom.server.test.data.wrapper.TestCommentInfo;
import com.illdangag.iricom.server.test.data.wrapper.TestPostInfo;
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
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("restdoc: 댓글")
public class CommentControllerTest extends IricomTestSuite {
    @Autowired
    MockMvc mockMvc;

    private static final TestBoardInfo testBoardInfo00 = TestBoardInfo.builder()
            .title("testBoardInfo00").isEnabled(true).adminList(Collections.singletonList(allBoardAdmin)).build();

    private static final TestPostInfo testPostInfo00 = TestPostInfo.builder()
            .title("testPostInfo00").content("content").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .creator(common00).board(testBoardInfo00).build();

    private static final TestCommentInfo testCommentInfo00 = TestCommentInfo.builder()
            .content("testCommentInfo00").creator(common00).post(testPostInfo00)
            .build();
    private static final TestCommentInfo testCommentInfo01 = TestCommentInfo.builder()
            .content("testCommentInfo01").creator(common00).post(testPostInfo00)
            .build();
    private static final TestCommentInfo testCommentInfo02 = TestCommentInfo.builder()
            .content("testCommentInfo02").creator(common00).post(testPostInfo00)
            .build();
    private static final TestCommentInfo testCommentInfo03 = TestCommentInfo.builder()
            .content("testCommentInfo03").creator(common00).post(testPostInfo00)
            .referenceComment(testCommentInfo00).build();

    @Autowired
    public CommentControllerTest(ApplicationContext context) {
        super(context);

        List<TestBoardInfo> testBoardInfoList = Arrays.asList(testBoardInfo00);
        List<TestPostInfo> testPostInfoList = Arrays.asList(testPostInfo00);
        List<TestCommentInfo> testCommentInfoList = Arrays.asList(testCommentInfo00, testCommentInfo01,
                testCommentInfo02, testCommentInfo03);

        super.setBoard(testBoardInfoList);
        super.setPost(testPostInfoList);
        super.setComment(testCommentInfoList);
    }

    @Test
    @DisplayName("생성")
    public void cm001() throws Exception {
        String commentId = getCommentId(testCommentInfo00);
        String postId = getPostId(testCommentInfo00.getPost());
        String boardId = getBoardId(testCommentInfo00.getPost().getBoard());

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("content", "this is content.");
        requestBody.put("referenceCommentId", commentId);

        MockHttpServletRequestBuilder requestBuilder = post("/v1/boards/{boardId}/posts/{postId}/comments", boardId, postId)
                .content(getJsonString(requestBody))
                .contentType(MediaType.APPLICATION_JSON);
        setAuthToken(requestBuilder, common01);

        List<FieldDescriptor> fieldDescriptorList = new LinkedList<>();
        fieldDescriptorList.addAll(IricomFieldsSnippet.getComment(""));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getAccount("account."));

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(200))
                .andDo(print())
                .andDo(document("CM_001", preprocessRequest(
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
                                fieldWithPath("content").description("내용"),
                                fieldWithPath("referenceCommentId").description("상위 댓글 아이디")
                        ),
                        responseFields(fieldDescriptorList.toArray(FieldDescriptor[]::new))
                ));
    }

    @Test
    @DisplayName("목록 조회")
    public void cm002() throws Exception {
        String commentId = getCommentId(testCommentInfo00);
        String postId = getPostId(testCommentInfo00.getPost());
        String boardId = getBoardId(testCommentInfo00.getPost().getBoard());

        MockHttpServletRequestBuilder requestBuilder = get("/v1/boards/{boardId}/posts/{postId}/comments", boardId, postId)
                .param("skip", "0")
                .param("limit", "5")
                .param("keyword", "")
                .param("includeComment", "true")
                .param("referenceCommentId", commentId)
                .param("includeCommentLimit", "5");
        setAuthToken(requestBuilder, common00);

        List<FieldDescriptor> fieldDescriptorList = new LinkedList<>();
        fieldDescriptorList.addAll(IricomFieldsSnippet.getSearchList(""));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getComment("comments.[]."));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getAccount("comments.[].account."));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getComment("comments.[].nestedComments.[]."));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getAccount("comments.[].nestedComments.[].account."));

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(200))
                .andDo(print())
                .andDo(document("CM_002",
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
                                parameterWithName("keyword").description("검색어"),
                                parameterWithName("includeComment").description("대댓글 포함 여부"),
                                parameterWithName("referenceCommentId").description("댓글 기준 대댓글 조회"),
                                parameterWithName("includeCommentLimit").description("대댓글의 최대 조회 수")
                        ),
                        responseFields(fieldDescriptorList.toArray(FieldDescriptor[]::new))
                ));
    }

    @Test
    @DisplayName("정보 조회")
    public void cm003() throws Exception {
        String commentId = getCommentId(testCommentInfo00);
        String postId = getPostId(testCommentInfo00.getPost());
        String boardId = getBoardId(testCommentInfo00.getPost().getBoard());

        MockHttpServletRequestBuilder requestBuilder = get("/v1/boards/{boardId}/posts/{postId}/comments/{commentId}", boardId, postId, commentId);
        setAuthToken(requestBuilder, common00);

        List<FieldDescriptor> fieldDescriptorList = new LinkedList<>();
        fieldDescriptorList.addAll(IricomFieldsSnippet.getComment(""));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getAccount("account."));

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(200))
                .andDo(print())
                .andDo(document("CM_003",
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
                        responseFields(fieldDescriptorList.toArray(FieldDescriptor[]::new))
                ));
    }

    @Test
    @DisplayName("수정")
    public void cm004() throws Exception {
        String commentId = getCommentId(testCommentInfo00);
        String postId = getPostId(testCommentInfo00.getPost());
        String boardId = getBoardId(testCommentInfo00.getPost().getBoard());

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("content", "update_comment");

        MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/{boardId}/posts/{postId}/comments/{commentId}", boardId, postId, commentId)
                .content(getJsonString(requestBody))
                .contentType(MediaType.APPLICATION_JSON);
        setAuthToken(requestBuilder, common00);

        List<FieldDescriptor> fieldDescriptorList = new LinkedList<>();
        fieldDescriptorList.addAll(IricomFieldsSnippet.getComment(""));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getAccount("account."));

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(200))
                .andDo(print())
                .andDo(document("CM_004",
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
                                fieldWithPath("content").description("내용")
                        ),
                        responseFields(fieldDescriptorList.toArray(FieldDescriptor[]::new))
                ));
    }

    @Test
    @DisplayName("삭제")
    public void cm005() throws Exception {
        String commentId = getCommentId(testCommentInfo01);
        String postId = getPostId(testCommentInfo01.getPost());
        String boardId = getBoardId(testCommentInfo01.getPost().getBoard());

        MockHttpServletRequestBuilder requestBuilder = delete("/v1/boards/{boardId}/posts/{postId}/comments/{commentId}", boardId, postId, commentId);
        setAuthToken(requestBuilder, common00);

        List<FieldDescriptor> fieldDescriptorList = new LinkedList<>();
        fieldDescriptorList.addAll(IricomFieldsSnippet.getComment(""));

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(200))
                .andDo(print())
                .andDo(document("CM_005",
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
                        responseFields(fieldDescriptorList.toArray(FieldDescriptor[]::new))
                ));
    }

    @Test
    @DisplayName("좋아요 싫어요")
    public void cm006() throws Exception {
        String commentId = getCommentId(testCommentInfo02);
        String postId = getPostId(testCommentInfo02.getPost());
        String boardId = getBoardId(testCommentInfo02.getPost().getBoard());

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("type", "upvote");

        MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/{boardId}/posts/{postId}/comments/{commentId}/vote", boardId, postId, commentId)
                .content(getJsonString(requestBody))
                .contentType(MediaType.APPLICATION_JSON);
        setAuthToken(requestBuilder, common00);

        List<FieldDescriptor> fieldDescriptorList = new LinkedList<>();
        fieldDescriptorList.addAll(IricomFieldsSnippet.getComment(""));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getAccount("account."));

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(200))
                .andDo(print())
                .andDo(document("CM_006",
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
                                fieldWithPath("type").description("종류 (upvote: 좋아요, downvote: 싫어요)")
                        ),
                        responseFields(fieldDescriptorList.toArray(FieldDescriptor[]::new))
                ));
    }
}
