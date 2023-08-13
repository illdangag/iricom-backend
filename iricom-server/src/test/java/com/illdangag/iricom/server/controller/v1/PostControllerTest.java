package com.illdangag.iricom.server.controller.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.illdangag.iricom.server.data.entity.PostState;
import com.illdangag.iricom.server.data.entity.PostType;
import com.illdangag.iricom.server.data.response.PostInfo;
import com.illdangag.iricom.server.test.IricomTestSuite;
import com.illdangag.iricom.server.test.data.wrapper.TestBoardInfo;
import com.illdangag.iricom.server.test.data.wrapper.TestPostInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@DisplayName("controller: 게시물")
public class PostControllerTest extends IricomTestSuite {
    @Autowired
    MockMvc mockMvc;

    private final TestBoardInfo testBoardInfo00 = TestBoardInfo.builder()
            .title("testBoardInfo00").isEnabled(true).adminList(Arrays.asList(allBoardAdmin, common01)).build();
    private final TestBoardInfo disableTestBoardInfo00 = TestBoardInfo.builder()
            .title("disableTestBoardInfo00").isEnabled(false).adminList(Arrays.asList(allBoardAdmin, disableBoardAdmin)).build();
    private final TestBoardInfo autoBoardInfo00 = TestBoardInfo.builder()
            .title("autoBoardInfo00").isEnabled(true).adminList(Collections.singletonList(allBoardAdmin)).build();
    private final TestBoardInfo deleteBoardInfo00 = TestBoardInfo.builder()
            .title("deleteBoardInfo00").isEnabled(true).adminList(Collections.singletonList(allBoardAdmin)).build();
    private final TestBoardInfo voteBoardInfo00 = TestBoardInfo.builder()
            .title("voteBoardInfo00").isEnabled(true).adminList(Collections.singletonList(allBoardAdmin)).build();

    private final TestPostInfo enableBoardPost00 = TestPostInfo.builder()
            .title("enableBoardPost00").content("enableBoardPost00").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .creator(allBoardAdmin).board(testBoardInfo00).build();
    private final TestPostInfo disableBoardPost00 = TestPostInfo.builder()
            .title("disableBoardPost00").content("content").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .creator(common00).board(disableTestBoardInfo00).build();
    protected final TestPostInfo disableBoardNotification00 = TestPostInfo.builder()
            .title("disableBoardNotification00").content("disableBoardNotification00").isAllowComment(true)
            .postType(PostType.NOTIFICATION).postState(PostState.TEMPORARY)
            .creator(allBoardAdmin).board(disableTestBoardInfo00).build();
    protected final TestPostInfo enableBoardPost03 = TestPostInfo.builder()
            .title("enableBoardPost03").content("enableBoardPost03").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.TEMPORARY)
            .creator(allBoardAdmin).board(testBoardInfo00).build();
    protected final TestPostInfo temporaryPostInfo00 = TestPostInfo.builder()
            .title("temporaryBoardInfo00").content("enableBoardPost03").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.TEMPORARY)
            .creator(common00).board(testBoardInfo00).build();
    protected final TestPostInfo temporaryPostInfo01 = TestPostInfo.builder()
            .title("temporaryPostInfo01").content("temporaryPostInfo01").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.TEMPORARY)
            .creator(common00).board(testBoardInfo00).build();
    protected final TestPostInfo temporaryPostInfo02 = TestPostInfo.builder()
            .title("temporaryPostInfo02").content("temporaryPostInfo02").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.TEMPORARY)
            .creator(common00).board(disableTestBoardInfo00).build();
    protected final TestPostInfo publishPostInfo00 = TestPostInfo.builder()
            .title("publishPostInfo00").content("publishPostInfo00").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .creator(common00).board(testBoardInfo00).build();
    protected final TestPostInfo notificationPostInfo00 = TestPostInfo.builder()
            .title("notificationPostInfo00").content("notificationPostInfo00").isAllowComment(true)
            .postType(PostType.NOTIFICATION).postState(PostState.TEMPORARY)
            .creator(allBoardAdmin).board(testBoardInfo00).build();
    private final TestPostInfo updatePostInfo00 = TestPostInfo.builder()
            .title("updatePostInfo00").content("updatePostInfo00").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.TEMPORARY)
            .creator(common00).board(testBoardInfo00).build();
    private final TestPostInfo updatePostInfo01 = TestPostInfo.builder()
            .title("updatePostInfo01").content("updatePostInfo01").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.TEMPORARY)
            .creator(allBoardAdmin).board(testBoardInfo00).build();
    private final TestPostInfo updatePostInfo02 = TestPostInfo.builder()
            .title("updatePostInfo02").content("updatePostInfo02").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .creator(common00).board(testBoardInfo00).build();
    private final TestPostInfo autoPostInfo00 = TestPostInfo.builder()
            .title("autoPostInfo00").content("autoPostInfo00").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .creator(common00).board(autoBoardInfo00).build();
    private final TestPostInfo autoPostInfo01 = TestPostInfo.builder()
            .title("autoPostInfo01").content("autoPostInfo01").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .creator(common01).board(autoBoardInfo00).build();

    private final TestPostInfo deletePostInfo00 = TestPostInfo.builder()
            .title("deletePostInfo00").content("deletePostInfo00").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.TEMPORARY)
            .creator(common00).board(autoBoardInfo00).build();
    private final TestPostInfo deletePostInfo01 = TestPostInfo.builder()
            .title("deletePostInfo01").content("deletePostInfo01").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .creator(common00).board(autoBoardInfo00).build();
    private final TestPostInfo deletePostInfo02 = TestPostInfo.builder()
            .title("deletePostInfo02").content("deletePostInfo02").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .creator(common00).board(autoBoardInfo00).build();
    private final TestPostInfo disabledPostInfo00 = TestPostInfo.builder()
            .title("disabledPostInfo00").content("disabledPostInfo00").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .creator(common00).board(disableTestBoardInfo00).build();
    private final TestPostInfo votePostInfo00 = TestPostInfo.builder()
            .title("votePostInfo00").content("votePostInfo00").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .creator(common00).board(voteBoardInfo00).build();
    private final TestPostInfo votePostInfo01 = TestPostInfo.builder()
            .title("votePostInfo01").content("votePostInfo01").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .creator(common00).board(voteBoardInfo00).build();
    private final TestPostInfo votePostInfo02 = TestPostInfo.builder()
            .title("votePostInfo02").content("votePostInfo02").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.TEMPORARY)
            .creator(common00).board(voteBoardInfo00).build();
    private final TestPostInfo votePostInfo03 = TestPostInfo.builder()
            .title("votePostInfo03").content("votePostInfo03").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .creator(common00).board(voteBoardInfo00).build();
    private final TestPostInfo votePostInfo04 = TestPostInfo.builder()
            .title("votePostInfo04").content("votePostInfo04").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .creator(common00).board(disableTestBoardInfo00).build();
    private final TestPostInfo votePostInfo05 = TestPostInfo.builder()
            .title("votePostInfo05").content("votePostInfo05").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .creator(common00).board(voteBoardInfo00).build();

    @Autowired
    public PostControllerTest(ApplicationContext context) {
        super(context);

        addTestBoardInfo(testBoardInfo00, disableTestBoardInfo00, autoBoardInfo00,
                deleteBoardInfo00, voteBoardInfo00);
        addTestPostInfo(enableBoardPost00, disableBoardPost00, disableBoardNotification00,
                enableBoardPost03, temporaryPostInfo00, temporaryPostInfo01, publishPostInfo00, notificationPostInfo00,
                temporaryPostInfo02, updatePostInfo00, updatePostInfo01, updatePostInfo02, autoPostInfo00, autoPostInfo01,
                deletePostInfo00, deletePostInfo01, deletePostInfo02, disabledPostInfo00, votePostInfo00, votePostInfo01,
                votePostInfo02, votePostInfo03, votePostInfo04, votePostInfo05);

        init();
    }

    @Nested
    @DisplayName("게시물 생성")
    class CreateTest {

        @Nested
        @DisplayName("게시물")
        class PostTest {

            @Test
            @DisplayName("제목, 내용, 댓글 허용 여부")
            public void titleContentIsAllowComment() throws Exception {
                String boardId = getBoardId(testBoardInfo00);

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("title", "new_title");
                requestBody.put("type", "post");
                requestBody.put("content", "new_content");
                requestBody.put("isAllowComment", true);

                MockHttpServletRequestBuilder requestBuilder = post("/v1/boards/{boardId}/posts", boardId)
                        .content(getJsonString(requestBody))
                        .contentType(MediaType.APPLICATION_JSON);
                setAuthToken(requestBuilder, common00);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(200))
                        .andExpect(jsonPath("$.title").value("new_title"))
                        .andExpect(jsonPath("$.type").value("post"))
                        .andExpect(jsonPath("$.content").value("new_content"))
                        .andExpect(jsonPath("$.allowComment").value(true))
                        .andDo(print());
            }

            @Test
            @DisplayName("제목")
            public void title() throws Exception {
                String boardId = getBoardId(testBoardInfo00);

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("title", "only_title");
                requestBody.put("type", "post");

                MockHttpServletRequestBuilder requestBuilder = post("/v1/boards/{boardId}/posts", boardId)
                        .content(getJsonString(requestBody))
                        .contentType(MediaType.APPLICATION_JSON);
                setAuthToken(requestBuilder, common00);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(200))
                        .andExpect(jsonPath("$.title").value("only_title"))
                        .andExpect(jsonPath("$.type").value("post"))
                        .andExpect(jsonPath("$.content").value(""))
                        .andExpect(jsonPath("$.allowComment").value(true))
                        .andDo(print());
            }

            @Test
            @DisplayName("내용")
            public void content() throws Exception {
                String boardId = getBoardId(testBoardInfo00);

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("type", "post");
                requestBody.put("content", "only_content");

                MockHttpServletRequestBuilder requestBuilder = post("/v1/boards/{boardId}/posts", boardId)
                        .content(getJsonString(requestBody))
                        .contentType(MediaType.APPLICATION_JSON);
                setAuthToken(requestBuilder, common00);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(400))
                        .andExpect(jsonPath("$.code").value("01020000"))
                        .andDo(print());
            }

            @Test
            @DisplayName("댓글 허용 여부")
            public void isAllowComment() throws Exception {
                String boardId = getBoardId(testBoardInfo00);

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("type", "post");
                requestBody.put("isAllowComment", true);

                MockHttpServletRequestBuilder requestBuilder = post("/v1/boards/{boardId}/posts", boardId)
                        .content(getJsonString(requestBody))
                        .contentType(MediaType.APPLICATION_JSON);
                setAuthToken(requestBuilder, common00);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(400))
                        .andExpect(jsonPath("$.code").value("01020000"))
                        .andDo(print());
            }

            @Test
            @DisplayName("비활성화 게시판에 생성")
            public void postToDisabledBoard() throws Exception {
                String boardId = getBoardId(disableTestBoardInfo00);

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("title", "new_title");
                requestBody.put("type", "post");
                requestBody.put("content", "new_content");
                requestBody.put("isAllowComment", true);

                MockHttpServletRequestBuilder requestBuilder = post("/v1/boards/{boardId}/posts", boardId)
                        .content(getJsonString(requestBody))
                        .contentType(MediaType.APPLICATION_JSON);
                setAuthToken(requestBuilder, common00);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(400))
                        .andExpect(jsonPath("$.code").value("03000001"))
                        .andExpect(jsonPath("$.message").value("Board is disabled."))
                        .andDo(print());
            }
        }

        @Nested
        @DisplayName("공지 사항")
        class NotificationTest {

            @Test
            @DisplayName("제목, 내용, 댓글 허용 여부")
            public void titleContentIsAllowComment() throws Exception {
                String boardId = getBoardId(testBoardInfo00);

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("title", "new_title");
                requestBody.put("type", "notification");
                requestBody.put("content", "new_content");
                requestBody.put("isAllowComment", true);

                MockHttpServletRequestBuilder requestBuilder = post("/v1/boards/{boardId}/posts", boardId)
                        .content(getJsonString(requestBody))
                        .contentType(MediaType.APPLICATION_JSON);
                setAuthToken(requestBuilder, systemAdmin);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(200))
                        .andExpect(jsonPath("$.title").value("new_title"))
                        .andExpect(jsonPath("$.type").value("notification"))
                        .andExpect(jsonPath("$.content").value("new_content"))
                        .andExpect(jsonPath("$.allowComment").value(true))
                        .andDo(print());
            }

            @Test
            @DisplayName("제목")
            public void title() throws Exception {
                String boardId = getBoardId(testBoardInfo00);

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("title", "only_title");
                requestBody.put("type", "notification");

                MockHttpServletRequestBuilder requestBuilder = post("/v1/boards/{boardId}/posts", boardId)
                        .content(getJsonString(requestBody))
                        .contentType(MediaType.APPLICATION_JSON);
                setAuthToken(requestBuilder, systemAdmin);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(200))
                        .andExpect(jsonPath("$.title").value("only_title"))
                        .andExpect(jsonPath("$.type").value("notification"))
                        .andExpect(jsonPath("$.content").value(""))
                        .andExpect(jsonPath("$.allowComment").value(true))
                        .andDo(print());
            }

            @Test
            @DisplayName("내용")
            public void content() throws Exception {
                String boardId = getBoardId(testBoardInfo00);

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("type", "post");
                requestBody.put("content", "notification");

                MockHttpServletRequestBuilder requestBuilder = post("/v1/boards/{boardId}/posts", boardId)
                        .content(getJsonString(requestBody))
                        .contentType(MediaType.APPLICATION_JSON);
                setAuthToken(requestBuilder, systemAdmin);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(400))
                        .andExpect(jsonPath("$.code").value("01020000"))
                        .andDo(print());
            }

            @Test
            @DisplayName("댓글 허용 여부")
            public void isAllowComment() throws Exception {
                String boardId = getBoardId(testBoardInfo00);

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("type", "notification");
                requestBody.put("isAllowComment", true);

                MockHttpServletRequestBuilder requestBuilder = post("/v1/boards/{boardId}/posts", boardId)
                        .content(getJsonString(requestBody))
                        .contentType(MediaType.APPLICATION_JSON);
                setAuthToken(requestBuilder, systemAdmin);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(400))
                        .andExpect(jsonPath("$.code").value("01020000"))
                        .andDo(print());
            }

            @Test
            @DisplayName("비활성화 게시판에 생성")
            public void postToDisabledBoard() throws Exception {
                String boardId = getBoardId(disableTestBoardInfo00);

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("title", "new_title");
                requestBody.put("type", "notification");
                requestBody.put("content", "new_content");
                requestBody.put("isAllowComment", true);

                MockHttpServletRequestBuilder requestBuilder = post("/v1/boards/{boardId}/posts", boardId)
                        .content(getJsonString(requestBody))
                        .contentType(MediaType.APPLICATION_JSON);
                setAuthToken(requestBuilder, systemAdmin);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(400))
                        .andExpect(jsonPath("$.code").value("03000001"))
                        .andExpect(jsonPath("$.message").value("Board is disabled."))
                        .andDo(print());
            }

            @Nested
            @DisplayName("권한")
            class AuthTest {

                @Test
                @DisplayName("게시판 관리자")
                public void boardAdmin() throws Exception {
                    String boardId = getBoardId(testBoardInfo00);

                    Map<String, Object> requestBody = new HashMap<>();
                    requestBody.put("title", "new_title");
                    requestBody.put("type", "notification");
                    requestBody.put("content", "new_content");
                    requestBody.put("isAllowComment", true);

                    MockHttpServletRequestBuilder requestBuilder = post("/v1/boards/{boardId}/posts", boardId)
                            .content(getJsonString(requestBody))
                            .contentType(MediaType.APPLICATION_JSON);
                    setAuthToken(requestBuilder, allBoardAdmin);

                    mockMvc.perform(requestBuilder)
                            .andExpect(jsonPath("$.title").value("new_title"))
                            .andExpect(jsonPath("$.type").value("notification"))
                            .andExpect(jsonPath("$.content").value("new_content"))
                            .andExpect(jsonPath("$.allowComment").value(true))
                            .andDo(print());
                }

                @Test
                @DisplayName("일반 계정")
                public void account() throws Exception {
                    String boardId = getBoardId(testBoardInfo00);

                    Map<String, Object> requestBody = new HashMap<>();
                    requestBody.put("title", "new_title");
                    requestBody.put("type", "notification");
                    requestBody.put("content", "new_content");
                    requestBody.put("isAllowComment", true);

                    MockHttpServletRequestBuilder requestBuilder = post("/v1/boards/{boardId}/posts", boardId)
                            .content(getJsonString(requestBody))
                            .contentType(MediaType.APPLICATION_JSON);
                    setAuthToken(requestBuilder, common00);

                    mockMvc.perform(requestBuilder)
                            .andExpect(status().is(401))
                            .andExpect(jsonPath("$.code").value("04000001"))
                            .andDo(print());
                }
            }
        }
    }

    @Nested
    @DisplayName("게시물 조회")
    class GetTest {

        @Nested
        @DisplayName("정보")
        class InfoTest {

            @Test
            @DisplayName("기본")
            public void getInfo() throws Exception {
                String boardId = getBoardId(testBoardInfo00);
                String postId = getPostId(enableBoardPost00);

                MockHttpServletRequestBuilder requestBuilder = get("/v1/boards/{boardId}/posts/{postId}", boardId, postId);
                setAuthToken(requestBuilder, common00);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(200))
                        .andDo(print());
            }

            @Test
            @DisplayName("임시 저장")
            public void getTemporaryPost() throws Exception {
                String boardId = getBoardId(testBoardInfo00);
                String postId = getPostId(enableBoardPost03);

                MockHttpServletRequestBuilder requestBuilder = get("/v1/boards/{boardId}/posts/{postId}", boardId, postId)
                        .param("state", "temporary");
                setAuthToken(requestBuilder, allBoardAdmin);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(200))
                        .andExpect(jsonPath("$.status").value("temporary"))
                        .andDo(print());
            }

            @Test
            @DisplayName("임시 저장 하지 않은 게시물")
            public void getPublishPost() throws Exception {
                String boardId = getBoardId(testBoardInfo00);
                String postId = getPostId(enableBoardPost00);

                MockHttpServletRequestBuilder requestBuilder = get("/v1/boards/{boardId}/posts/{postId}", boardId, postId)
                        .param("state", "temporary");
                setAuthToken(requestBuilder, allBoardAdmin);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(200))
                        .andExpect(jsonPath("$.hasTemporary").value(false))
                        .andDo(print());
            }

            @Test
            @DisplayName("발행 하지 않은 게시물")
            public void getNotPublishPost() throws Exception {
                String boardId = getBoardId(testBoardInfo00);
                String postId = getPostId(enableBoardPost03);

                MockHttpServletRequestBuilder requestBuilder = get("/v1/boards/{boardId}/posts/{postId}", boardId, postId)
                        .param("state", "publish");
                setAuthToken(requestBuilder, common00);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(404))
                        .andExpect(jsonPath("$.code").value("04000005"))
                        .andDo(print());
            }

            @Test
            @DisplayName("존재하지 않는 게시판")
            public void getNotExistBoard() throws Exception {
                String postId = getPostId(enableBoardPost03);

                MockHttpServletRequestBuilder requestBuilder = get("/v1/boards/unknown/posts/{postId}", postId)
                        .param("state", "temporary");
                setAuthToken(requestBuilder, common00);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(404))
                        .andExpect(jsonPath("$.code").value("03000000"))
                        .andDo(print());
            }

            @Test
            @DisplayName("존재하지 않는 게시물")
            public void getNotExistPost() throws Exception {
                String boardId = getBoardId(testBoardInfo00);

                MockHttpServletRequestBuilder requestBuilder = get("/v1/boards/{boardId}/posts/unknown", boardId)
                        .param("state", "temporary");
                setAuthToken(requestBuilder, common00);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(404))
                        .andExpect(jsonPath("$.code").value("04000000"))
                        .andExpect(jsonPath("$.message").value("Not exist post."))
                        .andDo(print());
            }

            @Test
            @DisplayName("조회수")
            public void getViewCount() throws Exception {
                String boardId = getBoardId(testBoardInfo00);
                String postId = getPostId(enableBoardPost00);

                MockHttpServletRequestBuilder requestBuilder = get("/v1/boards/{boardId}/posts/{postId}", boardId, postId);
                setAuthToken(requestBuilder, common00);

                AtomicLong viewCount = new AtomicLong();
                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(200))
                        .andDo(mvnResult -> {
                            String responseBody = mvnResult.getResponse().getContentAsString();
                            ObjectMapper mapper = new ObjectMapper();
                            PostInfo postInfo = mapper.readValue(responseBody, PostInfo.class);
                            viewCount.set(postInfo.getViewCount());
                        });

                requestBuilder = get("/v1/boards/{boardId}/posts/{postId}", boardId, postId);
                setAuthToken(requestBuilder, common00);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(200))
                        .andExpect(jsonPath("$.viewCount").value(viewCount.get() + 1))
                        .andDo(print());
            }
        }

        @Nested
        @DisplayName("목록")
        class ListTest {

            @Test
            @DisplayName("기본")
            public void getList() throws Exception {
                String boardId = getBoardId(testBoardInfo00);

                MockHttpServletRequestBuilder requestBuilder = get("/v1/boards/{boardId}/posts", boardId);
                setAuthToken(requestBuilder, common00);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(200))
                        .andExpect(jsonPath("$.total").exists())
                        .andExpect(jsonPath("$.skip").value(0))
                        .andExpect(jsonPath("$.limit").value(20))
                        .andExpect(jsonPath("$.posts").isArray())
                        .andDo(print());
            }

            @Test
            @DisplayName("skip")
            public void skip() throws Exception {
                String boardId = getBoardId(testBoardInfo00);

                MockHttpServletRequestBuilder requestBuilder = get("/v1/boards/{boardId}/posts", boardId)
                        .param("skip", "1");
                setAuthToken(requestBuilder, common00);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(200))
                        .andExpect(jsonPath("$.total").exists())
                        .andExpect(jsonPath("$.skip").value(1))
                        .andExpect(jsonPath("$.limit").value(20))
                        .andExpect(jsonPath("$.posts").isArray())
                        .andDo(print());
            }

            @Test
            @DisplayName("limit")
            public void limit() throws Exception {
                String boardId = getBoardId(testBoardInfo00);

                MockHttpServletRequestBuilder requestBuilder = get("/v1/boards/{boardId}/posts", boardId)
                        .param("limit", "1");
                setAuthToken(requestBuilder, common00);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(200))
                        .andExpect(jsonPath("$.total").exists())
                        .andExpect(jsonPath("$.skip").value(0))
                        .andExpect(jsonPath("$.limit").value(1))
                        .andExpect(jsonPath("$.posts").isArray())
                        .andDo(print());
            }

            @Test
            @DisplayName("title")
            public void title() throws Exception {
                String boardId = getBoardId(testBoardInfo00);

                MockHttpServletRequestBuilder requestBuilder = get("/v1/boards/{boardId}/posts", boardId)
                        .param("title", enableBoardPost00.getTitle());
                setAuthToken(requestBuilder, common00);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(200))
                        .andExpect(jsonPath("$.total").value(1))
                        .andExpect(jsonPath("$.skip").value(0))
                        .andExpect(jsonPath("$.limit").value(20))
                        .andExpect(jsonPath("$.posts").isArray())
                        .andExpect(jsonPath("$.posts", hasSize(1)))
                        .andExpect(jsonPath("$.posts[0].title").value("enableBoardPost00"))
                        .andExpect(jsonPath("$.posts[0].content").doesNotExist())
                        .andDo(print());
            }

            @Test
            @DisplayName("공지 사항")
            public void notification() throws Exception {
                String boardId = getBoardId(testBoardInfo00);

                MockHttpServletRequestBuilder requestBuilder = get("/v1/boards/{boardId}/posts", boardId)
                        .param("type", "notification");
                setAuthToken(requestBuilder, common00);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(200))
                        .andExpect(jsonPath("$.total").value(0))
                        .andExpect(jsonPath("$.skip").value(0))
                        .andDo(print());
            }
        }
    }

    @Nested
    @DisplayName("게시물 발행")
    class PublishTest {

        @Test
        @DisplayName("발행")
        public void publish() throws Exception {
            String boardId = getBoardId(temporaryPostInfo00.getBoard());
            String postId = getPostId(temporaryPostInfo00);

            MockHttpServletRequestBuilder requestBuilder = post("/v1/boards/{boardId}/posts/{postId}/publish", boardId, postId);
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.status").value("publish"))
                    .andDo(print());
        }

        @Test
        @DisplayName("발행한 게시물 다시 발행")
        public void publishAlreadyPublishPost() throws Exception {
            String boardId = getBoardId(publishPostInfo00.getBoard());
            String postId = getPostId(publishPostInfo00);

            MockHttpServletRequestBuilder requestBuilder = post("/v1/boards/{boardId}/posts/{postId}/publish", boardId, postId);
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(404))
                    .andExpect(jsonPath("$.code").value("04000004"))
                    .andExpect(jsonPath("$.message").value("Not exist temporary content."))
                    .andDo(print());
        }

        @Test
        @DisplayName("다른 계정이 생성한 게시물 발행")
        public void otherCreatePost() throws Exception {
            String boardId = getBoardId(temporaryPostInfo01.getBoard());
            String postId = getPostId(temporaryPostInfo01);

            MockHttpServletRequestBuilder requestBuilder = post("/v1/boards/{boardId}/posts/{postId}/publish", boardId, postId);
            setAuthToken(requestBuilder, common01);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(401))
                    .andExpect(jsonPath("$.code").value("04000002"))
                    .andDo(print());
        }

        @Test
        @DisplayName("공지 사항 발행")
        public void publishNotification() throws Exception {
            String boardId = getBoardId(notificationPostInfo00.getBoard());
            String postId = getPostId(notificationPostInfo00);

            MockHttpServletRequestBuilder requestBuilder = post("/v1/boards/{boardId}/posts/{postId}/publish", boardId, postId);
            setAuthToken(requestBuilder, allBoardAdmin);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.status").value("publish"))
                    .andDo(print());
        }

        @Test
        @DisplayName("비활성화 게시판의 게시물 발행")
        public void publishInDisabledBoard() throws Exception {
            String boardId = getBoardId(temporaryPostInfo02.getBoard());
            String postId = getPostId(temporaryPostInfo02);

            MockHttpServletRequestBuilder requestBuilder = post("/v1/boards/{boardId}/posts/{postId}/publish", boardId, postId);
            setAuthToken(requestBuilder, allBoardAdmin);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(400))
                    .andExpect(jsonPath("$.code").value("03000001"))
                    .andExpect(jsonPath("$.message").value("Board is disabled."))
                    .andDo(print());
        }

        @Test
        @DisplayName("존재하지 않는 게시물 발행")
        public void publishNotExistPost() throws Exception {
            String boardId = getBoardId(testBoardInfo00);

            MockHttpServletRequestBuilder requestBuilder = post("/v1/boards/{boardId}/posts/unknown/publish", boardId);
            setAuthToken(requestBuilder, allBoardAdmin);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(404))
                    .andExpect(jsonPath("$.code").value("04000000"))
                    .andExpect(jsonPath("$.message").value("Not exist post."))
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("게시물 수정")
    class UpdateTest {

        @Test
        @DisplayName("제목, 내용, 댓글 허용 여부")
        public void titleContentIsAllowComment() throws Exception {
            String boardId = getBoardId(updatePostInfo00.getBoard());
            String postId = getPostId(updatePostInfo00);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("title", "update_title");
            requestBody.put("content", "update_content");
            requestBody.put("allowComment", false);

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/{boardId}/posts/{postId}", boardId, postId)
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.title").value("update_title"))
                    .andExpect(jsonPath("$.content").value("update_content"))
                    .andExpect(jsonPath("$.allowComment").value(false))
                    .andDo(print());
        }

        @Test
        @DisplayName("제목")
        public void title() throws Exception {
            String boardId = getBoardId(updatePostInfo00.getBoard());
            String postId = getPostId(updatePostInfo00);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("title", "only_title");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/{boardId}/posts/{postId}", boardId, postId)
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.title").value("only_title"))
                    .andDo(print());
        }

        @Test
        @DisplayName("내용")
        public void content() throws Exception {
            String boardId = getBoardId(updatePostInfo00.getBoard());
            String postId = getPostId(updatePostInfo00);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("content", "only_content");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/{boardId}/posts/{postId}", boardId, postId)
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.content").value("only_content"))
                    .andDo(print());
        }

        @Test
        @DisplayName("댓글 허용 여부")
        public void isAllowComment() throws Exception {
            String boardId = getBoardId(updatePostInfo00.getBoard());
            String postId = getPostId(updatePostInfo00);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("allowComment", false);

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/{boardId}/posts/{postId}", boardId, postId)
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.allowComment").value(false))
                    .andDo(print());
        }

        @Test
        @DisplayName("게시물을 공지사항으로 수정")
        public void notification() throws Exception {
            String boardId = getBoardId(updatePostInfo01.getBoard());
            String postId = getPostId(updatePostInfo01);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("type", "notification");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/{boardId}/posts/{postId}", boardId, postId)
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, allBoardAdmin);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.type").value("notification"))
                    .andDo(print());
        }

        @Test
        @DisplayName("발행한 게시물 수정")
        public void updateAlreadyPublishPost() throws Exception {
            String boardId = getBoardId(updatePostInfo02.getBoard());
            String postId = getPostId(updatePostInfo02);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("title", "post_title");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/{boardId}/posts/{postId}", boardId, postId)
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.title").value("post_title"))
                    .andDo(print());
        }

        @Nested
        @DisplayName("권한")
        class AuthTest {

            @Test
            @DisplayName("일반 계정이 게시물을 공지사항으로 수정")
            public void updateToNotificationByAccount() throws Exception {
                String boardId = getBoardId(autoPostInfo00.getBoard());
                String postId = getPostId(autoPostInfo00);

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("type", "notification");

                MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/{boardId}/posts/{postId}", boardId, postId)
                        .content(getJsonString(requestBody))
                        .contentType(MediaType.APPLICATION_JSON);
                setAuthToken(requestBuilder, common00);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(401))
                        .andExpect(jsonPath("$.code").value("04000001"))
                        .andDo(print());
            }

            @Test
            @DisplayName("다른 게시판 관리자가 게시물을 공지사항으로 수정")
            public void updateToNotificationByOtherBoardAdmin() throws Exception {
                String boardId = getBoardId(autoPostInfo01.getBoard());
                String postId = getPostId(autoPostInfo01);

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("type", "notification");

                MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/{boardId}/posts/{postId}", boardId, postId)
                        .content(getJsonString(requestBody))
                        .contentType(MediaType.APPLICATION_JSON);
                setAuthToken(requestBuilder, common01);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(401))
                        .andExpect(jsonPath("$.code").value("04000001"))
                        .andDo(print());
            }
        }
    }

    @Nested
    @DisplayName("게시물 삭제")
    class DeleteTest {

        @Test
        @DisplayName("임시 게시물 삭제")
        public void deleteTemporaryPost() throws Exception {
            String boardId = getBoardId(deletePostInfo00.getBoard());
            String postId = getPostId(deletePostInfo00);

            MockHttpServletRequestBuilder requestBuilder = delete("/v1/boards/{boardId}/posts/{postId}", boardId, postId);
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andDo(print());
        }

        @Test
        @DisplayName("발행한 게시물 삭제")
        public void deletePublishPost() throws Exception {
            String boardId = getBoardId(deletePostInfo01.getBoard());
            String postId = getPostId(deletePostInfo01);

            MockHttpServletRequestBuilder requestBuilder = delete("/v1/boards/{boardId}/posts/{postId}", boardId, postId);
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andDo(print());
        }

        @Test
        @DisplayName("다른 계정의 게시물 삭제")
        public void deleteOtherAccount() throws Exception {
            String boardId = getBoardId(deletePostInfo02.getBoard());
            String postId = getPostId(deletePostInfo02);

            MockHttpServletRequestBuilder requestBuilder = delete("/v1/boards/{boardId}/posts/{postId}", boardId, postId);
            setAuthToken(requestBuilder, common01);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(401))
                    .andExpect(jsonPath("$.code").value("04000002"))
                    .andDo(print());
        }

        @Test
        @DisplayName("비활성화 게시판의 게시물 삭제")
        public void deleteInDisabledBoard() throws Exception {
            String boardId = getBoardId(disabledPostInfo00.getBoard());
            String postId = getPostId(disabledPostInfo00);

            MockHttpServletRequestBuilder requestBuilder = delete("/v1/boards/{boardId}/posts/{postId}", boardId, postId);
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(400))
                    .andExpect(jsonPath("$.code").value("03000001"))
                    .andExpect(jsonPath("$.message").value("Board is disabled."))
                    .andDo(print());
        }

        @Test
        @DisplayName("존재하지 않는 게시물 삭제")
        public void deleteNotExistPost() throws Exception {
            String boardId = getBoardId(deleteBoardInfo00);

            MockHttpServletRequestBuilder requestBuilder = delete("/v1/boards/{boardId}/posts/unknown", boardId);
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(404))
                    .andExpect(jsonPath("$.code").value("04000000"))
                    .andExpect(jsonPath("$.message").value("Not exist post."))
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("게시물 좋아요/싫어요")
    class VoteTest {

        @Test
        @DisplayName("좋아요")
        public void upvote() throws Exception {
            String boardId = getBoardId(votePostInfo00.getBoard());
            String postId = getPostId(votePostInfo00);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("type", "upvote");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/{boardId}/posts/{postId}/vote", boardId, postId)
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.upvote").value(1))
                    .andDo(print());
        }

        @Test
        @DisplayName("싫어요")
        public void downvote() throws Exception {
            String boardId = getBoardId(votePostInfo00.getBoard());
            String postId = getPostId(votePostInfo00);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("type", "downvote");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/{boardId}/posts/{postId}/vote", boardId, postId)
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.downvote").value(1))
                    .andDo(print());
        }

        @Test
        @DisplayName("중복 좋아요")
        public void duplicateUpvote() throws Exception {
            String postId = getPostId(votePostInfo01);
            String boardId = getBoardId(votePostInfo01.getBoard());

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("type", "upvote");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/{boardId}/posts/{postId}/vote", boardId, postId)
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, common01);
            mockMvc.perform(requestBuilder);

            requestBuilder = patch("/v1/boards/{boardId}/posts/{postId}/vote", boardId, postId)
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, common01);
            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(400))
                    .andExpect(jsonPath("$.code").value("04000006"))
                    .andDo(print());
        }

        @Test
        @DisplayName("중복 싫어요")
        public void duplicateDonwvote() throws Exception {
            String boardId = getBoardId(votePostInfo01.getBoard());
            String postId = getPostId(votePostInfo01);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("type", "downvote");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/{boardId}/posts/{postId}/vote", boardId, postId)
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, common01);
            mockMvc.perform(requestBuilder);

            requestBuilder = patch("/v1/boards/{boardId}/posts/{postId}/vote", boardId, postId)
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, common01);
            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(400))
                    .andExpect(jsonPath("$.code").value("04000006"))
                    .andDo(print());
        }

        @Test
        @DisplayName("발행되지 않은 게시물")
        public void temporaryPost() throws Exception {
            String boardId = getBoardId(votePostInfo02.getBoard());
            String postId = getPostId(votePostInfo02);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("type", "upvote");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/{boardId}/posts/{postId}/vote", boardId, postId)
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(404))
                    .andExpect(jsonPath("$.code").value("04000005"))
                    .andDo(print());
        }

        @Test
        @DisplayName("다른 게시판의 게시물")
        public void otherBoard() throws Exception {
            String boardId = getBoardId(testBoardInfo00);
            String postId = getPostId(votePostInfo03);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("type", "upvote");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/{boardId}/posts/{postId}/vote", boardId, postId)
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(404))
                    .andExpect(jsonPath("$.code").value("04000000"))
                    .andExpect(jsonPath("$.message").value("Not exist post."))
                    .andDo(print());
        }

        @Test
        @DisplayName("비활성화 게시판")
        public void disabledPost() throws Exception {
            String boardId = getBoardId(votePostInfo04.getBoard());
            String postId = getPostId(votePostInfo04);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("type", "upvote");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/{boardId}/posts/{postId}/vote", boardId, postId)
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(400))
                    .andExpect(jsonPath("$.code").value("03000001"))
                    .andExpect(jsonPath("$.message").value("Board is disabled."))
                    .andDo(print());
        }

        @Test
        @DisplayName("올바르지 않은 요청")
        public void invalidRequest() throws Exception {
            String boardId = getBoardId(votePostInfo05.getBoard());
            String postId = getPostId(votePostInfo05);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("type", "unknown");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/{boardId}/posts/{postId}/vote", boardId, postId)
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(400))
                    .andExpect(jsonPath("$.code").value("01020000"))
                    .andDo(print());
        }

        @Test
        @DisplayName("타입을 포함하지 않은 요청")
        public void notIncludeType() throws Exception {
            String boardId = getBoardId(votePostInfo05.getBoard());
            String postId = getPostId(votePostInfo05);

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/{boardId}/posts/{postId}/vote", boardId, postId);
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(400))
                    .andExpect(jsonPath("$.code").value("00000001"))
                    .andDo(print());
        }
    }
}
