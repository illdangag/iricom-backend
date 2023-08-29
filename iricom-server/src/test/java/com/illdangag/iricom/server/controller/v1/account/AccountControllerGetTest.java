package com.illdangag.iricom.server.controller.v1.account;

import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.test.IricomTestSuite;
import com.illdangag.iricom.server.test.data.wrapper.TestBoardInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Collections;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@DisplayName("controller: 계정 - 조회")
public class AccountControllerGetTest extends IricomTestSuite {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    public AccountControllerGetTest(ApplicationContext context) {
        super(context);

        TestBoardInfo testBoardInfo = TestBoardInfo.builder()
                .title("testBoardInfo").isEnabled(true).adminList(Collections.singletonList(allBoardAdmin)).build();

        addTestBoardInfo(testBoardInfo);

        init();
    }

    @Nested
    @DisplayName("정보")
    class GetInfo {

        @Test
        @DisplayName("시스템 관리자의 본인 계정 정보 조회")
        public void getSystemAccountInfo() throws Exception {
            Account account = getAccount(systemAdmin);

            MockHttpServletRequestBuilder requestBuilder = get("/v1/accounts/" + account.getId());
            setAuthToken(requestBuilder, systemAdmin);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.email").value(systemAdmin.getEmail()))
                    .andExpect(jsonPath("$.auth").value("systemAdmin"))
                    .andDo(print());
        }

        @Test
        @DisplayName("게시판 관리자의 본인 계정 정보 조회")
        public void getBoardAccountInfo() throws Exception {
            Account account = getAccount(allBoardAdmin);

            MockHttpServletRequestBuilder requestBuilder = get("/v1/accounts/" + account.getId());
            setAuthToken(requestBuilder, allBoardAdmin);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.email").value(allBoardAdmin.getEmail()))
                    .andExpect(jsonPath("$.auth").value("boardAdmin"))
                    .andDo(print());
        }

        @Test
        @DisplayName("일반 계정의 본인 계정 정보 조회")
        public void getAccountInfo() throws Exception {
            Account account = getAccount(common00);

            MockHttpServletRequestBuilder requestBuilder = get("/v1/accounts/" + account.getId());
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.email").value(common00.getEmail()))
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("목록")
    class ListInfo {
        @Test
        @DisplayName("목록 조회")
        public void getAccountInfoList() throws Exception {
            MockHttpServletRequestBuilder requestBuilder = get("/v1/accounts/");
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.skip").value(0))
                    .andExpect(jsonPath("$.limit").value((20)))
                    .andExpect(jsonPath("$.accounts").isArray())
                    .andDo(print());
        }

        @Test
        @DisplayName("skip")
        public void useSkip() throws Exception {
            MockHttpServletRequestBuilder requestBuilder = get("/v1/accounts/")
                    .param("skip", "2");
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.skip").value(2))
                    .andExpect(jsonPath("$.limit").value((20)))
                    .andExpect(jsonPath("$.accounts").isArray())
                    .andDo(print());
        }

        @Test
        @DisplayName("limit")
        public void useLimit() throws Exception {
            MockHttpServletRequestBuilder requestBuilder = get("/v1/accounts/")
                    .param("limit", "2");
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.skip").value(0))
                    .andExpect(jsonPath("$.limit").value((2)))
                    .andExpect(jsonPath("$.accounts").isArray())
                    .andExpect(jsonPath("$.accounts", hasSize(2)))
                    .andDo(print());
        }

        @Test
        @DisplayName("keyword, email")
        public void useEmailKeyword() throws Exception {
            MockHttpServletRequestBuilder requestBuilder = get("/v1/accounts/")
                    .param("keyword", common00.getEmail());
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.skip").value(0))
                    .andExpect(jsonPath("$.limit").value((20)))
                    .andExpect(jsonPath("$.accounts").isArray())
                    .andExpect(jsonPath("$.accounts", hasSize(1)))
                    .andDo(print());
        }

        @Test
        @DisplayName("keyword, 존재하지 않는 email")
        public void useNotExistEmailKeyword() throws Exception {
            MockHttpServletRequestBuilder requestBuilder = get("/v1/accounts/")
                    .param("keyword", "NOT_EXIST");
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.skip").value(0))
                    .andExpect(jsonPath("$.limit").value((20)))
                    .andExpect(jsonPath("$.accounts").isArray())
                    .andExpect(jsonPath("$.accounts", hasSize(0)))
                    .andDo(print());
        }
    }
}
