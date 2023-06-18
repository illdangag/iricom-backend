package com.illdangag.iricom.server.restdocs.v1;

import com.illdangag.iricom.server.data.entity.Board;
import com.illdangag.iricom.server.data.entity.Comment;
import com.illdangag.iricom.server.data.entity.Post;
import com.illdangag.iricom.server.restdocs.snippet.IricomFieldsSnippet;
import com.illdangag.iricom.server.test.IricomTestSuite;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
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

@DisplayName("restdoc: 댓글")
public class CommentControllerTest extends IricomTestSuite {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    public CommentControllerTest(ApplicationContext context) {
        super(context);
    }

    @Test
    @DisplayName("생성")
    @Order(0)
    public void cm001() throws Exception {
        Board board = getBoard(commentBoard);
        Post post = getPost(commentUpdatePost00);
        Comment comment = getComment(commentUpdateComment00);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("content", "this is content.");
        requestBody.put("referenceCommentId", comment.getId().toString());

        MockHttpServletRequestBuilder requestBuilder = post("/v1/boards/{boardId}/posts/{postId}/comments", board.getId(), post.getId())
                .content(getJsonString(requestBody))
                .contentType(MediaType.APPLICATION_JSON);
        setAuthToken(requestBuilder, common01);

        List<FieldDescriptor> fieldDescriptorList = new LinkedList<>();
        fieldDescriptorList.addAll(IricomFieldsSnippet.getComment("", true));
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
//                                        headerWithName("Authorization").description("firebase 토큰")
                                ),
                                requestFields(
                                        fieldWithPath("content").description("내용"),
                                        fieldWithPath("referenceCommentId").description("상위 댓글 아이디")
                                ),
                                responseFields(fieldDescriptorList.toArray(FieldDescriptor[]::new))
                        )

                );
    }

    @Test
    @DisplayName("목록 조회")
    @Order(1)
    public void cm002() throws Exception {
        Board board = getBoard(commentBoard);
        Post post = getPost(commentGetPost00);
        Comment comment = getComment(commentGetComment00);

        MockHttpServletRequestBuilder requestBuilder = get("/v1/boards/{boardId}/posts/{postId}/comments", board.getId(), post.getId())
                .param("skip", "0")
                .param("limit", "1")
                .param("keyword", "")
                .param("includeComment", "true")
                .param("referenceCommentId", "" + comment.getId())
                .param("includeCommentLimit", "5");
        setAuthToken(requestBuilder, common00);

        List<FieldDescriptor> fieldDescriptorList = new LinkedList<>();
        fieldDescriptorList.addAll(IricomFieldsSnippet.getSearchList(""));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getComment("comments.[].", true));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getAccount("comments.[].account."));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getComment("comments.[].nestedComments.[].", true));
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
//                                        headerWithName("Authorization").description("firebase 토큰")
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
    @Order(1)
    public void cm003() throws Exception {
        Board board = getBoard(commentBoard);
        Post post = getPost(commentGetPost00);
        Comment comment = getComment(commentGetComment04);

        MockHttpServletRequestBuilder requestBuilder = get("/v1/boards/{boardId}/posts/{postId}/comments/{commentId}", board.getId(), post.getId(), comment.getId());
        setAuthToken(requestBuilder, common00);

        List<FieldDescriptor> fieldDescriptorList = new LinkedList<>();
        fieldDescriptorList.addAll(IricomFieldsSnippet.getComment("", true));
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
//                                        headerWithName("Authorization").description("firebase 토큰")
                        ),
                        responseFields(fieldDescriptorList.toArray(FieldDescriptor[]::new))
                ));
    }

    @Test
    @DisplayName("수정")
    @Order(2)
    public void cm004() throws Exception {
        Board board = getBoard(commentBoard);
        Post post = getPost(commentUpdatePost00);
        Comment comment = getComment(commentUpdateComment00);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("content", "update_comment");

        MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/{boardId}/posts/{postId}/comments/{commentId}", board.getId(), post.getId(), comment.getId())
                .content(getJsonString(requestBody))
                .contentType(MediaType.APPLICATION_JSON);
        setAuthToken(requestBuilder, common00);

        List<FieldDescriptor> fieldDescriptorList = new LinkedList<>();
        fieldDescriptorList.addAll(IricomFieldsSnippet.getComment("", true));
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
//                                        headerWithName("Authorization").description("firebase 토큰")
                        ),
                        requestFields(
                                fieldWithPath("content").description("내용")
                        ),
                        responseFields(fieldDescriptorList.toArray(FieldDescriptor[]::new))
                ));
    }

    @Test
    @DisplayName("삭제")
    @Order(3)
    public void cm005() throws Exception {
        Board board = getBoard(commentBoard);
        Post post = getPost(commentUpdatePost00);
        Comment comment = getComment(commentDeleteComment00);

        MockHttpServletRequestBuilder requestBuilder = delete("/v1/boards/{boardId}/posts/{postId}/comments/{commentId}", board.getId(), post.getId(), comment.getId());
        setAuthToken(requestBuilder, common00);

        List<FieldDescriptor> fieldDescriptorList = new LinkedList<>();
        fieldDescriptorList.addAll(IricomFieldsSnippet.getComment("", false));

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
//                                        headerWithName("Authorization").description("firebase 토큰")
                        ),
                        responseFields(fieldDescriptorList.toArray(FieldDescriptor[]::new))
                ));
    }

    @Test
    @DisplayName("좋아요 싫어요")
    @Order(4)
    public void cm006() throws Exception {
        Board board = getBoard(voteBoard);
        Post post = getPost(voteCommentPost00);
        Comment comment = getComment(voteComment05);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("type", "upvote");

        MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/{boardId}/posts/{postId}/comments/{commentId}/vote", board.getId(), post.getId(), comment.getId())
                .content(getJsonString(requestBody))
                .contentType(MediaType.APPLICATION_JSON);
        setAuthToken(requestBuilder, common00);

        List<FieldDescriptor> fieldDescriptorList = new LinkedList<>();
        fieldDescriptorList.addAll(IricomFieldsSnippet.getComment("", true));
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
//                                        headerWithName("Authorization").description("firebase 토큰")
                        ),
                        requestFields(
                                fieldWithPath("type").description("종류 (upvote: 좋아요, downvote: 싫어요)")
                        ),
                        responseFields(fieldDescriptorList.toArray(FieldDescriptor[]::new))
                ));
    }
}
