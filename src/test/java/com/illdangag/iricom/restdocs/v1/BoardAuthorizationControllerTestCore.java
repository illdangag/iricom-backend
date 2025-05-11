package com.illdangag.iricom.restdocs.v1;

import com.illdangag.iricom.core.restdocs.snippet.IricomFieldsSnippet;
import com.illdangag.iricom.core.test.data.wrapper.TestAccountInfo;
import com.illdangag.iricom.core.test.data.wrapper.TestBoardInfo;
import com.illdangag.iricom.IricomTestServiceSuite;
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

@DisplayName("restdoc: 게시판 관리자")
public class BoardAuthorizationControllerTestCore extends IricomTestServiceSuite {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    public BoardAuthorizationControllerTestCore(ApplicationContext context) {
        super(context);
    }

    @Test
    @DisplayName("게시판 관리자 추가")
    public void at001() throws Exception {
        // 계정 생성
        TestAccountInfo account = setRandomAccount();
        // 계시판 생성
        TestBoardInfo board = setRandomBoard();

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("accountId", account.getId());
        requestBody.put("boardId", board.getId());

        MockHttpServletRequestBuilder requestBuilder = post("/v1/auth/boards")
                .content(getJsonString(requestBody))
                .contentType(MediaType.APPLICATION_JSON);
        setAuthToken(requestBuilder, systemAdmin);

        List<FieldDescriptor> fieldDescriptorList = new LinkedList<>();
        fieldDescriptorList.addAll(IricomFieldsSnippet.getBoard(""));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getAccount("accounts.[]."));

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(200))
                .andDo(print())
                .andDo(document("AT_001",
                        preprocessRequest(
                                modifyUris().scheme("https").host("api.iricom.com").removePort(),
                                removeHeaders("Authorization"),
                                prettyPrint()
                        ),
                        preprocessResponse(
                                prettyPrint()
                        ),
                        requestHeaders(
//                                headerWithName("Authorization").description("firebase 토큰")
                        ),
                        requestFields(
                                fieldWithPath("accountId").description("사용자 아이디"),
                                fieldWithPath("boardId").description("게시판 아이디")
                        ),
                        responseFields(fieldDescriptorList.toArray(FieldDescriptor[]::new))
                ));
    }

    @Test
    @DisplayName("게시판 관리자 목록 조회")
    public void at002() throws Exception {
        // 계정 생성
        TestAccountInfo account = setRandomAccount();
        // 계시판 생성
        TestBoardInfo board = setRandomBoard(Collections.singletonList(account));

        MockHttpServletRequestBuilder requestBuilder = get("/v1/auth/boards")
                .param("skip", "0")
                .param("limit", "5")
                .param("keyword", board.getTitle())
                .param("enabled", "true");
        setAuthToken(requestBuilder, systemAdmin);

        List<FieldDescriptor> fieldDescriptorList = new LinkedList<>();
        fieldDescriptorList.addAll(IricomFieldsSnippet.getSearchList(""));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getBoard("boardAdmins.[]."));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getAccount("boardAdmins.[].accounts.[]."));

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(200))
                .andDo(print())
                .andDo(document("AT_002",
                        preprocessRequest(
                                modifyUris().scheme("https").host("api.iricom.com").removePort(),
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
                                parameterWithName("keyword").description("검색어"),
                                parameterWithName("enabled").description("활성화 여부")
                        ),
                        responseFields(fieldDescriptorList.toArray(FieldDescriptor[]::new))
                ));
    }

    @Test
    @DisplayName("게시판 관리자 조회")
    public void at003() throws Exception {
        // 계정 생성
        TestAccountInfo account = setRandomAccount();
        // 계시판 생성
        TestBoardInfo board = setRandomBoard(Collections.singletonList(account));

        MockHttpServletRequestBuilder requestBuilder = get("/v1/auth/boards/{id}", board.getId());
        setAuthToken(requestBuilder, systemAdmin);

        List<FieldDescriptor> fieldDescriptorList = new LinkedList<>();
        fieldDescriptorList.addAll(IricomFieldsSnippet.getBoard(""));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getAccount("accounts.[]."));

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(200))
                .andDo(print())
                .andDo(document("AT_003",
                        preprocessRequest(
                                modifyUris().scheme("https").host("api.iricom.com").removePort(),
                                removeHeaders("Authorization"),
                                prettyPrint()
                        ),
                        preprocessResponse(
                                prettyPrint()
                        ),
                        requestHeaders(
//                                headerWithName("Authorization").description("firebase 토큰")
                        ),
                        pathParameters(
                                parameterWithName("id").description("아이디")
                        ),
                        responseFields(fieldDescriptorList.toArray(FieldDescriptor[]::new))
                ));
    }

    @Test
    @DisplayName("게시판 관리자 삭제")
    public void at004() throws Exception {
        // 계정 생성
        TestAccountInfo account00 = setRandomAccount();
        TestAccountInfo account01 = setRandomAccount();
        // 계시판 생성
        TestBoardInfo board = setRandomBoard(Arrays.asList(account00, account01));

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("accountId", account00.getId());
        requestBody.put("boardId", board.getId());

        MockHttpServletRequestBuilder requestBuilder = delete("/v1/auth/boards")
                .content(getJsonString(requestBody))
                .contentType(MediaType.APPLICATION_JSON);
        setAuthToken(requestBuilder, systemAdmin);

        List<FieldDescriptor> fieldDescriptorList = new LinkedList<>();
        fieldDescriptorList.addAll(IricomFieldsSnippet.getBoard(""));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getAccount("accounts.[]."));

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(200))
                .andDo(print())
                .andDo(document("AT_004",
                        preprocessRequest(
                                modifyUris().scheme("https").host("api.iricom.com").removePort(),
                                removeHeaders("Authorization"),
                                prettyPrint()
                        ),
                        preprocessResponse(
                                prettyPrint()
                        ),
                        requestHeaders(
//                                headerWithName("Authorization").description("firebase 토큰")
                        ),
                        requestFields(
                                fieldWithPath("accountId").description("사용자 아이디"),
                                fieldWithPath("boardId").description("게시판 아이디")
                        ),
                        responseFields(fieldDescriptorList.toArray(FieldDescriptor[]::new))
                ));
    }
}
