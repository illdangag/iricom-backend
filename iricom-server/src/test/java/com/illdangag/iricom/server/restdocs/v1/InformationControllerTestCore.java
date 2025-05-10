package com.illdangag.iricom.server.restdocs.v1;

import com.illdangag.iricom.core.restdocs.snippet.IricomFieldsSnippet;
import com.illdangag.iricom.core.test.data.wrapper.TestAccountInfo;
import com.illdangag.iricom.core.test.data.wrapper.TestBoardInfo;
import com.illdangag.iricom.server.IricomTestServiceSuite;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("restdoc: 내정보 조회")
public class InformationControllerTestCore extends IricomTestServiceSuite {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    public InformationControllerTestCore(ApplicationContext context) {
        super(context);
    }

    @Test
    @DisplayName("정보 조회")
    public void if001() throws Exception {
        // 계정 생성
        TestAccountInfo account = setRandomAccount();

        MockHttpServletRequestBuilder requestBuilder = get("/v1/infos");
        setAuthToken(requestBuilder, account);

        List<FieldDescriptor> fieldDescriptorList = new LinkedList<>();
        fieldDescriptorList.addAll(IricomFieldsSnippet.getAccount(""));

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(200))
                .andDo(print())
                .andDo(document("IF_001",
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
                        responseFields(fieldDescriptorList.toArray(FieldDescriptor[]::new))
                ));
    }

    @Test
    @DisplayName("작성한 게시물 조회")
    public void if002() throws Exception {
        // 계정 생성
        TestAccountInfo account = setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = setRandomBoard();
        // 게시물 생성
        setRandomPost(board, account, 9);

        MockHttpServletRequestBuilder requestBuilder = get("/v1/infos/posts")
                .param("skip", "0")
                .param("limit", "2");
        setAuthToken(requestBuilder, account);

        List<FieldDescriptor> fieldDescriptorList = new LinkedList<>();
        fieldDescriptorList.addAll(IricomFieldsSnippet.getSearchList(""));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getPost("posts.[]."));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getAccount("posts.[].account."));

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(200))
                .andDo(print())
                .andDo(document("IF_002",
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
                                parameterWithName("limit").description("최대 조회 수")
                        ),
                        responseFields(fieldDescriptorList.toArray(FieldDescriptor[]::new))
                ));
    }

    @Test
    @DisplayName("내 계정이 관리자로 등록된 게시판 목록 조회")
    public void if003() throws Exception {
        // 계정 생성
        TestAccountInfo account = setRandomAccount();
        // 게시판 생성
        setRandomBoard(Collections.singletonList(account));

        MockHttpServletRequestBuilder requestBuilder = get("/v1/infos/admin/boards")
                .param("skip", "0")
                .param("limit", "20");
        setAuthToken(requestBuilder, account);

        List<FieldDescriptor> fieldDescriptorList = new LinkedList<>();
        fieldDescriptorList.addAll(IricomFieldsSnippet.getSearchList(""));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getBoard("boards.[]."));

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(200))
                .andDo(print())
                .andDo(document("IF_003",
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
                                parameterWithName("limit").description("최대 조회 수")
                        ),
                        responseFields(fieldDescriptorList.toArray(FieldDescriptor[]::new))
                ));
    }
}
