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
                                responseFields(
                                        fieldWithPath("id").description("아이디"),
                                        fieldWithPath("content").description("내용"),
                                        fieldWithPath("referenceCommentId").description("상위 댓글 아이디"),
                                        fieldWithPath("createDate").description("작성일"),
                                        fieldWithPath("updateDate").description("수정일").optional().type(JsonFieldType.NUMBER),
                                        fieldWithPath("upvote").description("좋아요"),
                                        fieldWithPath("downvote").description("싫어요"),
                                        fieldWithPath("hasNestedComment").description("하위 댓글 여부"),
                                        fieldWithPath("deleted").description("삭제 여부"),
                                        fieldWithPath("account.id").description("작성자 아이디,"),
                                        fieldWithPath("account.email").description("작성자 이메일"),
                                        fieldWithPath("account.createDate").description("작성자 생성일"),
                                        fieldWithPath("account.lastActivityDate").description("작성자 최근 활동일"),
                                        fieldWithPath("account.nickname").description("작성자 닉네임"),
                                        fieldWithPath("account.description").description("작성자 설명"),
                                        fieldWithPath("account.auth").description("작성자 권한")
                                )
                        )

                );
    }

    @Test
    @DisplayName("목록 조회")
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
                        responseFields(
                                fieldWithPath("total").description("모든 결과의 수"),
                                fieldWithPath("skip").description("건너 뛸 결과 수"),
                                fieldWithPath("limit").description("조회 할 최대 결과 수"),
                                fieldWithPath("comments").description("댓글 목록"),
                                fieldWithPath("comments.[].id").description("아이디"),
                                fieldWithPath("comments.[].content").description("내용"),
                                fieldWithPath("comments.[].createDate").description("작성일"),
                                fieldWithPath("comments.[].updateDate").description("수정일").optional().type(JsonFieldType.NUMBER),
                                fieldWithPath("comments.[].upvote").description("좋아요"),
                                fieldWithPath("comments.[].downvote").description("싫어요"),
                                fieldWithPath("comments.[].hasNestedComment").description("하위 댓글 포함 여부"),
                                fieldWithPath("comments.[].deleted").description("삭제 여부"),
                                fieldWithPath("comments.[].referenceCommentId").description("상위 댓글 아이디"),
                                fieldWithPath("comments.[].account").description("댓글 작성자"),
                                fieldWithPath("comments.[].account.id").description("댓글 작성자 아이디"),
                                fieldWithPath("comments.[].account.email").description("댓글 작성자 이메일"),
                                fieldWithPath("comments.[].account.createDate").description("댓글 작성자 생성일"),
                                fieldWithPath("comments.[].account.lastActivityDate").description("댓글 작성자 최근 활동일"),
                                fieldWithPath("comments.[].account.nickname").description("댓글 작성자 닉네임"),
                                fieldWithPath("comments.[].account.description").description("댓글 작성자 설명"),
                                fieldWithPath("comments.[].account.auth").description("댓글 작성자 권한"),
                                fieldWithPath("comments.[].nestedComments").description("하위 댓글 목록"),
                                fieldWithPath("comments.[].nestedComments.[].id").description("하위 댓글 아이디"),
                                fieldWithPath("comments.[].nestedComments.[].content").description("하위 댓글 내용"),
                                fieldWithPath("comments.[].nestedComments.[].referenceCommentId").description("하위 댓글의 부모 댓글 아이디"),
                                fieldWithPath("comments.[].nestedComments.[].createDate").description("하위 댓글의 작성일"),
                                fieldWithPath("comments.[].nestedComments.[].updateDate").description("하위 댓글의 수정일").optional().type(JsonFieldType.NUMBER),
                                fieldWithPath("comments.[].nestedComments.[].upvote").description("하위 댓글의 좋아요"),
                                fieldWithPath("comments.[].nestedComments.[].downvote").description("하위 댓글의 싫어요"),
                                fieldWithPath("comments.[].nestedComments.[].hasNestedComment").description("하위 댓글의 대댓글 포함 여부"),
                                fieldWithPath("comments.[].nestedComments.[].deleted").description("하위 댓글의 삭제 여부"),
                                fieldWithPath("comments.[].nestedComments.[].account").description("하위 댓글 작성자"),
                                fieldWithPath("comments.[].nestedComments.[].account.id").description("하위 댓글 작성자 아이디"),
                                fieldWithPath("comments.[].nestedComments.[].account.email").description("하위 댓글 작성자 이메일"),
                                fieldWithPath("comments.[].nestedComments.[].account.createDate").description("하위 댓글 작성자 생성일"),
                                fieldWithPath("comments.[].nestedComments.[].account.lastActivityDate").description("하위 댓글 작성자 최근 활동일"),
                                fieldWithPath("comments.[].nestedComments.[].account.nickname").description("하위 댓글 작성자 닉네임"),
                                fieldWithPath("comments.[].nestedComments.[].account.description").description("하위 댓글 작성자 설명"),
                                fieldWithPath("comments.[].nestedComments.[].account.auth").description("하위 댓글 작성자 권한")
                        )
                ));
    }

    @Test
    @DisplayName("수정")
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
                        responseFields(
                                fieldWithPath("id").description("아이디"),
                                fieldWithPath("content").description("내용"),
                                fieldWithPath("createDate").description("작성일"),
                                fieldWithPath("updateDate").description("수정일"),
                                fieldWithPath("upvote").description("좋아요"),
                                fieldWithPath("downvote").description("싫어요"),
                                fieldWithPath("hasNestedComment").description("하위 댓글 여부"),
                                fieldWithPath("deleted").description("삭제 여부"),
                                fieldWithPath("account").description("작성자"),
                                fieldWithPath("account.id").description("작성자 아이디"),
                                fieldWithPath("account.email").description("작성자 이메일"),
                                fieldWithPath("account.createDate").description("작성자 생성일"),
                                fieldWithPath("account.lastActivityDate").description("작성자 최근 활동일"),
                                fieldWithPath("account.nickname").description("작성자 닉네임"),
                                fieldWithPath("account.description").description("작성자 설명"),
                                fieldWithPath("account.auth").description("작성자 권한")
                        )
                ));
    }

    @Test
    @DisplayName("삭제")
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
                                parameterWithName("boardId").description("게시판 아이디"),
                                parameterWithName("postId").description("게시물 아이디"),
                                parameterWithName("commentId").description("댓글 아이디")
                        ),
                        requestHeaders(
//                                        headerWithName("Authorization").description("firebase 토큰")
                        ),
                        responseFields(
                                fieldWithPath("id").description("아이디"),
                                fieldWithPath("createDate").description("작성일"),
                                fieldWithPath("updateDate").description("수정일").optional().type(JsonFieldType.NUMBER),
                                fieldWithPath("upvote").description("좋아요"),
                                fieldWithPath("downvote").description("싫어요"),
                                fieldWithPath("hasNestedComment").description("하위 댓글 여부"),
                                fieldWithPath("deleted").description("삭제 여부")
                        )
                ));
    }

    @Test
    @DisplayName("좋아요 싫어요")
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
                        responseFields(
                                fieldWithPath("id").description("아이디"),
                                fieldWithPath("content").description("내용"),
                                fieldWithPath("createDate").description("작성일"),
                                fieldWithPath("updateDate").description("수정일").optional().type(JsonFieldType.NUMBER),
                                fieldWithPath("upvote").description("좋아요"),
                                fieldWithPath("downvote").description("싫어요"),
                                fieldWithPath("hasNestedComment").description("하위 댓글 여부"),
                                fieldWithPath("deleted").description("삭제 여부"),
                                fieldWithPath("account").description("작성자"),
                                fieldWithPath("account.id").description("작성자 아이디"),
                                fieldWithPath("account.email").description("작성자 이메일"),
                                fieldWithPath("account.createDate").description("작성자 생성일"),
                                fieldWithPath("account.lastActivityDate").description("작성자 최근 활동일"),
                                fieldWithPath("account.nickname").description("작성자 닉네임"),
                                fieldWithPath("account.description").description("작성자 설명"),
                                fieldWithPath("account.auth").description("작성자 권한")
                        )
                ));
    }
}
