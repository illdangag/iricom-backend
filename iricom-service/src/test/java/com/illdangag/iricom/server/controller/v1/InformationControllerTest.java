package com.illdangag.iricom.server.controller.v1;

import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.test.IricomTestSuite;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("본인 정보 조회")
public class InformationControllerTest extends IricomTestSuite {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    public InformationControllerTest(ApplicationContext context) {
        super(context);
    }

    @Nested
    @DisplayName("기본 정보")
    class DefaultInfoGet {

        @Test
        @DisplayName("관리자 계정")
        public void testCase00() throws Exception {
            Account account = getAccount(systemAdmin);

            MockHttpServletRequestBuilder requestBuilder = get("/v1/infos");
            setAuthToken(requestBuilder, systemAdmin);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.email").value(account.getEmail()))
                    .andExpect(jsonPath("$.auth").value("systemAdmin"))
                    .andDo(print());
        }

        @Test
        @DisplayName("게시판 관리자 계정")
        public void testCase01() throws Exception {
            Account account = getAccount(allBoardAdmin);

            MockHttpServletRequestBuilder requestBuilder = get("/v1/infos");
            setAuthToken(requestBuilder, allBoardAdmin);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.email").value(account.getEmail()))
                    .andExpect(jsonPath("$.auth").value("boardAdmin"))
                    .andDo(print());
        }

        @Test
        @DisplayName("일반 계정")
        public void testCase02() throws Exception {
            Account account = getAccount(common00);

            MockHttpServletRequestBuilder requestBuilder = get("/v1/infos");
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.email").value(account.getEmail()))
                    .andExpect(jsonPath("$.auth").value("account"))
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("작성한 게시물 목록")
    class GetPostList {
        @Test
        public void testCase00() throws Exception {
            MockHttpServletRequestBuilder requestBuilder = get("/v1/infos/posts");
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.total").exists())
                    .andExpect(jsonPath("$.skip").exists())
                    .andExpect(jsonPath("$.limit").exists())
                    .andExpect(jsonPath("$.posts").isArray())
                    .andDo(print());
        }
    }
}
