package com.illdangag.iricom.server.restdocs.v1;

import com.illdangag.iricom.server.data.entity.Board;
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

@DisplayName("restdoc: 게시물")
public class PostControllerTest extends IricomTestSuite {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    public PostControllerTest(ApplicationContext context) {
        super(context);
    }

    @Test
    @DisplayName("생성")
    @Order(0)
    public void testCase00() throws Exception {
        Board board = getBoard(createBoard);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("title", "new_title");
        requestBody.put("type", "post");
        requestBody.put("content", "new_content");
        requestBody.put("isAllowComment", true);

        MockHttpServletRequestBuilder requestBuilder = post("/v1/boards/{boardId}/posts", board.getId())
                .content(getJsonString(requestBody))
                .contentType(MediaType.APPLICATION_JSON);
        setAuthToken(requestBuilder, systemAdmin);

        List<FieldDescriptor> fieldDescriptorList = new LinkedList<>();
        fieldDescriptorList.addAll(IricomFieldsSnippet.getPost("", true));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getAccount("account."));

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(200))
                .andDo(print())
                .andDo(document("PS_001",
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
                                requestFields(
                                        fieldWithPath("title").description("제목"),
                                        fieldWithPath("type").description("게시물의 종류 (post: 게시물, notification: 공지사항)"),
                                        fieldWithPath("content").description("내용"),
                                        fieldWithPath("isAllowComment").description("댓글 허용 여부")
                                ),
                                responseFields(fieldDescriptorList.toArray(FieldDescriptor[]::new))
                        )
                );
    }

    @Test
    @DisplayName("목록 조회")
    @Order(1)
    public void testCase01() throws Exception {
        Board board = getBoard(enableBoard);

        MockHttpServletRequestBuilder requestBuilder = get("/v1/boards/{boardId}/posts", board.getId())
                .param("skip", "0")
                .param("limit", "20")
                .param("keyword", "")
                .param("type", "post");
        setAuthToken(requestBuilder, common00);

        List<FieldDescriptor> fieldDescriptorList = new LinkedList<>();
        fieldDescriptorList.addAll(IricomFieldsSnippet.getSearchList(""));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getPost("posts.[].", false));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getAccount("posts.[].account."));

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(200))
                .andDo(print())
                .andDo(document("PS_002",
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
                                        parameterWithName("keyword").description("검색어"),
                                        parameterWithName("type").description("게시물의 종류 (post: 게시물, notification: 공지사항)")
                                ),
                                responseFields(fieldDescriptorList.toArray(FieldDescriptor[]::new))
                        )
                );
    }

    @Test
    @DisplayName("조회")
    @Order(2)
    public void testCase02() throws Exception {
        Board board = getBoard(enableBoard);
        Post post = getPost(enableBoardPost00);

        MockHttpServletRequestBuilder requestBuilder = get("/v1/boards/{boardId}/posts/{postId}", board.getId(), post.getId())
                .param("state", "publish");
        setAuthToken(requestBuilder, common00);

        List<FieldDescriptor> fieldDescriptorList = new LinkedList<>();
        fieldDescriptorList.addAll(IricomFieldsSnippet.getPost("", true));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getAccount("account."));

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(200))
                .andDo(print())
                .andDo(document("PS_003",
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
                                parameterWithName("state").description("게시물의 상태 (publish: 게시물, notification: 공지사항)")
                        ),
                        responseFields(fieldDescriptorList.toArray(FieldDescriptor[]::new))
                ));
    }

    @Test
    @DisplayName("수정")
    @Order(3)
    public void testCase03() throws Exception {
        Board board = getBoard(restDocBoard);
        Post post = getPost(updateBoardPost12);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("title", "update_title");
        requestBody.put("type", "notification");
        requestBody.put("content", "update_content");
        requestBody.put("isAllowComment", false);

        MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/{boardId}/posts/{postId}", board.getId(), post.getId())
                .content(getJsonString(requestBody))
                .contentType(MediaType.APPLICATION_JSON);
        ;
        setAuthToken(requestBuilder, allBoardAdmin);

        List<FieldDescriptor> fieldDescriptorList = new LinkedList<>();
        fieldDescriptorList.addAll(IricomFieldsSnippet.getPost("", true));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getAccount("account."));

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(200))
                .andDo(print())
                .andDo(document("PS_004",
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
                                fieldWithPath("title").description("제목"),
                                fieldWithPath("type").description("게시물의 종류 (post: 게시물, notification: 공지사항)"),
                                fieldWithPath("content").description("내용"),
                                fieldWithPath("isAllowComment").description("댓글 허용 여부")
                        ),
                        responseFields(fieldDescriptorList.toArray(FieldDescriptor[]::new))
                ));
    }

    @Test
    @DisplayName("발행")
    @Order(4)
    public void testCase04() throws Exception {
        Board board = getBoard(restDocBoard);
        Post post = getPost(updateBoardPost13);

        MockHttpServletRequestBuilder requestBuilder = post("/v1/boards/{boardId}/posts/{postId}/publish", board.getId(), post.getId());
        setAuthToken(requestBuilder, common00);

        List<FieldDescriptor> fieldDescriptorList = new LinkedList<>();
        fieldDescriptorList.addAll(IricomFieldsSnippet.getPost("", true));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getAccount("account."));

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(200))
                .andDo(print())
                .andDo(document("PS_005",
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
                        responseFields(fieldDescriptorList.toArray(FieldDescriptor[]::new))
                ));
    }

    @Test
    @DisplayName("삭제")
    @Order(5)
    public void testCase05() throws Exception {
        Board board = getBoard(restDocBoard);
        Post post = getPost(updateBoardPost14);

        MockHttpServletRequestBuilder requestBuilder = delete("/v1/boards/{boardId}/posts/{postId}", board.getId(), post.getId());
        setAuthToken(requestBuilder, common00);

        List<FieldDescriptor> fieldDescriptorList = new LinkedList<>();
        fieldDescriptorList.addAll(IricomFieldsSnippet.getPost("", false));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getAccount("account."));

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(200))
                .andDo(print())
                .andDo(document("PS_006",
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
                        responseFields(fieldDescriptorList.toArray(FieldDescriptor[]::new))
                ));
    }

    @Test
    @DisplayName("게시물 좋아요/싫어요")
    @Order(6)
    public void testCase06() throws Exception {
        Board board = getBoard(voteBoard);
        Post post = getPost(votePost02);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("type", "upvote");

        MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/{boardId}/posts/{postId}/vote", board.getId(), post.getId())
                .content(getJsonString(requestBody))
                .contentType(MediaType.APPLICATION_JSON);
        setAuthToken(requestBuilder, common00);

        List<FieldDescriptor> fieldDescriptorList = new LinkedList<>();
        fieldDescriptorList.addAll(IricomFieldsSnippet.getPost("", true));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getAccount("account."));

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(200))
                .andDo(print())
                .andDo(document("PS_007",
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
                                fieldWithPath("type").description("종류 (upvote: 좋아요, downvote: 싫어요)")
                        ),
                        responseFields(fieldDescriptorList.toArray(FieldDescriptor[]::new))
                ));
    }
}
