package com.illdangag.iricom.server.restdocs.v1;

import com.illdangag.iricom.server.restdocs.snippet.IricomFieldsSnippet;
import com.illdangag.iricom.server.test.IricomTestSuite;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

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
public class InformationControllerTest extends IricomTestSuite {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    public InformationControllerTest(ApplicationContext context) {
        super(context);
    }

    @Test
    @Order(0)
    @DisplayName("정보 조회")
    public void testCase00() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = get("/v1/infos");
        setAuthToken(requestBuilder, common00);

        List<FieldDescriptor> fieldDescriptorList = new LinkedList<>();
        fieldDescriptorList.addAll(IricomFieldsSnippet.getAccount(""));

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(200))
                .andDo(print())
                .andDo(document("IF_001",
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
                                responseFields(fieldDescriptorList.toArray(FieldDescriptor[]::new))
                        )
                );
    }

    @Test
    @Order(1)
    @DisplayName("작성한 게시물 조회")
    public void testCase01() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = get("/v1/infos/posts")
                .param("skip", "0")
                .param("limit", "2");
        setAuthToken(requestBuilder, common00);

        List<FieldDescriptor> fieldDescriptorList = new LinkedList<>();
        fieldDescriptorList.addAll(IricomFieldsSnippet.getSearchList(""));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getPost("posts.[].", false));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getAccount("posts.[].account."));

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(200))
                .andDo(print())
                .andDo(document("IF_002",
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
                                        parameterWithName("limit").description("최대 조회 수")
                                ),
                                responseFields(fieldDescriptorList.toArray(FieldDescriptor[]::new))
                        )
                );
    }
}
