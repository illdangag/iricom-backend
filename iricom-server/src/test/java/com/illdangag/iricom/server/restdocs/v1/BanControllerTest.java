package com.illdangag.iricom.server.restdocs.v1;

import com.illdangag.iricom.server.data.entity.type.PostState;
import com.illdangag.iricom.server.data.entity.type.PostType;
import com.illdangag.iricom.server.restdocs.snippet.IricomFieldsSnippet;
import com.illdangag.iricom.server.test.IricomTestSuite;
import com.illdangag.iricom.server.test.data.wrapper.TestBoardInfo;
import com.illdangag.iricom.server.test.data.wrapper.TestPostBanInfo;
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

@DisplayName("restdoc: 차단")
public class BanControllerTest extends IricomTestSuite {
    @Autowired
    MockMvc mockMvc;

    protected static final TestBoardInfo boardInfo00 = TestBoardInfo.builder()
            .title("boardInfo00").isEnabled(true).adminList(Arrays.asList(systemAdmin, allBoardAdmin))
            .build();

    protected static final TestPostInfo testPostInfo00 = TestPostInfo.builder()
            .title("testPostInfo00").content("test contents").isAllowComment(true)
            .board(boardInfo00).creator(common00)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .build();

    protected static final TestPostInfo alreadyPostInfo00 = TestPostInfo.builder()
            .title("alreadyPostInfo00").content("test contents").isAllowComment(true)
            .board(boardInfo00).creator(common00)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .build();
    protected static final TestPostInfo alreadyPostInfo01 = TestPostInfo.builder()
            .title("alreadyPostInfo01").content("test contents").isAllowComment(true)
            .board(boardInfo00).creator(common00)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .build();
    protected static final TestPostInfo alreadyPostInfo02 = TestPostInfo.builder()
            .title("alreadyPostInfo02").content("test contents").isAllowComment(true)
            .board(boardInfo00).creator(common00)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .build();

    protected static final TestPostBanInfo postBanInfo00 = TestPostBanInfo.builder()
            .post(alreadyPostInfo00).banAccount(systemAdmin).reason("Already ban.")
            .build();
    protected static final TestPostBanInfo postBanInfo01 = TestPostBanInfo.builder()
            .post(alreadyPostInfo01).banAccount(systemAdmin).reason("Already ban.")
            .build();
    protected static final TestPostBanInfo postBanInfo02 = TestPostBanInfo.builder()
            .post(alreadyPostInfo02).banAccount(systemAdmin).reason("Already ban.")
            .build();

    @Autowired
    public BanControllerTest(ApplicationContext context) {
        super(context);

        List<TestBoardInfo> testBoardInfoList = Arrays.asList(boardInfo00);
        List<TestPostInfo> testPostInfoList = Arrays.asList(testPostInfo00, alreadyPostInfo00, alreadyPostInfo01, alreadyPostInfo02);
        List<TestPostBanInfo> testPostBanInfoList = Arrays.asList(postBanInfo00, postBanInfo01, postBanInfo02);

        super.setBoard(testBoardInfoList);
        super.setPost(testPostInfoList);
        super.setBanPost(testPostBanInfoList);
    }

    @Test
    @DisplayName("게시물 차단")
    public void bp001() throws Exception {
        String boardId = getBoardId(testPostInfo00.getBoard());
        String postId = getPostId(testPostInfo00);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("reason", "This is ban.");

        MockHttpServletRequestBuilder requestBuilder = post("/v1/ban/post/boards/{boardId}/posts/{postId}", boardId, postId)
                .content(getJsonString(requestBody))
                .contentType(MediaType.APPLICATION_JSON);
        setAuthToken(requestBuilder, systemAdmin);

        List<FieldDescriptor> fieldDescriptorList = new LinkedList<>();
        fieldDescriptorList.addAll(IricomFieldsSnippet.getPostBan(""));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getPost("post."));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getAccount("post.account."));

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(200))
                .andDo(print())
                .andDo(document("BP_001",
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
                                fieldWithPath("reason").description("사유")
                        ),
                        responseFields(fieldDescriptorList.toArray(FieldDescriptor[]::new))
                ));
    }

    @Test
    @DisplayName("게시물 목록 조회")
    public void bp002() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = get("/v1/ban/post/boards")
                .param("skip", "0")
                .param("limit", "5")
                .param("reason", "already");
        setAuthToken(requestBuilder, systemAdmin);

        List<FieldDescriptor> fieldDescriptorList = new LinkedList<>();
        fieldDescriptorList.addAll(IricomFieldsSnippet.getSearchList(""));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getPostBan("bans.[]."));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getPost("bans.[].post."));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getAccount("bans.[].post.account."));

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(200))
                .andDo(print())
                .andDo(document("BP_002",
                        preprocessRequest(
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
    @DisplayName("게시물 목록 조회: 게시판")
    public void bp003() throws Exception {
        String boardId = getBoardId(boardInfo00);

        MockHttpServletRequestBuilder requestBuilder = get("/v1/ban/post/boards/{boardId}", boardId)
                .param("skip", "0")
                .param("limit", "20")
                .param("reason", "already");
        setAuthToken(requestBuilder, systemAdmin);

        List<FieldDescriptor> fieldDescriptorList = new LinkedList<>();
        fieldDescriptorList.addAll(IricomFieldsSnippet.getSearchList(""));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getPostBan("bans.[]."));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getPost("bans.[].post."));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getAccount("bans.[].post.account."));

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(200))
                .andDo(print())
                .andDo(document("BP_003",
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
                                parameterWithName("reason").description("사유")
                        ),
                        responseFields(fieldDescriptorList.toArray(FieldDescriptor[]::new))
                ));
    }

    @Test
    @DisplayName("게시물 정보 조회")
    public void bp004() throws Exception {
        String boardId = getBoardId(alreadyPostInfo00.getBoard());
        String postId = getPostId(alreadyPostInfo00);

        MockHttpServletRequestBuilder requestBuilder = get("/v1/ban/post/boards/{boardId}/posts/{postId}", boardId, postId);
        setAuthToken(requestBuilder, systemAdmin);

        List<FieldDescriptor> fieldDescriptorList = new LinkedList<>();
        fieldDescriptorList.addAll(IricomFieldsSnippet.getPostBan(""));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getPost("post."));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getAccount("post.account."));

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(200))
                .andDo(print())
                .andDo(document("BP_004",
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
    @DisplayName("게시물 차단")
    public void bp005() throws Exception {
        String boardId = getBoardId(alreadyPostInfo01.getBoard());
        String postId = getPostId(alreadyPostInfo01);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("reason", "Reason update.");

        MockHttpServletRequestBuilder requestBuilder = patch("/v1/ban/post/boards/{boardId}/posts/{postId}", boardId, postId)
                .content(getJsonString(requestBody))
                .contentType(MediaType.APPLICATION_JSON);
        setAuthToken(requestBuilder, systemAdmin);

        List<FieldDescriptor> fieldDescriptorList = new LinkedList<>();
        fieldDescriptorList.addAll(IricomFieldsSnippet.getPostBan(""));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getPost("post."));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getAccount("post.account."));

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(200))
                .andDo(print())
                .andDo(document("BP_005",
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
                                fieldWithPath("reason").description("사유")
                        ),
                        responseFields(fieldDescriptorList.toArray(FieldDescriptor[]::new))
                ));
    }

    @Test
    @DisplayName("게시물 차단 해제")
    public void bp006() throws Exception {
        String boardId = getBoardId(alreadyPostInfo01.getBoard());
        String postId = getPostId(alreadyPostInfo01);

        MockHttpServletRequestBuilder requestBuilder = delete("/v1/ban/post/boards/{boardId}/posts/{postId}", boardId, postId);
        setAuthToken(requestBuilder, systemAdmin);

        List<FieldDescriptor> fieldDescriptorList = new LinkedList<>();
        fieldDescriptorList.addAll(IricomFieldsSnippet.getPostBan(""));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getPost("post."));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getAccount("post.account."));

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(200))
                .andDo(print())
                .andDo(document("BP_006",
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
}
