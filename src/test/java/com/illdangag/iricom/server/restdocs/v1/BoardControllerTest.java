package com.illdangag.iricom.server.restdocs.v1;

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
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("restdoc: 게시판")
public class BoardControllerTest extends IricomTestSuite {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    public BoardControllerTest(ApplicationContext context) {
        super(context);
    }

    @Test
    @DisplayName("생성")
    @Order(0)
    public void testCase00() throws Exception {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("title", "new_board");
        requestBody.put("description", "new_board_description");
        requestBody.put("enabled", true);

        MockHttpServletRequestBuilder requestBuilder = post("/v1/boards")
                .content(getJsonString(requestBody))
                .contentType(MediaType.APPLICATION_JSON);
        setAuthToken(requestBuilder, systemAdmin);

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(200))
                .andDo(print())
                .andDo(document("BD_001",
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
                                        fieldWithPath("title").description("제목"),
                                        fieldWithPath("description").description("설명"),
                                        fieldWithPath("enabled").description("활성화 여부")
                                ),
                                responseFields(
                                        fieldWithPath("id").description("아이디"),
                                        fieldWithPath("title").description("제목"),
                                        fieldWithPath("description").description("설명"),
                                        fieldWithPath("enabled").description("활성화 여부")
                                )
                        )
                );
    }

    @Test
    @DisplayName("목록 조회")
    @Order(1)
    public void testCase01() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = get("/v1/boards")
                .param("skip", "0")
                .param("limit", "20")
                .param("keyword", "Board");
        setAuthToken(requestBuilder, common00);

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(200))
                .andDo(print())
                .andDo(document("BD_002",
                                preprocessRequest(
                                        removeHeaders("Authorization"),
                                        prettyPrint()
                                ),
                                preprocessResponse(
                                        prettyPrint()
                                ),
                                requestParameters(
                                        parameterWithName("skip").description("건너 뛸 수"),
                                        parameterWithName("limit").description("최대 조회 수"),
                                        parameterWithName("keyword").description("검색어")
                                ),
                                requestHeaders(
//                                        headerWithName("Authorization").description("firebase 토큰")
                                ),
                                responseFields(
                                        fieldWithPath("total").description("모든 결과의 수"),
                                        fieldWithPath("skip").description("건너 뛸 결과 수"),
                                        fieldWithPath("limit").description("조회 할 최대 결과 수"),
                                        fieldWithPath("boards").description("게시판 목록"),
                                        fieldWithPath("boards.[].id").description("아이디"),
                                        fieldWithPath("boards.[].title").description("제목"),
                                        fieldWithPath("boards.[].description").description("설명"),
                                        fieldWithPath("boards.[].enabled").description("활성화 여부")
                                )
                        )
                );
    }

    @Test
    @DisplayName("정보 조회")
    @Order(2)
    public void testCase02() throws Exception {
        Board board = getBoard(enableBoard);

        MockHttpServletRequestBuilder requestBuilder = get("/v1/boards/{id}", board.getId());
        setAuthToken(requestBuilder, common00);

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(200))
                .andDo(print())
                .andDo(document("BD_003",
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
                                pathParameters(
                                        parameterWithName("id").description("아이디")
                                ),
                                responseFields(
                                        fieldWithPath("id").description("아이디"),
                                        fieldWithPath("title").description("제목"),
                                        fieldWithPath("description").description("설명"),
                                        fieldWithPath("enabled").description("활성화 여부")
                                )
                        )
                );
    }

    @Test
    @DisplayName("정보 수정")
    @Order(3)
    public void testCase03() throws Exception {
        Board board = getBoard(updateBoard);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("title", "update_title");
        requestBody.put("description", "update_description");
        requestBody.put("enabled", false);

        MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/{id}", board.getId())
                .content(getJsonString(requestBody))
                .contentType(MediaType.APPLICATION_JSON);
        setAuthToken(requestBuilder, systemAdmin);

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(200))
                .andDo(print())
                .andDo(document("BD_004",
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
                                        fieldWithPath("title").description("제목"),
                                        fieldWithPath("description").description("설명"),
                                        fieldWithPath("enabled").description("활성화 여부")
                                ),
                                pathParameters(
                                        parameterWithName("id").description("아이디")
                                ),
                                responseFields(
                                        fieldWithPath("id").description("아이디"),
                                        fieldWithPath("title").description("제목"),
                                        fieldWithPath("description").description("설명"),
                                        fieldWithPath("enabled").description("활성화 여부")
                                )
                        )
                );
    }
}
