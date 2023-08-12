package com.illdangag.iricom.server.controller.v1;

import com.illdangag.iricom.server.data.entity.Account;
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

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@DisplayName("controller: 계정")
public class AccountControllerTest extends IricomTestSuite {
    @Autowired
    MockMvc mockMvc;

    private final TestBoardInfo testBoardInfo00 = TestBoardInfo.builder()
            .title("testBoardInfo00").isEnabled(true).adminList(Collections.singletonList(allBoardAdmin)).build();

    @Autowired
    public AccountControllerTest(ApplicationContext context) {
        super(context);

        addTestBoardInfo(testBoardInfo00);

        init();
    }

    @Nested
    @DisplayName("조회")
    class GetTest {

        @Nested
        @DisplayName("계정 정보 조회")
        class AccountGetTest {

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
        @DisplayName("목록 조회")
        class GetListTest {

            @Test
            @DisplayName("기본")
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

    @Nested
    @DisplayName("수정")
    class UpdateTest {

        @Nested
        @DisplayName("자신의 계정 정보 수정")
        class SelfUpdateTest {

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
                Account account = getAccount(common00);

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("nickname", "admin_update");
                requestBody.put("description", "admin_update");

                MockHttpServletRequestBuilder requestBuilder = patch("/v1/accounts/" + account.getId())
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
                Account account = getAccount(common00);

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("nickname", "board_update");
                requestBody.put("description", "board_update");

                MockHttpServletRequestBuilder requestBuilder = patch("/v1/accounts/" + account.getId())
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
                Account account = getAccount(common00);

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("nickname", "common_update");
                requestBody.put("description", "common_update");

                MockHttpServletRequestBuilder requestBuilder = patch("/v1/accounts/" + account.getId())
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
                Account account = getAccount(common00);

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("nickname", "self_update");
                requestBody.put("description", "self_update");

                MockHttpServletRequestBuilder requestBuilder = patch("/v1/accounts/" + account.getId())
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
}
