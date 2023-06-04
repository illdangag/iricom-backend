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
        requestBody.put("boardId", board.getId());
        requestBody.put("postId", post.getId());
        requestBody.put("type", "hate");
        requestBody.put("reason", "This is a hateful post.");

        MockHttpServletRequestBuilder requestBuilder = post("/v1/report/post")
                .content(getJsonString(requestBody))
                .contentType(MediaType.APPLICATION_JSON);
        setAuthToken(requestBuilder, common00);

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
                                requestHeaders(
//                                        headerWithName("Authorization").description("firebase 토큰")
                                ),
                                requestFields(
                                        fieldWithPath("boardId").description("게시판 ID"),
                                        fieldWithPath("postId").description("게시물 ID"),
                                        fieldWithPath("type").description("종류"),
                                        fieldWithPath("reason").description("사유")
                                ),
                                responseFields(
                                        fieldWithPath("id").description("아이디"),
                                        fieldWithPath("type").description("게시물의 종류"),
                                        fieldWithPath("createDate").description("작성일"),
                                        fieldWithPath("updateDate").description("수정일"),
                                        fieldWithPath("status").description("상태"),
                                        fieldWithPath("title").description("제목"),
                                        fieldWithPath("content").description("내용"),
                                        fieldWithPath("viewCount").description("조회수"),
                                        fieldWithPath("upvote").description("좋아요"),
                                        fieldWithPath("downvote").description("싫어요"),
                                        fieldWithPath("commentCount").description("댓글수"),
                                        fieldWithPath("isAllowComment").description("댓글 허용 여부"),
                                        fieldWithPath("isPublish").description("발행 여부"),
                                        fieldWithPath("hasTemporary").description("임시 저장 여부"),
                                        fieldWithPath("boardId").description("게시판 아이디"),
                                        fieldWithPath("account").description("작성자"),
                                        fieldWithPath("account.id").description("아이디"),
                                        fieldWithPath("account.email").description("이메일"),
                                        fieldWithPath("account.createDate").description("생성일"),
                                        fieldWithPath("account.lastActivityDate").description("최근 활동일"),
                                        fieldWithPath("account.nickname").description("닉네임"),
                                        fieldWithPath("account.description").description("설명"),
                                        fieldWithPath("account.auth").description("권한")
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

        MockHttpServletRequestBuilder requestBuilder = post("/v1/report/comment")
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
                                        fieldWithPath("content").description("내용"),
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
}
