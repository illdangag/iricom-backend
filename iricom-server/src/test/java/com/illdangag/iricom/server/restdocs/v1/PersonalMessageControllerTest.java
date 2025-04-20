package com.illdangag.iricom.server.restdocs.v1;

import com.illdangag.iricom.server.restdocs.snippet.IricomFieldsSnippet;
import com.illdangag.iricom.server.test.IricomTestSuite;
import com.illdangag.iricom.server.test.data.wrapper.TestAccountInfo;
import com.illdangag.iricom.server.test.data.wrapper.TestPersonalMessageInfo;
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

@DisplayName("restdoc: 개인 쪽지")
public class PersonalMessageControllerTest extends IricomTestSuite {
    @Autowired
    MockMvc mockMvc;

    public PersonalMessageControllerTest(ApplicationContext context) {
        super(context);
    }

    @Test
    @DisplayName("개인 쪽지 전송")
    public void pm001() throws Exception {
        // 계정 생성
        TestAccountInfo sender = setRandomAccount();
        TestAccountInfo receiver = setRandomAccount();

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("title", "Title");
        requestBody.put("message", "Message");
        requestBody.put("receiveAccountId", receiver.getId());

        MockHttpServletRequestBuilder requestBuilder = post("/v1/personal/messages")
                .content(getJsonString(requestBody))
                .contentType(MediaType.APPLICATION_JSON);
        setAuthToken(requestBuilder, sender);

        List<FieldDescriptor> fieldDescriptorList = new LinkedList<>();
        fieldDescriptorList.addAll(IricomFieldsSnippet.getPersonalMessage(""));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getAccount("sendAccount."));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getAccount("receiveAccount."));

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(200))
                .andDo(print())
                .andDo(document("PM_001",
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
                                fieldWithPath("message").description("내용"),
                                fieldWithPath("receiveAccountId").description("수신자")
                        ),
                        responseFields(fieldDescriptorList.toArray(FieldDescriptor[]::new))
                ));
    }

    @Test
    @DisplayName("수신 개인 쪽지 목록 조회")
    public void pm002() throws Exception {
        // 계정 생성
        TestAccountInfo sender = setRandomAccount();
        TestAccountInfo receiver = setRandomAccount();
        // 개인 쪽지 전송
        setRandomPersonalMessage(sender, receiver, 5);

        MockHttpServletRequestBuilder requestBuilder = get("/v1/personal/messages/receive")
                .param("skip", "0")
                .param("limit", "20");
        setAuthToken(requestBuilder, receiver);

        List<FieldDescriptor> fieldDescriptorList = new LinkedList<>();
        fieldDescriptorList.addAll(IricomFieldsSnippet.getSearchList(""));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getPersonalMessage("personalMessages.[]."));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getAccount("personalMessages.[].sendAccount."));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getAccount("personalMessages.[].receiveAccount."));

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(200))
                .andDo(print())
                .andDo(document("PM_002",
                        preprocessRequest(
                                modifyUris().scheme("https").host("api.iricom.com").removePort(),
                                removeHeaders("Authorization"),
                                prettyPrint()
                        ),
                        preprocessResponse(
                                prettyPrint()
                        ),
                        pathParameters(
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
    @DisplayName("수신 개인 쪽지 정보 조회")
    public void pm003() throws Exception {
        // 계정 생성
        TestAccountInfo sender = setRandomAccount();
        TestAccountInfo receiver = setRandomAccount();
        // 개인 쪽지 전송
        TestPersonalMessageInfo personalMessage = setRandomPersonalMessage(sender, receiver);

        MockHttpServletRequestBuilder requestBuilder = get("/v1/personal/messages/receive/{personalMessageId}", personalMessage.getId());
        setAuthToken(requestBuilder, receiver);

        List<FieldDescriptor> fieldDescriptorList = new LinkedList<>();
        fieldDescriptorList.addAll(IricomFieldsSnippet.getPersonalMessage(""));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getAccount("sendAccount."));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getAccount("receiveAccount."));

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(200))
                .andDo(print())
                .andDo(document("PM_003",
                        preprocessRequest(
                                modifyUris().scheme("https").host("api.iricom.com").removePort(),
                                removeHeaders("Authorization"),
                                prettyPrint()
                        ),
                        preprocessResponse(
                                prettyPrint()
                        ),
                        pathParameters(
                                parameterWithName("personalMessageId").description("개인 쪽지 아이디")
                        ),
                        requestHeaders(
//                                headerWithName("Authorization").description("firebase 토큰")
                        ),
                        requestParameters(
                        ),
                        responseFields(fieldDescriptorList.toArray(FieldDescriptor[]::new))
                ));
    }

    @Test
    @DisplayName("발신 개인 쪽지 목록 조회")
    public void pm004() throws Exception {
        // 계정 생성
        TestAccountInfo sender = setRandomAccount();
        TestAccountInfo receiver = setRandomAccount();
        // 개인 쪽지 발신
        setRandomPersonalMessage(sender, receiver, 5);

        MockHttpServletRequestBuilder requestBuilder = get("/v1/personal/messages/send")
                .param("skip", "0")
                .param("limit", "20")
                .param("status", "ALL");
        setAuthToken(requestBuilder, sender);

        List<FieldDescriptor> fieldDescriptorList = new LinkedList<>();
        fieldDescriptorList.addAll(IricomFieldsSnippet.getSearchList(""));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getPersonalMessage("personalMessages.[]."));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getAccount("personalMessages.[].sendAccount."));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getAccount("personalMessages.[].receiveAccount."));

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(200))
                .andDo(print())
                .andDo(document("PM_004",
                        preprocessRequest(
                                modifyUris().scheme("https").host("api.iricom.com").removePort(),
                                removeHeaders("Authorization"),
                                prettyPrint()
                        ),
                        preprocessResponse(
                                prettyPrint()
                        ),
                        pathParameters(
                        ),
                        requestHeaders(
//                                headerWithName("Authorization").description("firebase 토큰")
                        ),
                        requestParameters(
                                parameterWithName("skip").description("건너 뛸 수"),
                                parameterWithName("limit").description("최대 조회 수"),
                                parameterWithName("status").description("개인 쪽지 상태 (ALL, UNREAD)")
                        ),
                        responseFields(fieldDescriptorList.toArray(FieldDescriptor[]::new))
                ));
    }

    @Test
    @DisplayName("발신 개인 쪽지 정보 조회")
    public void pm005() throws Exception {
        // 계정 생성
        TestAccountInfo sender = setRandomAccount();
        TestAccountInfo receiver = setRandomAccount();
        // 개인 쪽지 발신
        TestPersonalMessageInfo personalMessage = setRandomPersonalMessage(sender, receiver);

        MockHttpServletRequestBuilder requestBuilder = get("/v1/personal/messages/send/{personalMessageId}", personalMessage.getId());
        setAuthToken(requestBuilder, sender);

        List<FieldDescriptor> fieldDescriptorList = new LinkedList<>();
        fieldDescriptorList.addAll(IricomFieldsSnippet.getPersonalMessage(""));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getAccount("sendAccount."));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getAccount("receiveAccount."));

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(200))
                .andDo(print())
                .andDo(document("PM_005",
                        preprocessRequest(
                                modifyUris().scheme("https").host("api.iricom.com").removePort(),
                                removeHeaders("Authorization"),
                                prettyPrint()
                        ),
                        preprocessResponse(
                                prettyPrint()
                        ),
                        pathParameters(
                                parameterWithName("personalMessageId").description("개인 쪽지 아이디")
                        ),
                        requestHeaders(
//                                headerWithName("Authorization").description("firebase 토큰")
                        ),
                        requestParameters(
                        ),
                        responseFields(fieldDescriptorList.toArray(FieldDescriptor[]::new))
                ));
    }

    @Test
    @DisplayName("개인 쪽지 삭제")
    public void pm006() throws Exception {
        // 계정 생성
        TestAccountInfo sender = setRandomAccount();
        TestAccountInfo receiver = setRandomAccount();
        // 개인 쪽지 발신
        TestPersonalMessageInfo personalMessage = setRandomPersonalMessage(sender, receiver);

        MockHttpServletRequestBuilder requestBuilder = delete("/v1/personal/messages/{personalMessageId}", personalMessage.getId());
        setAuthToken(requestBuilder, sender);

        List<FieldDescriptor> fieldDescriptorList = new LinkedList<>();
        fieldDescriptorList.addAll(IricomFieldsSnippet.getPersonalMessage(""));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getAccount("sendAccount."));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getAccount("receiveAccount."));

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(200))
                .andDo(print())
                .andDo(document("PM_006",
                        preprocessRequest(
                                modifyUris().scheme("https").host("api.iricom.com").removePort(),
                                removeHeaders("Authorization"),
                                prettyPrint()
                        ),
                        preprocessResponse(
                                prettyPrint()
                        ),
                        pathParameters(
                                parameterWithName("personalMessageId").description("개인 쪽지 아이디")
                        ),
                        requestHeaders(
//                                headerWithName("Authorization").description("firebase 토큰")
                        ),
                        requestParameters(
                        ),
                        responseFields(fieldDescriptorList.toArray(FieldDescriptor[]::new))
                ));
    }
}
