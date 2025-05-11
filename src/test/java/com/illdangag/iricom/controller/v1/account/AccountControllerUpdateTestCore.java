package com.illdangag.iricom.controller.v1.account;

import com.illdangag.iricom.core.test.data.wrapper.TestAccountInfo;
import com.illdangag.iricom.IricomTestServiceSuite;
import lombok.extern.slf4j.Slf4j;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@DisplayName("controller: 계정 - 수정")
public class AccountControllerUpdateTestCore extends IricomTestServiceSuite {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    public AccountControllerUpdateTestCore(ApplicationContext context) {
        super(context);
    }

    @Nested
    @DisplayName("자신의 계정 정보 수정")
    class SelfUpdate {
        @Test
        @DisplayName("닉네임, 설명")
        void nicknameAndDescription() throws Exception {
            // 계정 생성
            TestAccountInfo account = setRandomAccount();

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("nickname", "common00_00");
            requestBody.put("description", "update_description");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/accounts/")
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, account);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.nickname").value("common00_00"))
                    .andExpect(jsonPath("$.description").value("update_description"))
                    .andDo(print());
        }

        @Test
        @DisplayName("닉네임")
        void nickname() throws Exception {
            // 계정 생성
            TestAccountInfo account = setRandomAccount();

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("nickname", "common00_01");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/accounts/")
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, account);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.nickname").value("common00_01"))
                    .andDo(print());
        }

        @Test
        @DisplayName("설명")
        void description() throws Exception {
            // 계정 생성
            TestAccountInfo account = setRandomAccount();

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("description", "only_description");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/accounts/")
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, account);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.description").value("only_description"))
                    .andDo(print());
        }

        @Test
        @DisplayName("닉네임 빈 문자열")
        void emptyNickname() throws Exception {
            // 계정 생성
            TestAccountInfo account = setRandomAccount();

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("description", "");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/accounts/")
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, account);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.description").value(""))
                    .andDo(print());
        }

        @Test
        @DisplayName("닉네임 문자열의 길이 초과")
        void overflowNickname() throws Exception {
            // 계정 생성
            TestAccountInfo account = setRandomAccount();

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("nickname", "012345678901234567890");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/accounts/")
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, account);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(400))
                    .andExpect(jsonPath("$.code").value("01020000"))
                    .andDo(print());
        }

        @Test
        @DisplayName("설명 문자열을 빈 문자열")
        void emptyDescription() throws Exception {
            // 계정 생성
            TestAccountInfo account = setRandomAccount();

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("nickname", TEXT_10 + TEXT_10 + "0");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/accounts/")
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, account);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(400))
                    .andExpect(jsonPath("$.code").value("01020000"))
                    .andDo(print());
        }

        @Test
        @DisplayName("설명 문자열의 길이 초과")
        void overflowDescription() throws Exception {
            // 계정 생성
            TestAccountInfo account = setRandomAccount();

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("description", TEXT_100 + "0");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/accounts/")
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, account);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(400))
                    .andExpect(jsonPath("$.code").value("01020000"))
                    .andDo(print());
        }

        @Test
        @DisplayName("닉네임 중복")
        void duplicateNickname() throws Exception {
            // 계정 생성
            TestAccountInfo account00 = setRandomAccount();
            TestAccountInfo account01 = setRandomAccount();

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("nickname", account01.getNickname());

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/accounts/")
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, account00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(400))
                    .andExpect(jsonPath("$.code").value("02000002"))
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("등록되지 않은 계정이 자신의 계정 정보 수정")
    class UnknownUpdateTest {

        @Test
        @DisplayName("닉네임과 설명 수정")
        void nicknameAndDescription() throws Exception {
            // 계정 생성
            TestAccountInfo account = setRandomAccount(true);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("nickname", "update_unknown01");
            requestBody.put("description", "update_description");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/accounts/")
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, account);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.nickname").value("update_unknown01"))
                    .andExpect(jsonPath("$.description").value("update_description"))
                    .andDo(print());
        }

        @Test
        @DisplayName("닉네임 수정")
        void nickname() throws Exception {
            // 계정 생성
            TestAccountInfo account = setRandomAccount(true);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("nickname", "update_unknown02");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/accounts/")
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, account);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.nickname").value("update_unknown02"))
                    .andDo(print());
        }

        @Test
        @DisplayName("닉네임을 빈 문자열로 수정")
        void emptyNickname() throws Exception {
            // 계정 생성
            TestAccountInfo account = setRandomAccount(true);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("nickname", "");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/accounts/")
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, account);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(400))
                    .andExpect(jsonPath("$.code").value("01020000"))
                    .andDo(print());
        }

        @Test
        @DisplayName("설명 수정")
        void description() throws Exception {
            // 계정 생성
            TestAccountInfo account = setRandomAccount(true);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("description", "update_description");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/accounts/")
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, account);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(400))
                    .andExpect(jsonPath("$.code").value("02000002"))
                    .andDo(print());
        }

        @Test
        @DisplayName("빈 문자열로 설명 수정")
        void emptyDescription() throws Exception {
            // 계정 생성
            TestAccountInfo account = setRandomAccount(true);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("description", "");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/accounts/")
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, account);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(400))
                    .andExpect(jsonPath("$.code").value("02000002"))
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("다른 계정의 정보 수정")
    class OtherUpdateTest {

        @Test
        @DisplayName("시스템 관리자가 다른 계정 정보 수정")
        void updateOtherAccountBySystemAdmin() throws Exception {
            // 계정 생성
            TestAccountInfo account = setRandomAccount();

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("nickname", "admin_update");
            requestBody.put("description", "admin_update");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/accounts/{id}", account.getId())
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, systemAdmin);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.nickname").value("admin_update"))
                    .andExpect(jsonPath("$.description").value("admin_update"))
                    .andDo(print());
        }

        @Test
        @DisplayName("게시판 관리자가 다른 계정 정보 수정")
        void updateOtherAccountByBoardAdmin() throws Exception {
            // 계정 생성
            TestAccountInfo account = setRandomAccount();
            TestAccountInfo boardAdminAccount = setRandomAccount();
            // 게시판 생성
            setRandomBoard(Collections.singletonList(boardAdminAccount));

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("nickname", "board_update");
            requestBody.put("description", "board_update");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/accounts/{id}", account.getId())
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, boardAdminAccount);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(401))
                    .andExpect(jsonPath("$.code").value("01000004"))
                    .andDo(print());
        }

        @Test
        @DisplayName("일반 계정이 다른 계정 정보 수정")
        void updateOtherAccountByAccount() throws Exception {
            // 계정 생성
            TestAccountInfo account = setRandomAccount();
            TestAccountInfo otherAccount = setRandomAccount();

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("nickname", "common_update");
            requestBody.put("description", "common_update");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/accounts/{id}", account.getId())
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, otherAccount);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(401))
                    .andExpect(jsonPath("$.code").value("01000004"))
                    .andDo(print());
        }

        @Test
        @DisplayName("일반 계정이 본인 계정을 수정")
        void updateAccount() throws Exception {
            // 계정 생성
            TestAccountInfo account = setRandomAccount();

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("nickname", "self_update");
            requestBody.put("description", "self_update");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/accounts/{id}", account.getId())
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, account);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(401))
                    .andExpect(jsonPath("$.code").value("01000004"))
                    .andDo(print());
        }
    }
}
