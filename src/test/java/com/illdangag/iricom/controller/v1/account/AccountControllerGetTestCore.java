package com.illdangag.iricom.controller.v1.account;

import com.illdangag.iricom.core.test.data.wrapper.TestAccountInfo;
import com.illdangag.iricom.IricomTestServiceSuite;
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
public class AccountControllerGetTestCore extends IricomTestServiceSuite {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    public AccountControllerGetTestCore(ApplicationContext context) {
        super(context);
    }

    @Nested
    @DisplayName("정보")
    class GetInfo {
        @Test
        @DisplayName("시스템 관리자의 본인 계정 정보 조회")
        void getSystemAccountInfo() throws Exception {
            MockHttpServletRequestBuilder requestBuilder = get("/v1/accounts/{id}", systemAdmin.getId());
            setAuthToken(requestBuilder, systemAdmin);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.email").value(systemAdmin.getEmail()))
                    .andExpect(jsonPath("$.auth").value("systemAdmin"))
                    .andDo(print());
        }

        @Test
        @DisplayName("게시판 관리자의 본인 계정 정보 조회")
        void getBoardAccountInfo() throws Exception {
            // 계정 생성
            TestAccountInfo account = setRandomAccount();
            // 게시판 생성
            setRandomBoard(Collections.singletonList(account));

            MockHttpServletRequestBuilder requestBuilder = get("/v1/accounts/{id}", account.getId());
            setAuthToken(requestBuilder, account);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.email").value(account.getEmail()))
                    .andExpect(jsonPath("$.auth").value("boardAdmin"))
                    .andDo(print());
        }

        @Test
        @DisplayName("일반 계정의 본인 계정 정보 조회")
        void getAccountInfo() throws Exception {
            // 계정 생성
            TestAccountInfo account = setRandomAccount();

            MockHttpServletRequestBuilder requestBuilder = get("/v1/accounts/{id}", account.getId());
            setAuthToken(requestBuilder, account);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.email").value(account.getEmail()))
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("목록")
    class ListInfo {
        @Test
        @DisplayName("목록 조회")
        void getAccountInfoList() throws Exception {
            // 계정 생성
            TestAccountInfo account = setRandomAccount();

            MockHttpServletRequestBuilder requestBuilder = get("/v1/accounts/");
            setAuthToken(requestBuilder, account);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.skip").value(0))
                    .andExpect(jsonPath("$.limit").value((20)))
                    .andExpect(jsonPath("$.accounts").isArray())
                    .andDo(print());
        }

        @Test
        @DisplayName("skip")
        void useSkip() throws Exception {
            // 계정 생성
            TestAccountInfo account = setRandomAccount();

            MockHttpServletRequestBuilder requestBuilder = get("/v1/accounts/")
                    .param("skip", "2");
            setAuthToken(requestBuilder, account);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.skip").value(2))
                    .andExpect(jsonPath("$.limit").value((20)))
                    .andExpect(jsonPath("$.accounts").isArray())
                    .andDo(print());
        }

        @Test
        @DisplayName("limit")
        void useLimit() throws Exception {
            // 계정 생성
            TestAccountInfo account = setRandomAccount();

            MockHttpServletRequestBuilder requestBuilder = get("/v1/accounts/")
                    .param("limit", "2");
            setAuthToken(requestBuilder, account);

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
        void useEmailKeyword() throws Exception {
            // 계정 생성
            TestAccountInfo account = setRandomAccount();

            MockHttpServletRequestBuilder requestBuilder = get("/v1/accounts/")
                    .param("keyword", account.getEmail());
            setAuthToken(requestBuilder, account);

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
        void useNotExistEmailKeyword() throws Exception {
            // 계정 생성
            TestAccountInfo account = setRandomAccount();

            MockHttpServletRequestBuilder requestBuilder = get("/v1/accounts/")
                    .param("keyword", "NOT_EXIST");
            setAuthToken(requestBuilder, account);

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
