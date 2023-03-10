package com.illdangag.iricom.server.restdocs.v1;

import com.illdangag.iricom.server.data.entity.Board;
import com.illdangag.iricom.server.data.entity.Comment;
import com.illdangag.iricom.server.data.entity.Post;
import com.illdangag.iricom.server.test.IricomTestSuite;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("restdoc: λκΈ")
public class CommentControllerTest extends IricomTestSuite {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    public CommentControllerTest(ApplicationContext context) {
        super(context);
    }

    @Test
    @DisplayName("μμ±")
    @Order(0)
    public void testCase00() throws Exception {
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
                                        parameterWithName("boardId").description("κ²μν μμ΄λ"),
                                        parameterWithName("postId").description("κ²μλ¬Ό μμ΄λ")
                                ),
                                requestHeaders(
//                                        headerWithName("Authorization").description("firebase ν ν°")
                                ),
                                requestFields(
                                        fieldWithPath("content").description("λ΄μ©"),
                                        fieldWithPath("referenceCommentId").description("μμ λκΈ μμ΄λ")
                                ),
                                responseFields(
                                        fieldWithPath("id").description("μμ΄λ"),
                                        fieldWithPath("content").description("λ΄μ©"),
                                        fieldWithPath("referenceCommentId").description("μμ λκΈ μμ΄λ"),
                                        fieldWithPath("createDate").description("μμ±μΌ"),
                                        fieldWithPath("updateDate").description("μμ μΌ").optional().type(JsonFieldType.NUMBER),
                                        fieldWithPath("upvote").description("μ’μμ"),
                                        fieldWithPath("downvote").description("μ«μ΄μ"),
                                        fieldWithPath("hasNestedComment").description("νμ λκΈ μ¬λΆ"),
                                        fieldWithPath("deleted").description("μ­μ  μ¬λΆ"),
                                        fieldWithPath("account.id").description("μμ±μ μμ΄λ,"),
                                        fieldWithPath("account.email").description("μμ±μ μ΄λ©μΌ"),
                                        fieldWithPath("account.createDate").description("μμ±μ μμ±μΌ"),
                                        fieldWithPath("account.lastActivityDate").description("μμ±μ μ΅κ·Ό νλμΌ"),
                                        fieldWithPath("account.nickname").description("μμ±μ λλ€μ"),
                                        fieldWithPath("account.description").description("μμ±μ μ€λͺ"),
                                        fieldWithPath("account.auth").description("μμ±μ κΆν")
                                )
                        )

                );
    }

    @Test
    @DisplayName("λͺ©λ‘ μ‘°ν")
    @Order(1)
    public void testCase01() throws Exception {
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
                                parameterWithName("boardId").description("κ²μν μμ΄λ"),
                                parameterWithName("postId").description("κ²μλ¬Ό μμ΄λ")
                        ),
                        requestHeaders(
//                                        headerWithName("Authorization").description("firebase ν ν°")
                        ),
                        requestParameters(
                                parameterWithName("skip").description("κ±΄λ λΈ μ"),
                                parameterWithName("limit").description("μ΅λ μ‘°ν μ"),
                                parameterWithName("keyword").description("κ²μμ΄"),
                                parameterWithName("includeComment").description("λλκΈ ν¬ν¨ μ¬λΆ"),
                                parameterWithName("referenceCommentId").description("λκΈ κΈ°μ€ λλκΈ μ‘°ν"),
                                parameterWithName("includeCommentLimit").description("λλκΈμ μ΅λ μ‘°ν μ")
                        ),
                        responseFields(
                                fieldWithPath("total").description("λͺ¨λ  κ²°κ³Όμ μ"),
                                fieldWithPath("skip").description("κ±΄λ λΈ κ²°κ³Ό μ"),
                                fieldWithPath("limit").description("μ‘°ν ν  μ΅λ κ²°κ³Ό μ"),
                                fieldWithPath("comments").description("λκΈ λͺ©λ‘"),
                                fieldWithPath("comments.[].id").description("μμ΄λ"),
                                fieldWithPath("comments.[].content").description("λ΄μ©"),
                                fieldWithPath("comments.[].createDate").description("μμ±μΌ"),
                                fieldWithPath("comments.[].updateDate").description("μμ μΌ").optional().type(JsonFieldType.NUMBER),
                                fieldWithPath("comments.[].upvote").description("μ’μμ"),
                                fieldWithPath("comments.[].downvote").description("μ«μ΄μ"),
                                fieldWithPath("comments.[].hasNestedComment").description("νμ λκΈ ν¬ν¨ μ¬λΆ"),
                                fieldWithPath("comments.[].deleted").description("μ­μ  μ¬λΆ"),
                                fieldWithPath("comments.[].referenceCommentId").description("μμ λκΈ μμ΄λ"),
                                fieldWithPath("comments.[].account").description("λκΈ μμ±μ"),
                                fieldWithPath("comments.[].account.id").description("λκΈ μμ±μ μμ΄λ"),
                                fieldWithPath("comments.[].account.email").description("λκΈ μμ±μ μ΄λ©μΌ"),
                                fieldWithPath("comments.[].account.createDate").description("λκΈ μμ±μ μμ±μΌ"),
                                fieldWithPath("comments.[].account.lastActivityDate").description("λκΈ μμ±μ μ΅κ·Ό νλμΌ"),
                                fieldWithPath("comments.[].account.nickname").description("λκΈ μμ±μ λλ€μ"),
                                fieldWithPath("comments.[].account.description").description("λκΈ μμ±μ μ€λͺ"),
                                fieldWithPath("comments.[].account.auth").description("λκΈ μμ±μ κΆν"),
                                fieldWithPath("comments.[].nestedComments").description("νμ λκΈ λͺ©λ‘"),
                                fieldWithPath("comments.[].nestedComments.[].id").description("νμ λκΈ μμ΄λ"),
                                fieldWithPath("comments.[].nestedComments.[].content").description("νμ λκΈ λ΄μ©"),
                                fieldWithPath("comments.[].nestedComments.[].referenceCommentId").description("νμ λκΈμ λΆλͺ¨ λκΈ μμ΄λ"),
                                fieldWithPath("comments.[].nestedComments.[].createDate").description("νμ λκΈμ μμ±μΌ"),
                                fieldWithPath("comments.[].nestedComments.[].updateDate").description("νμ λκΈμ μμ μΌ").optional().type(JsonFieldType.NUMBER),
                                fieldWithPath("comments.[].nestedComments.[].upvote").description("νμ λκΈμ μ’μμ"),
                                fieldWithPath("comments.[].nestedComments.[].downvote").description("νμ λκΈμ μ«μ΄μ"),
                                fieldWithPath("comments.[].nestedComments.[].hasNestedComment").description("νμ λκΈμ λλκΈ ν¬ν¨ μ¬λΆ"),
                                fieldWithPath("comments.[].nestedComments.[].deleted").description("νμ λκΈμ μ­μ  μ¬λΆ"),
                                fieldWithPath("comments.[].nestedComments.[].account").description("νμ λκΈ μμ±μ"),
                                fieldWithPath("comments.[].nestedComments.[].account.id").description("νμ λκΈ μμ±μ μμ΄λ"),
                                fieldWithPath("comments.[].nestedComments.[].account.email").description("νμ λκΈ μμ±μ μ΄λ©μΌ"),
                                fieldWithPath("comments.[].nestedComments.[].account.createDate").description("νμ λκΈ μμ±μ μμ±μΌ"),
                                fieldWithPath("comments.[].nestedComments.[].account.lastActivityDate").description("νμ λκΈ μμ±μ μ΅κ·Ό νλμΌ"),
                                fieldWithPath("comments.[].nestedComments.[].account.nickname").description("νμ λκΈ μμ±μ λλ€μ"),
                                fieldWithPath("comments.[].nestedComments.[].account.description").description("νμ λκΈ μμ±μ μ€λͺ"),
                                fieldWithPath("comments.[].nestedComments.[].account.auth").description("νμ λκΈ μμ±μ κΆν")
                        )
                ));
    }

    @Test
    @DisplayName("μμ ")
    @Order(2)
    public void testCase02() throws Exception {
        Board board = getBoard(commentBoard);
        Post post = getPost(commentUpdatePost00);
        Comment comment = getComment(commentUpdateComment00);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("content", "update_comment");

        MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/{boardId}/posts/{postId}/comments/{commentId}", board.getId(), post.getId(), comment.getId())
                .content(getJsonString(requestBody))
                .contentType(MediaType.APPLICATION_JSON);
        setAuthToken(requestBuilder, common00);

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
                                parameterWithName("boardId").description("κ²μν μμ΄λ"),
                                parameterWithName("postId").description("κ²μλ¬Ό μμ΄λ"),
                                parameterWithName("commentId").description("λκΈ μμ΄λ")
                        ),
                        requestHeaders(
//                                        headerWithName("Authorization").description("firebase ν ν°")
                        ),
                        requestFields(
                                fieldWithPath("content").description("λ΄μ©")
                        ),
                        responseFields(
                                fieldWithPath("id").description("μμ΄λ"),
                                fieldWithPath("content").description("λ΄μ©"),
                                fieldWithPath("createDate").description("μμ±μΌ"),
                                fieldWithPath("updateDate").description("μμ μΌ"),
                                fieldWithPath("upvote").description("μ’μμ"),
                                fieldWithPath("downvote").description("μ«μ΄μ"),
                                fieldWithPath("hasNestedComment").description("νμ λκΈ μ¬λΆ"),
                                fieldWithPath("deleted").description("μ­μ  μ¬λΆ"),
                                fieldWithPath("account").description("μμ±μ"),
                                fieldWithPath("account.id").description("μμ±μ μμ΄λ"),
                                fieldWithPath("account.email").description("μμ±μ μ΄λ©μΌ"),
                                fieldWithPath("account.createDate").description("μμ±μ μμ±μΌ"),
                                fieldWithPath("account.lastActivityDate").description("μμ±μ μ΅κ·Ό νλμΌ"),
                                fieldWithPath("account.nickname").description("μμ±μ λλ€μ"),
                                fieldWithPath("account.description").description("μμ±μ μ€λͺ"),
                                fieldWithPath("account.auth").description("μμ±μ κΆν")
                        )
                ));
    }

    @Test
    @DisplayName("μ­μ ")
    @Order(3)
    public void testCase03() throws Exception {
        Board board = getBoard(commentBoard);
        Post post = getPost(commentUpdatePost00);
        Comment comment = getComment(commentDeleteComment00);

        MockHttpServletRequestBuilder requestBuilder = delete("/v1/boards/{boardId}/posts/{postId}/comments/{commentId}", board.getId(), post.getId(), comment.getId());
        setAuthToken(requestBuilder, common00);

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
                                parameterWithName("boardId").description("κ²μν μμ΄λ"),
                                parameterWithName("postId").description("κ²μλ¬Ό μμ΄λ"),
                                parameterWithName("commentId").description("λκΈ μμ΄λ")
                        ),
                        requestHeaders(
//                                        headerWithName("Authorization").description("firebase ν ν°")
                        ),
                        responseFields(
                                fieldWithPath("id").description("μμ΄λ"),
                                fieldWithPath("createDate").description("μμ±μΌ"),
                                fieldWithPath("updateDate").description("μμ μΌ").optional().type(JsonFieldType.NUMBER),
                                fieldWithPath("upvote").description("μ’μμ"),
                                fieldWithPath("downvote").description("μ«μ΄μ"),
                                fieldWithPath("hasNestedComment").description("νμ λκΈ μ¬λΆ"),
                                fieldWithPath("deleted").description("μ­μ  μ¬λΆ")
                        )
                ));
    }

    @Test
    @DisplayName("μ’μμ μ«μ΄μ")
    @Order(4)
    public void testCase04() throws Exception {
        Board board = getBoard(voteBoard);
        Post post = getPost(voteCommentPost00);
        Comment comment = getComment(voteComment05);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("type", "upvote");

        MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/{boardId}/posts/{postId}/comments/{commentId}/vote", board.getId(), post.getId(), comment.getId())
                .content(getJsonString(requestBody))
                .contentType(MediaType.APPLICATION_JSON);
        setAuthToken(requestBuilder, common00);

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
                                parameterWithName("boardId").description("κ²μν μμ΄λ"),
                                parameterWithName("postId").description("κ²μλ¬Ό μμ΄λ"),
                                parameterWithName("commentId").description("λκΈ μμ΄λ")
                        ),
                        requestHeaders(
//                                        headerWithName("Authorization").description("firebase ν ν°")
                        ),
                        requestFields(
                                fieldWithPath("type").description("μ’λ₯ (upvote: μ’μμ, downvote: μ«μ΄μ)")
                        ),
                        responseFields(
                                fieldWithPath("id").description("μμ΄λ"),
                                fieldWithPath("content").description("λ΄μ©"),
                                fieldWithPath("createDate").description("μμ±μΌ"),
                                fieldWithPath("updateDate").description("μμ μΌ").optional().type(JsonFieldType.NUMBER),
                                fieldWithPath("upvote").description("μ’μμ"),
                                fieldWithPath("downvote").description("μ«μ΄μ"),
                                fieldWithPath("hasNestedComment").description("νμ λκΈ μ¬λΆ"),
                                fieldWithPath("deleted").description("μ­μ  μ¬λΆ"),
                                fieldWithPath("account").description("μμ±μ"),
                                fieldWithPath("account.id").description("μμ±μ μμ΄λ"),
                                fieldWithPath("account.email").description("μμ±μ μ΄λ©μΌ"),
                                fieldWithPath("account.createDate").description("μμ±μ μμ±μΌ"),
                                fieldWithPath("account.lastActivityDate").description("μμ±μ μ΅κ·Ό νλμΌ"),
                                fieldWithPath("account.nickname").description("μμ±μ λλ€μ"),
                                fieldWithPath("account.description").description("μμ±μ μ€λͺ"),
                                fieldWithPath("account.auth").description("μμ±μ κΆν")
                        )
                ));
    }
}
