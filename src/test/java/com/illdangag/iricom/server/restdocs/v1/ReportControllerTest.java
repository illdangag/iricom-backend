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
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
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
    @Order(0)
    public void testCase00() throws Exception {
        Post post = getPost(reportPost02);
        Board board = post.getBoard();

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("type", "hate");
        requestBody.put("reason", "This is a hateful post.");

        MockHttpServletRequestBuilder requestBuilder = post("/v1/boards/{boardId}/posts/{postId}/report", board.getId(), post.getId())
                .content(getJsonString(requestBody))
                .contentType(MediaType.APPLICATION_JSON);
        setAuthToken(requestBuilder, common00);

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
                                responseFields(
                                        fieldWithPath("id").description("아이디"),
                                        fieldWithPath("createDate").description("작성일"),
                                        fieldWithPath("updateDate").description("수정일"),
                                        fieldWithPath("type").description("신고 종류"),
                                        fieldWithPath("reason").description("사유"),
                                        fieldWithPath("post.id").description("아이디"),
                                        fieldWithPath("post.type").description("게시물의 종류"),
                                        fieldWithPath("post.createDate").description("작성일"),
                                        fieldWithPath("post.updateDate").description("수정일"),
                                        fieldWithPath("post.status").description("상태"),
                                        fieldWithPath("post.title").description("제목"),
                                        fieldWithPath("post.content").description("내용"),
                                        fieldWithPath("post.viewCount").description("조회수"),
                                        fieldWithPath("post.upvote").description("좋아요"),
                                        fieldWithPath("post.downvote").description("싫어요"),
                                        fieldWithPath("post.commentCount").description("댓글수"),
                                        fieldWithPath("post.isAllowComment").description("댓글 허용 여부"),
                                        fieldWithPath("post.isPublish").description("발행 여부"),
                                        fieldWithPath("post.hasTemporary").description("임시 저장 여부"),
                                        fieldWithPath("post.boardId").description("게시판 아이디"),
                                        fieldWithPath("post.isReport").description("신고 여부"),
                                        fieldWithPath("post.isBan").description("차단 여부"),
                                        fieldWithPath("post.account").description("작성자"),
                                        fieldWithPath("post.account.id").description("아이디"),
                                        fieldWithPath("post.account.email").description("이메일"),
                                        fieldWithPath("post.account.createDate").description("생성일"),
                                        fieldWithPath("post.account.lastActivityDate").description("최근 활동일"),
                                        fieldWithPath("post.account.nickname").description("닉네임"),
                                        fieldWithPath("post.account.description").description("설명"),
                                        fieldWithPath("post.account.auth").description("권한")
                                )
                        )
                );
    }

    @Test
    @DisplayName("댓글 신고")
    @Order(1)
    public void testCase01() throws Exception {
        Comment comment = getComment(reportComment08);
        Post post = comment.getPost();
        Board board = post.getBoard();

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("boardId", board.getId());
        requestBody.put("postId", post.getId());
        requestBody.put("commentId", comment.getId());
        requestBody.put("type", "hate");
        requestBody.put("reason", "This is a hateful comment.");

        MockHttpServletRequestBuilder requestBuilder = post("/v1/boards/{boardId}/posts/{postId}/comments/{commentId}/report", board.getId(), post.getId(), comment.getId())
                .content(getJsonString(requestBody))
                .contentType(MediaType.APPLICATION_JSON);
        setAuthToken(requestBuilder, common00);

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
                                requestHeaders(
//                                        headerWithName("Authorization").description("firebase 토큰")
                                ),
                                requestFields(
                                        fieldWithPath("boardId").description("게시판 ID"),
                                        fieldWithPath("postId").description("게시물 ID"),
                                        fieldWithPath("commentId").description("댓글 ID"),
                                        fieldWithPath("type").description("종류"),
                                        fieldWithPath("reason").description("사유")
                                ),
                                responseFields(
                                        fieldWithPath("id").description("아이디"),
                                        fieldWithPath("createDate").description("생성일").optional().type(JsonFieldType.NUMBER),
                                        fieldWithPath("updateDate").description("수정일").optional().type(JsonFieldType.NUMBER),
                                        fieldWithPath("type").description("신고 종류"),
                                        fieldWithPath("reason").description("사유"),
                                        fieldWithPath("comment.id").description("아이디"),
                                        fieldWithPath("comment.content").description("내용"),
                                        fieldWithPath("comment.createDate").description("작성일").optional().type(JsonFieldType.NUMBER),
                                        fieldWithPath("comment.updateDate").description("수정일").optional().type(JsonFieldType.NUMBER),
                                        fieldWithPath("comment.upvote").description("좋아요").optional().type(JsonFieldType.NUMBER),
                                        fieldWithPath("comment.downvote").description("싫어요").optional().type(JsonFieldType.NUMBER),
                                        fieldWithPath("comment.hasNestedComment").description("하위 댓글 여부"),
                                        fieldWithPath("comment.deleted").description("삭제 여부"),
                                        fieldWithPath("comment.isReport").description("신고 여부"),
                                        fieldWithPath("comment.account.id").description("작성자 아이디,"),
                                        fieldWithPath("comment.account.email").description("작성자 이메일"),
                                        fieldWithPath("comment.account.createDate").description("작성자 생성일").optional().type(JsonFieldType.NUMBER),
                                        fieldWithPath("comment.account.lastActivityDate").description("작성자 최근 활동일").optional().type(JsonFieldType.NUMBER),
                                        fieldWithPath("comment.account.nickname").description("작성자 닉네임"),
                                        fieldWithPath("comment.account.description").description("작성자 설명"),
                                        fieldWithPath("comment.account.auth").description("작성자 권한")
                                )
                        )
                );
    }
}
