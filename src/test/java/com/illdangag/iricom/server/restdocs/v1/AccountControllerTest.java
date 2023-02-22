package com.illdangag.iricom.server.restdocs.v1;

import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.test.IricomTestSuite;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.regex.Pattern;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("계정")
public class AccountControllerTest extends IricomTestSuite {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    public AccountControllerTest(ApplicationContext context) {
        super(context);
    }

    @Test
    @DisplayName("조회")
    @Order(0)
    public void testCase00() throws Exception {
        Account account = getAccount(systemAdmin);

        MockHttpServletRequestBuilder requestBuilder = get("/v1/accounts/" + account.getId());
        setAuthToken(requestBuilder, systemAdmin);

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.email").value(systemAdmin.getEmail()))
                .andExpect(jsonPath("$.auth").value("systemAdmin"))
                .andDo(print())
                .andDo(document("account",
                                preprocessRequest(removeHeaders("Authorization")),
                                requestHeaders(
//                                        headerWithName("Authorization").description("firebase 토큰")
                                ),
                                responseFields(
                                        fieldWithPath("id").description("아이디"),
                                        fieldWithPath("email").description("이메일"),
                                        fieldWithPath("createDate").description("생성일"),
                                        fieldWithPath("lastActivityDate").description("최근 활동일"),
                                        fieldWithPath("nickname").description("닉네임"),
                                        fieldWithPath("description").description("설명"),
                                        fieldWithPath("auth").description("권한")
                                )
                        )
                );
    }
}
