package com.illdangag.iricom.server.restdocs.v1;

import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.data.entity.Board;
import com.illdangag.iricom.server.restdocs.snippet.IricomFieldsSnippet;
import com.illdangag.iricom.server.test.IricomTestSuite;
import com.illdangag.iricom.server.test.data.wrapper.TestBoardInfo;
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
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("restdoc: 게시판 관리자")
public class BoardAuthorizationControllerTest extends IricomTestSuite {
    @Autowired
    MockMvc mockMvc;

    private static final TestBoardInfo boardAdminBoard00 = TestBoardInfo.builder()
            .title("boardAdminBoard00").description("boardAdminBoard00").isEnabled(true)
            .adminList(Collections.singletonList(allBoardAdmin)).build();

    @Autowired
    public BoardAuthorizationControllerTest(ApplicationContext context) {
        super(context);

        List<TestBoardInfo> testBoardInfoList = Arrays.asList(boardAdminBoard00);

        super.setBoard(testBoardInfoList);
    }

    @Test
    @DisplayName("게시판 관리자 추가")
    public void at001() throws Exception {
        Account account = getAccount(toEnableBoardAdmin);
        Board board = getBoard(boardAdminBoard00);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("accountId", account.getId());
        requestBody.put("boardId", board.getId());

        MockHttpServletRequestBuilder requestBuilder = post("/v1/auth/boards")
                .content(getJsonString(requestBody))
                .contentType(MediaType.APPLICATION_JSON);
        setAuthToken(requestBuilder, systemAdmin);

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(200))
                .andDo(print())
                .andDo(document("AT_001",
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
                        requestFields(
                                fieldWithPath("accountId").description("사용자 아이디"),
                                fieldWithPath("boardId").description("게시판 아이디")
                        )
                ));
    }

    @Test
    @DisplayName("게시판 관리자 조회")
    public void at002() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = get("/v1/auth/boards")
                .param("skip", "0")
                .param("limit", "5")
                .param("keyword", "boardAdminBoard00")
                .param("enabled", "true");
        setAuthToken(requestBuilder, systemAdmin);

        List<FieldDescriptor> fieldDescriptorList = new LinkedList<>();
        fieldDescriptorList.addAll(IricomFieldsSnippet.getSearchList(""));
        fieldDescriptorList.add(fieldWithPath("boardAdmins.[].id").description("게시판 아이디"));
        fieldDescriptorList.add(fieldWithPath("boardAdmins.[].title").description("게시판 제목"));
        fieldDescriptorList.add(fieldWithPath("boardAdmins.[].description").description("게시판 설명"));
        fieldDescriptorList.add(fieldWithPath("boardAdmins.[].isEnabled").description("게시판 활성화 여부"));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getAccount("boardAdmins.[].accounts.[]."));

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(200))
                .andDo(print())
                .andDo(document("AT_002",
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
                                parameterWithName("keyword").description("검색어"),
                                parameterWithName("enabled").description("활성화 여부")
                        ),
                        responseFields(fieldDescriptorList.toArray(FieldDescriptor[]::new))
                ));
    }

    @Test
    @DisplayName("게시판 관리자 조회")
    public void at003() throws Exception {
        Board board = getBoard(boardAdminBoard00);
        MockHttpServletRequestBuilder requestBuilder = get("/v1/auth/boards/{id}", board.getId());
        setAuthToken(requestBuilder, systemAdmin);

        List<FieldDescriptor> fieldDescriptorList = new LinkedList<>();
        fieldDescriptorList.add(fieldWithPath("id").description("게시판 아이디"));
        fieldDescriptorList.add(fieldWithPath("title").description("게시판 제목"));
        fieldDescriptorList.add(fieldWithPath("description").description("게시판 설명"));
        fieldDescriptorList.add(fieldWithPath("isEnabled").description("게시판 활셩화 여부"));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getAccount("accounts.[]."));

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(200))
                .andDo(print())
                .andDo(document("AT_003",
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
                        pathParameters(
                                parameterWithName("id").description("아이디")
                        ),
                        responseFields(fieldDescriptorList.toArray(FieldDescriptor[]::new))
                ));
    }

    @Test
    @DisplayName("게시판 관리자 삭제")
    public void at004() throws Exception {
        Account account = getAccount(toDisableBoardAdmin);
        Board board = getBoard(boardAdminBoard00);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("accountId", account.getId());
        requestBody.put("boardId", board.getId());

        MockHttpServletRequestBuilder requestBuilder = delete("/v1/auth/boards")
                .content(getJsonString(requestBody))
                .contentType(MediaType.APPLICATION_JSON);
        setAuthToken(requestBuilder, systemAdmin);

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(200))
                .andDo(print())
                .andDo(document("AT_004",
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
                        requestFields(
                                fieldWithPath("accountId").description("사용자 아이디"),
                                fieldWithPath("boardId").description("게시판 아이디")
                        )
                ));
    }
}
