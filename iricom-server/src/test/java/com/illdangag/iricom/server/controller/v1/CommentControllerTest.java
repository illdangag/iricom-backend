package com.illdangag.iricom.server.controller.v1;

import com.illdangag.iricom.server.data.entity.type.PostState;
import com.illdangag.iricom.server.data.entity.type.PostType;
import com.illdangag.iricom.server.data.entity.type.VoteType;
import com.illdangag.iricom.server.test.IricomTestSuite;
import com.illdangag.iricom.server.test.data.wrapper.TestAccountInfo;
import com.illdangag.iricom.server.test.data.wrapper.TestBoardInfo;
import com.illdangag.iricom.server.test.data.wrapper.TestCommentInfo;
import com.illdangag.iricom.server.test.data.wrapper.TestPostInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("controller: 댓글")
public class CommentControllerTest extends IricomTestSuite {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    public CommentControllerTest(ApplicationContext context) {
        super(context);
    }

    @Nested
    @DisplayName("생성")
    class CreateTest {

        @Test
        @DisplayName("기본")
        void createComment() throws Exception {
            // 계정 생성
            TestAccountInfo account = setRandomAccount();
            // 게시판 생성
            TestBoardInfo board = setRandomBoard();
            // 게시물 생성
            TestPostInfo post = setRandomPost(board, account);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("content", "comment");

            MockHttpServletRequestBuilder requestBuilder = post("/v1/boards/{boardId}/posts/{postId}/comments", board.getId(), post.getId())
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, account);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.id").exists())
                    .andExpect(jsonPath("$.content").exists())
                    .andExpect(jsonPath("$.referenceCommentId").doesNotExist())
                    .andDo(print());
        }

        @Test
        @DisplayName("대댓글")
        void createReferenceComment() throws Exception {
            // 계정 생성
            TestAccountInfo account = setRandomAccount();
            // 게시판 생성
            TestBoardInfo board = setRandomBoard();
            // 게시물 생성
            TestPostInfo post = setRandomPost(board, account);
            // 댓글 생성
            TestCommentInfo comment = setRandomComment(post, account);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("content", "reference_content");
            requestBody.put("referenceCommentId", comment.getId());

            MockHttpServletRequestBuilder requestBuilder = post("/v1/boards/{boardId}/posts/{postId}/comments", board.getId(), post.getId())
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, account);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.id").exists())
                    .andExpect(jsonPath("$.content").exists())
                    .andExpect(jsonPath("$.referenceCommentId").exists())
                    .andDo(print());
        }

        @Test
        @DisplayName("발행되지 않은 게시물에 댓글")
        void createCommentTemporaryPost() throws Exception {
            // 계정 생성
            TestAccountInfo account = setRandomAccount();
            // 게시판 생성
            TestBoardInfo board = setRandomBoard();
            // 게시물 생성
            TestPostInfo post = setRandomPost(board, account, PostType.POST, PostState.TEMPORARY);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("content", "reference_content");

            MockHttpServletRequestBuilder requestBuilder = post("/v1/boards/{boardId}/posts/{postId}/comments", board.getId(), post.getId())
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, common01);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(404))
                    .andExpect(jsonPath("$.code").value("04000000"))
                    .andDo(print());
        }

        @Test
        @DisplayName("댓글을 허용하지 않는 게시물에 댓글")
        void notAllowCommentPost() throws Exception {
            // 계정 생성
            TestAccountInfo account = setRandomAccount();
            // 게시판 생성
            TestBoardInfo board = setRandomBoard();
            // 게시물 생성
            TestPostInfo post = setRandomPost(board, account, PostType.POST, PostState.PUBLISH, false);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("content", "reference_content");

            MockHttpServletRequestBuilder requestBuilder = post("/v1/boards/{boardId}/posts/{postId}/comments", board.getId(), post.getId())
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, account);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(400))
                    .andExpect(jsonPath("$.code").value("05000002"))
                    .andDo(print());
        }

        @Test
        @DisplayName("비활성화 게시판의 게시물에 댓글")
        void disabledBoard() throws Exception {
            // 계정 생성
            TestAccountInfo account = setRandomAccount();
            // 게시판 생성
            TestBoardInfo board = setRandomBoard();
            // 게시물 생성
            TestPostInfo post = setRandomPost(board, account);
            // 게시판 비활성화
            setDisabledBoard(Collections.singletonList(board));

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("content", "new_content");

            MockHttpServletRequestBuilder requestBuilder = post("/v1/boards/{boardId}/posts/{postId}/comments", board.getId(), post.getId())
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, account);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(400))
                    .andExpect(jsonPath("$.code").value("03000001"))
                    .andExpect(jsonPath("$.message").value("Board is disabled."))
                    .andDo(print());
        }

        @Test
        @DisplayName("다른 게시판에 존재하는 게시물의 댓글")
        void testCase05() throws Exception {
            // 계정 생성
            TestAccountInfo account = setRandomAccount();
            // 게시판 생성
            TestBoardInfo board = setRandomBoard();
            TestBoardInfo otherBoard = setRandomBoard();
            // 게시물 생성
            TestPostInfo post = setRandomPost(board, account);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("content", "new_content");

            MockHttpServletRequestBuilder requestBuilder = post("/v1/boards/{boardId}/posts/{postId}/comments", otherBoard.getId(), post.getId())
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, account);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(404))
                    .andExpect(jsonPath("$.code").value("04000000"))
                    .andExpect(jsonPath("$.message").value("Not exist post."))
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("수정")
    class UpdateTest {

        @Test
        @DisplayName("기본")
        void update() throws Exception {
            // 계정 생성
            TestAccountInfo account = setRandomAccount();
            // 게시판 생성
            TestBoardInfo board = setRandomBoard();
            // 게시물 생성
            TestPostInfo post = setRandomPost(board, account);
            // 댓글 생성
            TestCommentInfo comment = setRandomComment(post, account);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("content", "update_comment");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/{boardId}/posts/{postId}/comments/{commentId}", board.getId(), post.getId(), comment.getId())
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, account);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.content").value("update_comment"))
                    .andDo(print());
        }

        @Test
        @DisplayName("존재하지 않는 댓글")
        void notExistPost() throws Exception {
            // 계정 생성
            TestAccountInfo account = setRandomAccount();
            // 게시판 생성
            TestBoardInfo board = setRandomBoard();
            // 게시물 생성
            TestPostInfo post = setRandomPost(board, account);
            // 댓글 생성
            setRandomComment(post, account);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("content", "update_comment");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/{boardId}/posts/{postId}/comments/{commentId}", board.getId(), post.getId(), "UNKNOWN")
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(404))
                    .andExpect(jsonPath("$.code").value("05000000"))
                    .andDo(print());
        }

        @Test
        @DisplayName("비활성화 게시판의 게시물에 댓글")
        void disabledBoard() throws Exception {
            // 계정 생성
            TestAccountInfo account = setRandomAccount();
            // 게시판 생성
            TestBoardInfo board = setRandomBoard();
            // 게시물 생성
            TestPostInfo post = setRandomPost(board, account);
            // 댓글 생성
            TestCommentInfo comment = setRandomComment(post, account);
            // 게시판 비활성화
            setDisabledBoard(Collections.singletonList(board));

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("content", "update_comment");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/{boardId}/posts/{postId}/comments/{commentId}", board.getId(), post.getId(), comment.getId())
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
        @DisplayName("다른 사람의 댓글 수정")
        void updateOtherCreator() throws Exception {
            // 계정 생성
            TestAccountInfo account = setRandomAccount();
            TestAccountInfo otherAccount = setRandomAccount();
            // 게시판 생성
            TestBoardInfo board = setRandomBoard();
            // 게시물 생성
            TestPostInfo post = setRandomPost(board, account);
            // 댓글 생성
            TestCommentInfo comment = setRandomComment(post, account);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("content", "update_comment");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/{boardId}/posts/{postId}/comments/{commentId}", board.getId(), post.getId(), comment.getId())
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, otherAccount);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(401))
                    .andExpect(jsonPath("$.code").value("05000003"))
                    .andExpect(jsonPath("$.message").value("Invalid authorization."))
                    .andDo(print());
        }

        @Test
        @DisplayName("다른 게시판에 존재하는 게시물의 댓글")
        void updateOtherPost() throws Exception {
            // 계정 생성
            TestAccountInfo account = setRandomAccount();
            // 게시판 생성
            TestBoardInfo board = setRandomBoard();
            TestBoardInfo otherBoard = setRandomBoard();
            // 게시물 생성
            TestPostInfo post = setRandomPost(board, account);
            // 댓글 생성
            TestCommentInfo comment = setRandomComment(post, account);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("content", "update_comment");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/{boardId}/posts/{postId}/comments/{commentId}", otherBoard.getId(), post.getId(), comment.getId())
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, account);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(404))
                    .andExpect(jsonPath("$.code").value("04000000"))
                    .andExpect(jsonPath("$.message").value("Not exist post."))
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("조회")
    class GetTest {

        @Test
        @DisplayName("목록 조회")
        void getList() throws Exception {
            // 계정 생성
            TestAccountInfo account = setRandomAccount();
            // 게시판 생성
            TestBoardInfo board = setRandomBoard();
            // 게시물 생성
            TestPostInfo post = setRandomPost(board, account);
            // 댓글 생성
            setRandomComment(post, account, 12);

            MockHttpServletRequestBuilder requestBuilder = get("/v1/boards/{boardId}/posts/{postId}/comments", board.getId(), post.getId());
            setAuthToken(requestBuilder, account);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.total").value(12))
                    .andExpect(jsonPath("$.skip").value(0))
                    .andExpect(jsonPath("$.limit").value(20))
                    .andExpect(jsonPath("$.comments").isArray())
                    .andDo(print());
        }

        @Test
        @DisplayName("대댓글 포함 조회")
        void getListIncludeReferenceComment() throws Exception {
            // 계정 생성
            TestAccountInfo account = setRandomAccount();
            // 게시판 생성
            TestBoardInfo board = setRandomBoard();
            // 게시물 생성
            TestPostInfo post = setRandomPost(board, account);
            // 댓글 생성
            TestCommentInfo comment00 = setRandomComment(post, account);
            TestCommentInfo comment01 = setRandomComment(post, account);
            setRandomComment(post, comment00, account, 3);
            TestCommentInfo comment0100 = setRandomComment(post, comment01, account);
            setRandomComment(post, comment0100, account, 3);


            MockHttpServletRequestBuilder requestBuilder = get("/v1/boards/{boardId}/posts/{postId}/comments", board.getId(), post.getId())
                    .param("includeComment", "true");
            setAuthToken(requestBuilder, account);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.total").value(2))
                    .andExpect(jsonPath("$.skip").value(0))
                    .andExpect(jsonPath("$.limit").value(20))
                    .andExpect(jsonPath("$.comments").isArray())
                    .andExpect(jsonPath("$.comments[0].nestedComments").isArray())
                    .andExpect(jsonPath("$.comments[1].nestedComments").isArray())
                    .andDo(print());
        }

        @Test
        @DisplayName("skip")
        void skip() throws Exception {
            // 계정 생성
            TestAccountInfo account = setRandomAccount();
            // 게시판 생성
            TestBoardInfo board = setRandomBoard();
            // 게시물 생성
            TestPostInfo post = setRandomPost(board, account);
            // 댓글 생성
            setRandomComment(post, account, 13);

            MockHttpServletRequestBuilder requestBuilder = get("/v1/boards/{boardId}/posts/{postId}/comments", board.getId(), post.getId())
                    .param("skip", "10");
            setAuthToken(requestBuilder, account);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.total").value(13))
                    .andExpect(jsonPath("$.skip").value(10))
                    .andExpect(jsonPath("$.limit").value(20))
                    .andExpect(jsonPath("$.comments").isArray())
                    .andExpect(jsonPath("$.comments", hasSize(3)))
                    .andDo(print());
        }

        @Test
        @DisplayName("limit")
        void limit() throws Exception {
            // 계정 생성
            TestAccountInfo account = setRandomAccount();
            // 게시판 생성
            TestBoardInfo board = setRandomBoard();
            // 게시물 생성
            TestPostInfo post = setRandomPost(board, account);
            // 댓글 생성
            setRandomComment(post, account, 13);

            MockHttpServletRequestBuilder requestBuilder = get("/v1/boards/{boardId}/posts/{postId}/comments", board.getId(), post.getId())
                    .param("limit", "4");
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.total").value(13))
                    .andExpect(jsonPath("$.skip").value(0))
                    .andExpect(jsonPath("$.limit").value(4))
                    .andExpect(jsonPath("$.comments").isArray())
                    .andExpect(jsonPath("$.comments", hasSize(4)))
                    .andDo(print());
        }

        @Test
        @DisplayName("대댓글 기준")
        void getReferenceComment() throws Exception {
            // 계정 생성
            TestAccountInfo account = setRandomAccount();
            // 게시판 생성
            TestBoardInfo board = setRandomBoard();
            // 게시물 생성
            TestPostInfo post = setRandomPost(board, account);
            // 댓글 생성
            TestCommentInfo comment00 = setRandomComment(post, account);
            TestCommentInfo comment0000 = setRandomComment(post, comment00, account);
            setRandomComment(post, comment0000, account);
            setRandomComment(post, account);


            MockHttpServletRequestBuilder requestBuilder = get("/v1/boards/{boardId}/posts/{postId}/comments", board.getId(), post.getId())
                    .param("referenceCommentId", comment0000.getId());
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.total").value(1))
                    .andExpect(jsonPath("$.skip").value(0))
                    .andExpect(jsonPath("$.limit").value(20))
                    .andExpect(jsonPath("$.comments").isArray())
                    .andExpect(jsonPath("$.comments", hasSize(1)))
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("삭제")
    class DeleteTest {

        @Test
        @DisplayName("기본")
        void deleteComment() throws Exception {
            // 계정 생성
            TestAccountInfo account = setRandomAccount();
            // 게시판 생성
            TestBoardInfo board = setRandomBoard();
            // 게시물 생성
            TestPostInfo post = setRandomPost(board, account);
            // 댓글 생성
            TestCommentInfo comment = setRandomComment(post, account);

            MockHttpServletRequestBuilder requestBuilder = delete("/v1/boards/{boardId}/posts/{postId}/comments/{commentId}", board.getId(), post.getId(), comment.getId());
            setAuthToken(requestBuilder, account);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.deleted").value(true))
                    .andDo(print());
        }

        @Test
        @DisplayName("대댓글")
        void deleteReferenceComment() throws Exception {
            // 계정 생성
            TestAccountInfo account = setRandomAccount();
            // 게시판 생성
            TestBoardInfo board = setRandomBoard();
            // 게시물 생성
            TestPostInfo post = setRandomPost(board, account);
            // 댓글 생성
            TestCommentInfo comment00 = setRandomComment(post, account);
            TestCommentInfo comment0000 = setRandomComment(post, comment00, account);


            MockHttpServletRequestBuilder requestBuilder = delete("/v1/boards/{boardId}/posts/{postId}/comments/{commentId}", board.getId(), post.getId(), comment0000.getId());
            setAuthToken(requestBuilder, account);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.deleted").value(true))
                    .andDo(print());
        }

        @Test
        @DisplayName("이미 삭제한 댓글")
        void deletedComment() throws Exception {
            // 계정 생성
            TestAccountInfo account = setRandomAccount();
            // 게시판 생성
            TestBoardInfo board = setRandomBoard();
            // 게시물 생성
            TestPostInfo post = setRandomPost(board, account);
            // 댓글 생성
            TestCommentInfo comment = setRandomComment(post, account);
            setDeletedComment(Collections.singletonList(comment));

            MockHttpServletRequestBuilder requestBuilder = delete("/v1/boards/{boardId}/posts/{postId}/comments/{commentId}", board.getId(), post.getId(), comment.getId());
            setAuthToken(requestBuilder, account);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.deleted").value(true))
                    .andDo(print());
        }

        @Test
        @DisplayName("다른 사람이 작성한 댓글 삭제")
        void deleteOtherComment() throws Exception {
            // 계정 생성
            TestAccountInfo account = setRandomAccount();
            TestAccountInfo otherAccount = setRandomAccount();
            // 게시판 생성
            TestBoardInfo board = setRandomBoard();
            // 게시물 생성
            TestPostInfo post = setRandomPost(board, account);
            // 댓글 생성
            TestCommentInfo comment = setRandomComment(post, account);

            MockHttpServletRequestBuilder requestBuilder = delete("/v1/boards/{boardId}/posts/{postId}/comments/{commentId}", board.getId(), post.getId(), comment.getId());
            setAuthToken(requestBuilder, otherAccount);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(401))
                    .andExpect(jsonPath("$.code").value("05000004"))
                    .andExpect(jsonPath("$.message").value("Invalid authorization."))
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("댓글 투표")
    class VoteTest {
        @Test
        @DisplayName("좋아요")
        void upvote() throws Exception {
            // 계정 생성
            TestAccountInfo account = setRandomAccount();
            // 게시판 생성
            TestBoardInfo board = setRandomBoard();
            // 게시물 생성
            TestPostInfo post = setRandomPost(board, account);
            // 댓글 생성
            TestCommentInfo comment = setRandomComment(post, account);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("type", "upvote");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/{boardId}/posts/{postId}/comments/{commentId}/vote", board.getId(), post.getId(), comment.getId())
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, account);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.upvote").value(1))
                    .andDo(print());
        }

        @Test
        @DisplayName("싫어요")
        void downvote() throws Exception {
            // 계정 생성
            TestAccountInfo account = setRandomAccount();
            // 게시판 생성
            TestBoardInfo board = setRandomBoard();
            // 게시물 생성
            TestPostInfo post = setRandomPost(board, account);
            // 댓글 생성
            TestCommentInfo comment = setRandomComment(post, account);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("type", "downvote");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/{boardId}/posts/{postId}/comments/{commentId}/vote", board.getId(), post.getId(), comment.getId())
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, account);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.downvote").value(1))
                    .andDo(print());
        }

        @Test
        @DisplayName("대댓글에 좋아요")
        void upvoteReferenceComment() throws Exception {
            // 계정 생성
            TestAccountInfo account = setRandomAccount();
            // 게시판 생성
            TestBoardInfo board = setRandomBoard();
            // 게시물 생성
            TestPostInfo post = setRandomPost(board, account);
            // 댓글 생성
            TestCommentInfo comment00 = setRandomComment(post, account);
            TestCommentInfo comment0000 = setRandomComment(post, comment00, account);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("type", "upvote");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/{boardId}/posts/{postId}/comments/{commentId}/vote", board.getId(), post.getId(), comment0000.getId())
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, account);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.upvote").value(1))
                    .andDo(print());
        }

        @Test
        @DisplayName("대댓글에 싫어요")
        void downvoteReferenceComment() throws Exception {
            // 계정 생성
            TestAccountInfo account = setRandomAccount();
            // 게시판 생성
            TestBoardInfo board = setRandomBoard();
            // 게시물 생성
            TestPostInfo post = setRandomPost(board, account);
            // 댓글 생성
            TestCommentInfo comment00 = setRandomComment(post, account);
            TestCommentInfo comment0000 = setRandomComment(post, comment00, account);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("type", "downvote");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/{boardId}/posts/{postId}/comments/{commentId}/vote", board.getId(), post.getId(), comment0000.getId())
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, account);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.downvote").value(1))
                    .andDo(print());
        }

        @Test
        @DisplayName("중복 좋아요")
        void duplicateUpvote() throws Exception {
            // 계정 생성
            TestAccountInfo account = setRandomAccount();
            // 게시판 생성
            TestBoardInfo board = setRandomBoard();
            // 게시물 생성
            TestPostInfo post = setRandomPost(board, account);
            // 댓글 생성
            TestCommentInfo comment = setRandomComment(post, account);
            // 댓글 좋아요
            setVoteComment(comment, account, VoteType.UPVOTE);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("type", "upvote");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/{boardId}/posts/{postId}/comments/{commentId}/vote", board.getId(), post.getId(), comment.getId())
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, account);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(400))
                    .andExpect(jsonPath("$.code").value("05000005"))
                    .andDo(print());
        }

        @Test
        @DisplayName("중복 싫어요")
        void duplicateDownvote() throws Exception {
            // 계정 생성
            TestAccountInfo account = setRandomAccount();
            // 게시판 생성
            TestBoardInfo board = setRandomBoard();
            // 게시물 생성
            TestPostInfo post = setRandomPost(board, account);
            // 댓글 생성
            TestCommentInfo comment = setRandomComment(post, account);
            // 댓글 좋아요
            setVoteComment(comment, account, VoteType.DOWNVOTE);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("type", "downvote");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/{boardId}/posts/{postId}/comments/{commentId}/vote", board.getId(), post.getId(), comment.getId())
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, account);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(400))
                    .andExpect(jsonPath("$.code").value("05000005"))
                    .andDo(print());
        }

        @Test
        @DisplayName("올바르지 않은 요청")
        void invalidType() throws Exception {
            // 계정 생성
            TestAccountInfo account = setRandomAccount();
            // 게시판 생성
            TestBoardInfo board = setRandomBoard();
            // 게시물 생성
            TestPostInfo post = setRandomPost(board, account);
            // 댓글 생성
            TestCommentInfo comment = setRandomComment(post, account);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("type", "unknown");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/{boardId}/posts/{postId}/comments/{commentId}/vote", board.getId(), post.getId(), comment.getId())
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, account);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(400))
                    .andExpect(jsonPath("$.code").value("01020000"))
                    .andDo(print());
        }

        @Test
        @DisplayName("댓글을 허용하지 않은 게시물")
        void notAllowCommentPost() throws Exception {
            // 계정 생성
            TestAccountInfo account = setRandomAccount();
            // 게시판 생성
            TestBoardInfo board = setRandomBoard();
            // 게시물 생성
            TestPostInfo post = setRandomPost(board, account);
            // 댓글 생성
            TestCommentInfo comment = setRandomComment(post, account);
            // 게시물을 댓글 작성 불가로 변경
            updateDisabledAllowComment(post);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("type", "upvote");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/{boardId}/posts/{postId}/comments/{commentId}/vote", board.getId(), post.getId(), comment.getId())
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, account);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(400))
                    .andExpect(jsonPath("$.code").value("05000002"))
                    .andDo(print());
        }

        @Test
        @DisplayName("비활성화 게시판의 게시물의 댓글")
        void disabledBoard() throws Exception {
            // 계정 생성
            TestAccountInfo account = setRandomAccount();
            // 게시판 생성
            TestBoardInfo board = setRandomBoard();
            // 게시물 생성
            TestPostInfo post = setRandomPost(board, account);
            // 댓글 생성
            TestCommentInfo comment = setRandomComment(post, account);
            // 게시판 비활성화
            setDisabledBoard(Collections.singletonList(board));

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("type", "upvote");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/{boardId}/posts/{postId}/comments/{commentId}/vote", board.getId(), post.getId(), comment.getId())
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, account);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(400))
                    .andExpect(jsonPath("$.code").value("03000001"))
                    .andExpect(jsonPath("$.message").value("Board is disabled."))
                    .andDo(print());
        }

        @Test
        @DisplayName("해당 게시판에 존재하지 않는 게시물")
        void notExistPost() throws Exception {
            // 계정 생성
            TestAccountInfo account = setRandomAccount();
            // 게시판 생성
            TestBoardInfo board = setRandomBoard();
            TestBoardInfo otherBoard = setRandomBoard();
            // 게시물 생성
            TestPostInfo post = setRandomPost(board, account);
            // 댓글 생성
            TestCommentInfo comment = setRandomComment(post, account);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("type", "upvote");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/{boardId}/posts/{postId}/comments/{commentId}/vote", otherBoard.getId(), post.getId(), comment.getId())
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
        @DisplayName("다른 게시물의 댓글")
        void otherPostComment() throws Exception {
            // 계정 생성
            TestAccountInfo account = setRandomAccount();
            // 게시판 생성
            TestBoardInfo board = setRandomBoard();
            // 게시물 생성
            TestPostInfo post = setRandomPost(board, account);
            TestPostInfo otherPost = setRandomPost(board, account);
            // 댓글 생성
            setRandomComment(post, account);
            TestCommentInfo otherComment = setRandomComment(otherPost, account);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("type", "upvote");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/{boardId}/posts/{postId}/comments/{commentId}/vote", board.getId(), post.getId(), otherComment.getPost())
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(404))
                    .andExpect(jsonPath("$.code").value("05000000"))
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("신고된 댓글")
    class ReportTest {
        @Test
        @DisplayName("신고된 댓글 조회")
        void testCase00() throws Exception {
            // 계정 생성
            TestAccountInfo account = setRandomAccount();
            // 게시판 생성
            TestBoardInfo board = setRandomBoard();
            // 게시물 생성
            TestPostInfo post = setRandomPost(board, account);
            // 댓글 생성
            TestCommentInfo comment = setRandomComment(post, account);
            // 댓글 신고
            setRandomCommentReport(comment, account);

            MockHttpServletRequestBuilder requestBuilder = get("/v1/boards/{boardId}/posts/{postId}/comments", board.getId(), post.getId());
            setAuthToken(requestBuilder, account);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.total").value(1))
                    .andExpect(jsonPath("$.skip").value(0))
                    .andExpect(jsonPath("$.limit").value(20))
                    .andExpect(jsonPath("$.comments").isArray())
                    .andDo(print());
        }
    }
}
