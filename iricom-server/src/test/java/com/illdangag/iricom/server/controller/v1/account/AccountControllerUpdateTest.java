package com.illdangag.iricom.server.controller.v1.account;

import com.illdangag.iricom.server.test.IricomTestSuite;
import com.illdangag.iricom.server.test.data.wrapper.TestBoardInfo;
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
public class AccountControllerUpdateTest extends IricomTestSuite {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    public AccountControllerUpdateTest(ApplicationContext context) {
        super(context);

        TestBoardInfo testBoardInfo = TestBoardInfo.builder()
                .title("testBoardInfo").isEnabled(true).adminList(Collections.singletonList(allBoardAdmin)).build();

        addTestBoardInfo(testBoardInfo);

        init();
    }

    @Nested
    @DisplayName("자신의 계정 정보 수정")
    class SelfUpdate {
        @Test
        @DisplayName("닉네임, 설명")
        public void nicknameAndDescription() throws Exception {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("nickname", "common00_00");
            requestBody.put("description", "update_description");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/accounts/")
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.nickname").value("common00_00"))
                    .andExpect(jsonPath("$.description").value("update_description"))
                    .andDo(print());
        }

        @Test
        @DisplayName("닉네임")
        public void nickname() throws Exception {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("nickname", "common00_01");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/accounts/")
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.nickname").value("common00_01"))
                    .andDo(print());
        }

        @Test
        @DisplayName("설명")
        public void description() throws Exception {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("description", "only_description");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/accounts/")
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.description").value("only_description"))
                    .andDo(print());
        }

        @Test
        @DisplayName("닉네임 빈 문자열")
        public void emptyNickname() throws Exception {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("description", "");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/accounts/")
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.description").value(""))
                    .andDo(print());
        }

        @Test
        @DisplayName("닉네임 문자열의 길이 초과")
        public void overflowNickname() throws Exception {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("nickname", "012345678901234567890");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/accounts/")
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(400))
                    .andExpect(jsonPath("$.code").value("01020000"))
                    .andDo(print());
        }

        @Test
        @DisplayName("설명 문자열을 빈 문자열")
        public void emptyDescription() throws Exception {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("nickname", "012345678901234567890");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/accounts/")
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(400))
                    .andExpect(jsonPath("$.code").value("01020000"))
                    .andDo(print());
        }

        @Test
        @DisplayName("설명 문자열의 길이 초과")
        public void overflowDescription() throws Exception {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("description", "01234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/accounts/")
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(400))
                    .andExpect(jsonPath("$.code").value("01020000"))
                    .andDo(print());
        }

        @Test
        @DisplayName("닉네임 중복")
        public void duplicateNickname() throws Exception {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("nickname", "admin");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/accounts/")
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, common00);

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
        public void nicknameAndDescription() throws Exception {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("nickname", "update_unknown01");
            requestBody.put("description", "update_description");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/accounts/")
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, unknown01);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.nickname").value("update_unknown01"))
                    .andExpect(jsonPath("$.description").value("update_description"))
                    .andDo(print());
        }

        @Test
        @DisplayName("닉네임 수정")
        public void nickname() throws Exception {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("nickname", "update_unknown02");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/accounts/")
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, unknown02);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.nickname").value("update_unknown02"))
                    .andDo(print());
        }

        @Test
        @DisplayName("닉네임을 빈 문자열로 수정")
        public void emptyNickname() throws Exception {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("nickname", "");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/accounts/")
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, unknown00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(400))
                    .andExpect(jsonPath("$.code").value("01020000"))
                    .andDo(print());
        }

        @Test
        @DisplayName("설명 수정")
        public void description() throws Exception {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("description", "update_description");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/accounts/")
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, unknown00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(400))
                    .andExpect(jsonPath("$.code").value("02000002"))
                    .andDo(print());
        }

        @Test
        @DisplayName("빈 문자열로 설명 수정")
        public void emptyDescription() throws Exception {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("description", "");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/accounts/")
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, unknown00);

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
        public void updateOtherAccountBySystemAdmin() throws Exception {
            String accountId = getAccountId(common00);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("nickname", "admin_update");
            requestBody.put("description", "admin_update");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/accounts/" + accountId)
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
        public void updateOtherAccountByBoardAdmin() throws Exception {
            String accountId = getAccountId(common00);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("nickname", "board_update");
            requestBody.put("description", "board_update");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/accounts/" + accountId)
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, enableBoardAdmin);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(401))
                    .andExpect(jsonPath("$.code").value("01000004"))
                    .andDo(print());
        }

        @Test
        @DisplayName("일반 계정이 다른 계정 정보 수정")
        public void updateOtherAccountByAccount() throws Exception {
            String accountId = getAccountId(common00);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("nickname", "common_update");
            requestBody.put("description", "common_update");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/accounts/" + accountId)
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, common01);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(401))
                    .andExpect(jsonPath("$.code").value("01000004"))
                    .andDo(print());
        }

        @Test
        @DisplayName("일반 계정이 본인 계정을 수정")
        public void updateAccount() throws Exception {
            String accountId = getAccountId(common00);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("nickname", "self_update");
            requestBody.put("description", "self_update");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/accounts/" + accountId)
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(401))
                    .andExpect(jsonPath("$.code").value("01000004"))
                    .andDo(print());
        }
    }
}
