package com.illdangag.iricom.server.restdocs.v1;

import com.illdangag.iricom.server.controller.v1.AccountGroupController;
import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.data.entity.AccountAuth;
import com.illdangag.iricom.server.data.entity.Board;
import com.illdangag.iricom.server.restdocs.snippet.IricomFieldsSnippet;
import com.illdangag.iricom.server.test.IricomTestSuite;
import com.illdangag.iricom.server.test.data.TestBoardInfo;
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
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("restdoc: 계정 그룹")
public class AccountGroupControllerTest extends IricomTestSuite {
    @Autowired
    MockMvc mockMvc;

    private TestBoardInfo testBoardInfo00 = TestBoardInfo.builder()
            .title("testBoardInfo00").isEnabled(true).adminList(Collections.singletonList(allBoardAdmin)).build();


    @Autowired
    public AccountGroupControllerTest(ApplicationContext context) {
        super(context);

        List<TestBoardInfo> testBoardInfoList = Arrays.asList(testBoardInfo00);

        super.setBoard(testBoardInfoList);
    }

    @Test
    @DisplayName("계정 그룹 생성")
    public void ag001() throws Exception {
        Account account = getAccount(common00);
        Board board = getBoard(testBoardInfo00);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("title", "Account group");
        requestBody.put("description", "account group description");

        List<String> accountIdList = Arrays.asList(String.valueOf(account.getId()));
        requestBody.put("accountIds", accountIdList);

        List<String> boardIdList = Arrays.asList(String.valueOf(board.getId()));
        requestBody.put("boardIds", boardIdList);

        MockHttpServletRequestBuilder requestBuilder = post("/v1/group/account")
                .content(getJsonString(requestBody))
                .contentType(MediaType.APPLICATION_JSON);
        setAuthToken(requestBuilder, systemAdmin);

        List<FieldDescriptor> fieldDescriptorList = new LinkedList<>();
        fieldDescriptorList.addAll(IricomFieldsSnippet.getAccountGroup(""));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getBoard("boards.[]."));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getAccount("accounts.[]."));

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(200))
                .andDo(print())
                .andDo(document("AG_001",
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
                                        fieldWithPath("accountIds").description("계정 그룹에 포함 할 계정의 아이디 목록"),
                                        fieldWithPath("boardIds").description("계정 그룹에 포함 할 게시판의 아이디 목록")
                                ),
                        responseFields(fieldDescriptorList.toArray(FieldDescriptor[]::new))
                        )
                );
    }
}
