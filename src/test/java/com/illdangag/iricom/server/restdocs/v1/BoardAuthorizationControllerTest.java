package com.illdangag.iricom.server.restdocs.v1;

import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.data.entity.Board;
import com.illdangag.iricom.server.test.IricomTestSuite;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("restdoc: 게시판 관리자")
public class BoardAuthorizationControllerTest extends IricomTestSuite {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    public BoardAuthorizationControllerTest(ApplicationContext context) {
        super(context);
    }

    @Test
    @DisplayName("게시판 관리자 추가")
    @Order(0)
    public void testCase00() throws Exception {
        Account account = getAccount(toEnableBoardAdmin);
        Board board = getBoard(enableBoard);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("accountId", account.getId());
        requestBody.put("boardId", board.getId());

        MockHttpServletRequestBuilder requestBuilder = post("/v1/auth/board")
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
//                                        headerWithName("Authorization").description("firebase 토큰")
                        ),
                        requestFields(
                                fieldWithPath("accountId").description("사용자 아이디"),
                                fieldWithPath("boardId").description("게시판 아이디")
                        )
                ));
    }

    @Test
    @DisplayName("게시판 관리자 조회")
    @Order(1)
    public void testCase01() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = get("/v1/auth/board")
                .param("skip", "0")
                .param("limit", "20")
                .param("keyword", "createBoard")
                .param("enabled", "true");

        setAuthToken(requestBuilder, systemAdmin);

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
//                                        headerWithName("Authorization").description("firebase 토큰")
                        ),
                        requestParameters(
                                parameterWithName("skip").description("건너 뛸 수"),
                                parameterWithName("limit").description("최대 조회 수"),
                                parameterWithName("keyword").description("검색어"),
                                parameterWithName("enabled").description("활성화 여부")
                        ),
                        responseFields(
                                fieldWithPath("total").description("모든 결과의 수"),
                                fieldWithPath("skip").description("건너 뛸 결과 수"),
                                fieldWithPath("limit").description("조회 할 최대 결과 수"),
                                fieldWithPath("boardAdmins").description("게시판 관리자 목록"),
                                fieldWithPath("boardAdmins.[].id").description("게시판 아이디"),
                                fieldWithPath("boardAdmins.[].title").description("게시판 제목"),
                                fieldWithPath("boardAdmins.[].description").description("게시판 설명"),
                                fieldWithPath("boardAdmins.[].enabled").description("게시판 활성화 여부"),
                                fieldWithPath("boardAdmins.[].accounts").description("게시판 관리자 목록"),
                                fieldWithPath("boardAdmins.[].accounts.[].id").description("게시판 관리자 아이디"),
                                fieldWithPath("boardAdmins.[].accounts.[].email").description("게시판 관리자 이메일"),
                                fieldWithPath("boardAdmins.[].accounts.[].createDate").description("게시판 관리자 생성일"),
                                fieldWithPath("boardAdmins.[].accounts.[].lastActivityDate").description("게시판 관리자 최근 활동일"),
                                fieldWithPath("boardAdmins.[].accounts.[].nickname").description("게시판 관리자 닉네임"),
                                fieldWithPath("boardAdmins.[].accounts.[].description").description("게시판 관리자 설명"),
                                fieldWithPath("boardAdmins.[].accounts.[].auth").description("게시판 관리자 권한")
                        )
                ));
    }

    @Test
    @DisplayName("게시판 관리자 삭제")
    @Order(1)
    public void testCase02() throws Exception {
        Account account = getAccount(toDisableBoardAdmin);
        Board board = getBoard(enableBoard);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("accountId", account.getId());
        requestBody.put("boardId", board.getId());

        MockHttpServletRequestBuilder requestBuilder = delete("/v1/auth/board")
                .content(getJsonString(requestBody))
                .contentType(MediaType.APPLICATION_JSON);
        setAuthToken(requestBuilder, systemAdmin);

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
//                                        headerWithName("Authorization").description("firebase 토큰")
                        ),
                        requestFields(
                                fieldWithPath("accountId").description("사용자 아이디"),
                                fieldWithPath("boardId").description("게시판 아이디")
                        )
                ));
    }
}
