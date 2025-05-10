package com.illdangag.iricom.server.restdocs.v1;

import com.illdangag.iricom.core.restdocs.snippet.IricomFieldsSnippet;
import com.illdangag.iricom.core.test.data.wrapper.TestAccountInfo;
import com.illdangag.iricom.core.test.data.wrapper.TestBoardInfo;
import com.illdangag.iricom.server.IricomTestServiceSuite;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
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
public class BoardControllerTestCore extends IricomTestServiceSuite {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    public BoardControllerTestCore(ApplicationContext context) {
        super(context);
    }

    @Test
    @DisplayName("생성")
    public void bd001() throws Exception {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("title", "new_board");
        requestBody.put("description", "new_board_description");
        requestBody.put("enabled", true);

        MockHttpServletRequestBuilder requestBuilder = post("/v1/boards")
                .content(getJsonString(requestBody))
                .contentType(MediaType.APPLICATION_JSON);
        setAuthToken(requestBuilder, systemAdmin);

        List<FieldDescriptor> fieldDescriptorList = new LinkedList<>();
        fieldDescriptorList.addAll(IricomFieldsSnippet.getBoard(""));

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(200))
                .andDo(print())
                .andDo(document("BD_001",
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
                                fieldWithPath("title").description("제목"),
                                fieldWithPath("description").description("설명"),
                                fieldWithPath("enabled").description("활성화 여부")
                        ),
                        responseFields(fieldDescriptorList.toArray(FieldDescriptor[]::new))
                ));
    }


    @Test
    @DisplayName("목록 조회")
    public void bd002() throws Exception {
        // 계정 생성
        TestAccountInfo account = setRandomAccount();
        // 게시판 생성
        setRandomBoard(3);

        MockHttpServletRequestBuilder requestBuilder = get("/v1/boards")
                .param("skip", "0")
                .param("limit", "5")
                .param("keyword", "Board");
        setAuthToken(requestBuilder, account);

        List<FieldDescriptor> fieldDescriptorList = new LinkedList<>();
        fieldDescriptorList.addAll(IricomFieldsSnippet.getSearchList(""));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getBoard("boards.[]."));

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(200))
                .andDo(print())
                .andDo(document("BD_002",
                        preprocessRequest(
                                modifyUris().scheme("https").host("api.iricom.com").removePort(),
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
//                                headerWithName("Authorization").description("firebase 토큰")
                        ),
                        responseFields(fieldDescriptorList.toArray(FieldDescriptor[]::new))
                ));
    }

    @Test
    @DisplayName("정보 조회")
    public void bd003() throws Exception {
        // 계정 생성
        TestAccountInfo account = setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = setRandomBoard();

        MockHttpServletRequestBuilder requestBuilder = get("/v1/boards/{id}", board.getId());
        setAuthToken(requestBuilder, account);

        List<FieldDescriptor> fieldDescriptorList = new LinkedList<>();
        fieldDescriptorList.addAll(IricomFieldsSnippet.getBoard(""));

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(200))
                .andDo(print())
                .andDo(document("BD_003",
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
    @DisplayName("정보 수정")
    public void testCase03() throws Exception {
        // 게시판 생성
        TestBoardInfo board = setRandomBoard();

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("title", "update_title");
        requestBody.put("description", "update_description");
        requestBody.put("enabled", false);

        MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/{id}", board.getId())
                .content(getJsonString(requestBody))
                .contentType(MediaType.APPLICATION_JSON);
        setAuthToken(requestBuilder, systemAdmin);

        List<FieldDescriptor> fieldDescriptorList = new LinkedList<>();
        fieldDescriptorList.addAll(IricomFieldsSnippet.getBoard(""));

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(200))
                .andDo(print())
                .andDo(document("BD_004",
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
                        requestFields(
                                fieldWithPath("title").description("제목"),
                                fieldWithPath("description").description("설명"),
                                fieldWithPath("enabled").description("활성화 여부")
                        ),
                        responseFields(fieldDescriptorList.toArray(FieldDescriptor[]::new))
                ));
    }
}
