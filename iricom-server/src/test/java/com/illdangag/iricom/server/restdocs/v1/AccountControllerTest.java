package com.illdangag.iricom.server.restdocs.v1;

import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.restdocs.snippet.IricomFieldsSnippet;
import com.illdangag.iricom.server.test.IricomTestSuite;
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
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("restdoc: 계정")
public class AccountControllerTest extends IricomTestSuite {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    public AccountControllerTest(ApplicationContext context) {
        super(context);
    }

    @Test
    @DisplayName("목록 조회")
    public void ac001() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = get("/v1/accounts/")
                .param("skip", "0")
                .param("limit", "5")
                .param("keyword", "common");
        setAuthToken(requestBuilder, systemAdmin);

        List<FieldDescriptor> fieldDescriptorList = new LinkedList<>();
        fieldDescriptorList.addAll(IricomFieldsSnippet.getSearchList(""));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getAccount("accounts.[]."));

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(200))
                .andDo(print())
                .andDo(document("AC_001",
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
                                parameterWithName("keyword").description("검색어")
                        ),
                        responseFields(fieldDescriptorList.toArray(FieldDescriptor[]::new))
                ));
    }

    @Test
    @DisplayName("정보 조회")
    public void ac002() throws Exception {
        Account account = getAccount(systemAdmin);

        MockHttpServletRequestBuilder requestBuilder = get("/v1/accounts/{id}", account.getId());
        setAuthToken(requestBuilder, systemAdmin);

        List<FieldDescriptor> fieldDescriptorList = new LinkedList<>();
        fieldDescriptorList.addAll(IricomFieldsSnippet.getAccount(""));

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(200))
                .andDo(print())
                .andDo(document("AC_002",
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
    @DisplayName("수정")
    public void ac003() throws Exception {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("nickname", "common00_00");
        requestBody.put("description", "update_description");

        MockHttpServletRequestBuilder requestBuilder = patch("/v1/accounts/")
                .content(getJsonString(requestBody))
                .contentType(MediaType.APPLICATION_JSON);
        setAuthToken(requestBuilder, common00);

        List<FieldDescriptor> fieldDescriptorList = new LinkedList<>();
        fieldDescriptorList.addAll(IricomFieldsSnippet.getAccount(""));

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(200))
                .andDo(print())
                .andDo(document("AC_003",
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
                                fieldWithPath("nickname").description("닉네임"),
                                fieldWithPath("description").description("설명")
                        ),
                        responseFields(fieldDescriptorList.toArray(FieldDescriptor[]::new))
                ));
    }

    @Test
    @DisplayName("수정")
    public void ac004() throws Exception {
        Account account = getAccount(common00);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("nickname", "admin_update");
        requestBody.put("description", "admin_update");

        MockHttpServletRequestBuilder requestBuilder = patch("/v1/accounts/{id}", account.getId())
                .content(getJsonString(requestBody))
                .contentType(MediaType.APPLICATION_JSON);
        setAuthToken(requestBuilder, systemAdmin);

        List<FieldDescriptor> fieldDescriptorList = new LinkedList<>();
        fieldDescriptorList.addAll(IricomFieldsSnippet.getAccount(""));

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(200))
                .andDo(print())
                .andDo(document("AC_004",
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
                        requestFields(
                                fieldWithPath("nickname").description("닉네임"),
                                fieldWithPath("description").description("설명")
                        ),
                        responseFields(fieldDescriptorList.toArray(FieldDescriptor[]::new))
                ));
    }
}
