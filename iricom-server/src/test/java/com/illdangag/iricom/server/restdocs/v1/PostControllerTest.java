package com.illdangag.iricom.server.restdocs.v1;

import com.illdangag.iricom.server.data.entity.type.PostState;
import com.illdangag.iricom.server.data.entity.type.PostType;
import com.illdangag.iricom.server.restdocs.snippet.IricomFieldsSnippet;
import com.illdangag.iricom.server.test.IricomTestSuite;
import com.illdangag.iricom.server.test.data.wrapper.TestBoardInfo;
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

@DisplayName("restdoc: 게시물")
public class PostControllerTest extends IricomTestSuite {
    @Autowired
    MockMvc mockMvc;

    private static final TestBoardInfo testBoardInfo00 = TestBoardInfo.builder()
            .title("testBoardInfo00").isEnabled(true).adminList(Collections.singletonList(allBoardAdmin)).build();

    private static final TestPostInfo testPostInfo00 = TestPostInfo.builder()
            .title("testPostInfo00").content("content").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .creator(common00).board(testBoardInfo00).build();
    private static final TestPostInfo testPostInfo01 = TestPostInfo.builder()
            .title("testPostInfo01").content("content").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .creator(common00).board(testBoardInfo00).build();
    private static final TestPostInfo testPostInfo02 = TestPostInfo.builder()
            .title("testPostInfo01").content("content").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.TEMPORARY)
            .creator(common00).board(testBoardInfo00).build();
    private static final TestPostInfo testPostInfo03 = TestPostInfo.builder()
            .title("testPostInfo03").content("content").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.TEMPORARY)
            .creator(common00).board(testBoardInfo00).build();
    private static final TestPostInfo testPostInfo04 = TestPostInfo.builder()
            .title("testPostInfo04").content("content").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .creator(common00).board(testBoardInfo00).build();

    @Autowired
    public PostControllerTest(ApplicationContext context) {
        super(context);

        List<TestBoardInfo> testBoardInfoList = Arrays.asList(testBoardInfo00);
        List<TestPostInfo> testPostInfoList = Arrays.asList(testPostInfo00, testPostInfo01, testPostInfo02, testPostInfo03,
                testPostInfo04);

        super.setBoard(testBoardInfoList);
        super.setPost(testPostInfoList);
    }

    @Test
    @DisplayName("생성")
    public void ps001() throws Exception {
        String boardId = getBoardId(testBoardInfo00);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("title", "new_title");
        requestBody.put("type", "post");
        requestBody.put("content", "new_content");
        requestBody.put("allowComment", true);

        MockHttpServletRequestBuilder requestBuilder = post("/v1/boards/{boardId}/posts", boardId)
                .content(getJsonString(requestBody))
                .contentType(MediaType.APPLICATION_JSON);
        setAuthToken(requestBuilder, systemAdmin);

        List<FieldDescriptor> fieldDescriptorList = new LinkedList<>();
        fieldDescriptorList.addAll(IricomFieldsSnippet.getPost(""));
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
//                                headerWithName("Authorization").description("firebase 토큰")
                        ),
                        requestFields(
                                fieldWithPath("title").description("제목"),
                                fieldWithPath("type").description("게시물의 종류 (post: 게시물, notification: 공지사항)"),
                                fieldWithPath("content").description("내용"),
                                fieldWithPath("allowComment").description("댓글 허용 여부")
                        ),
                        responseFields(fieldDescriptorList.toArray(FieldDescriptor[]::new))
                ));
    }

    @Test
    @DisplayName("목록 조회")
    public void ps002() throws Exception {
        String boardId = getBoardId(testBoardInfo00);

        MockHttpServletRequestBuilder requestBuilder = get("/v1/boards/{boardId}/posts", boardId)
                .param("skip", "0")
                .param("limit", "5")
                .param("keyword", "")
                .param("type", "post");
        setAuthToken(requestBuilder, common00);

        List<FieldDescriptor> fieldDescriptorList = new LinkedList<>();
        fieldDescriptorList.addAll(IricomFieldsSnippet.getSearchList(""));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getPost("posts.[]."));
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
//                                headerWithName("Authorization").description("firebase 토큰")
                        ),
                        requestParameters(
                                parameterWithName("skip").description("건너 뛸 수"),
                                parameterWithName("limit").description("최대 조회 수"),
                                parameterWithName("keyword").description("검색어"),
                                parameterWithName("type").description("게시물의 종류 (post: 게시물, notification: 공지사항)")
                        ),
                        responseFields(fieldDescriptorList.toArray(FieldDescriptor[]::new))
                ));
    }

    @Test
    @DisplayName("조회")
    public void ps003() throws Exception {
        String boardId = getBoardId(testPostInfo00.getBoard());
        String postId = getPostId(testPostInfo00);

        MockHttpServletRequestBuilder requestBuilder = get("/v1/boards/{boardId}/posts/{postId}", boardId, postId)
                .param("state", "publish");
        setAuthToken(requestBuilder, common00);

        List<FieldDescriptor> fieldDescriptorList = new LinkedList<>();
        fieldDescriptorList.addAll(IricomFieldsSnippet.getPost(""));
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
//                                headerWithName("Authorization").description("firebase 토큰")
                        ),
                        requestParameters(
                                parameterWithName("state").description("게시물의 상태 (publish: 게시물, notification: 공지사항)")
                        ),
                        responseFields(fieldDescriptorList.toArray(FieldDescriptor[]::new))
                ));
    }

    @Test
    @DisplayName("수정")
    public void ps004() throws Exception {
        String boardId = getBoardId(testPostInfo01.getBoard());
        String postId = getPostId(testPostInfo01);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("title", "update_title");
        requestBody.put("type", "post");
        requestBody.put("content", "update_content");
        requestBody.put("allowComment", false);

        MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/{boardId}/posts/{postId}", boardId, postId)
                .content(getJsonString(requestBody))
                .contentType(MediaType.APPLICATION_JSON);
        setAuthToken(requestBuilder, common00);

        List<FieldDescriptor> fieldDescriptorList = new LinkedList<>();
        fieldDescriptorList.addAll(IricomFieldsSnippet.getPost(""));
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
//                                headerWithName("Authorization").description("firebase 토큰")
                        ),
                        requestFields(
                                fieldWithPath("title").description("제목"),
                                fieldWithPath("type").description("게시물의 종류 (post: 게시물, notification: 공지사항)"),
                                fieldWithPath("content").description("내용"),
                                fieldWithPath("allowComment").description("댓글 허용 여부")
                        ),
                        responseFields(fieldDescriptorList.toArray(FieldDescriptor[]::new))
                ));
    }

    @Test
    @DisplayName("발행")
    public void ps005() throws Exception {
        String boardId = getBoardId(testPostInfo02.getBoard());
        String postId = getPostId(testPostInfo02);

        MockHttpServletRequestBuilder requestBuilder = post("/v1/boards/{boardId}/posts/{postId}/publish", boardId, postId);
        setAuthToken(requestBuilder, common00);

        List<FieldDescriptor> fieldDescriptorList = new LinkedList<>();
        fieldDescriptorList.addAll(IricomFieldsSnippet.getPost(""));
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
//                                headerWithName("Authorization").description("firebase 토큰")
                        ),
                        responseFields(fieldDescriptorList.toArray(FieldDescriptor[]::new))
                ));
    }

    @Test
    @DisplayName("삭제")
    public void ps006() throws Exception {
        String boardId = getBoardId(testPostInfo03.getBoard());
        String postId = getPostId(testPostInfo03);

        MockHttpServletRequestBuilder requestBuilder = delete("/v1/boards/{boardId}/posts/{postId}", boardId, postId);
        setAuthToken(requestBuilder, common00);

        List<FieldDescriptor> fieldDescriptorList = new LinkedList<>();
        fieldDescriptorList.addAll(IricomFieldsSnippet.getPost(""));
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
//                                headerWithName("Authorization").description("firebase 토큰")
                        ),
                        responseFields(fieldDescriptorList.toArray(FieldDescriptor[]::new))
                ));
    }

    @Test
    @DisplayName("게시물 좋아요/싫어요")
    public void ps007() throws Exception {
        String boardId = getBoardId(testPostInfo04.getBoard());
        String postId = getPostId(testPostInfo04);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("type", "upvote");

        MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/{boardId}/posts/{postId}/vote", boardId, postId)
                .content(getJsonString(requestBody))
                .contentType(MediaType.APPLICATION_JSON);
        setAuthToken(requestBuilder, common00);

        List<FieldDescriptor> fieldDescriptorList = new LinkedList<>();
        fieldDescriptorList.addAll(IricomFieldsSnippet.getPost(""));
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
//                                headerWithName("Authorization").description("firebase 토큰")
                        ),
                        requestFields(
                                fieldWithPath("type").description("종류 (upvote: 좋아요, downvote: 싫어요)")
                        ),
                        responseFields(fieldDescriptorList.toArray(FieldDescriptor[]::new))
                ));
    }
}
