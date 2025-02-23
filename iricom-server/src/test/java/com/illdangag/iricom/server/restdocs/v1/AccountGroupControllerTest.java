package com.illdangag.iricom.server.restdocs.v1;

import com.illdangag.iricom.server.restdocs.snippet.IricomFieldsSnippet;
import com.illdangag.iricom.server.test.IricomTestSuite;
import com.illdangag.iricom.server.test.data.wrapper.TestAccountGroupInfo;
import com.illdangag.iricom.server.test.data.wrapper.TestAccountInfo;
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
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("restdoc: 계정 그룹")
public class AccountGroupControllerTest extends IricomTestSuite {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    public AccountGroupControllerTest(ApplicationContext context) {
        super(context);
    }

    @Test
    @DisplayName("계정 그룹 생성")
    public void ag001() throws Exception {
        // 계정 생성
        TestAccountInfo account00 = this.setRandomAccount();
        TestAccountInfo account01 = this.setRandomAccount();
        // 게시판 생성
        TestBoardInfo board00 = this.setRandomBoard();
        TestBoardInfo board01 = this.setRandomBoard();

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("title", "Account group");
        requestBody.put("description", "account group description");

        List<String> accountIdList = Arrays.asList(account00.getId(), account01.getId());
        requestBody.put("accountIds", accountIdList);

        List<String> boardIdList = Arrays.asList(board00.getId(), board01.getId());
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
                                fieldWithPath("accountIds").description("계정 그룹에 포함 할 계정의 아이디 목록"),
                                fieldWithPath("boardIds").description("계정 그룹에 포함 할 게시판의 아이디 목록")
                        ),
                        responseFields(fieldDescriptorList.toArray(FieldDescriptor[]::new))
                ));
    }

    @Test
    @DisplayName("계정 그룹 목록 조회")
    public void ag002() throws Exception {
        // 계정 생성
        TestAccountInfo account00 = this.setRandomAccount();
        TestAccountInfo account01 = this.setRandomAccount();
        // 게시판 생성
        TestBoardInfo board00 = this.setRandomBoard();
        TestBoardInfo board01 = this.setRandomBoard();
        // 계정 그룹 생성
        setRandomAccountGroup(Arrays.asList(account00, account01), Arrays.asList(board00, board01));

        MockHttpServletRequestBuilder requestBuilder = get("/v1/group/account")
                .param("skip", "0")
                .param("limit", "5");
        setAuthToken(requestBuilder, systemAdmin);

        List<FieldDescriptor> fieldDescriptorList = new LinkedList<>();
        fieldDescriptorList.addAll(IricomFieldsSnippet.getSearchList(""));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getAccountGroup("accountGroups.[]."));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getBoard("accountGroups.[].boards.[]."));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getAccount("accountGroups.[].accounts.[]."));

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(200))
                .andDo(print())
                .andDo(document("AG_002",
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
                        responseFields(
                                fieldDescriptorList.toArray(FieldDescriptor[]::new)
                        )
                ));
    }

    @Test
    @DisplayName("계정 그룹 정보 조회")
    public void ag003() throws Exception {
        // 계정 생성
        TestAccountInfo account00 = this.setRandomAccount();
        TestAccountInfo account01 = this.setRandomAccount();
        // 게시판 생성
        TestBoardInfo board00 = this.setRandomBoard();
        TestBoardInfo board01 = this.setRandomBoard();
        // 계정 그룹 생성
        TestAccountGroupInfo accountGroup = setRandomAccountGroup(Arrays.asList(account00, account01), Arrays.asList(board00, board01));

        MockHttpServletRequestBuilder requestBuilder = get("/v1/group/account/{id}", accountGroup.getId());
        setAuthToken(requestBuilder, systemAdmin);

        List<FieldDescriptor> fieldDescriptorList = new LinkedList<>();
        fieldDescriptorList.addAll(IricomFieldsSnippet.getAccountGroup(""));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getBoard("boards.[]."));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getAccount("accounts.[]."));

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(200))
                .andDo(print())
                .andDo(document("AG_003",
                        preprocessRequest(
                                modifyUris().scheme("https").host("api.iricom.com").removePort(),
                                removeHeaders("Authorization"),
                                prettyPrint()
                        ),
                        preprocessResponse(
                                prettyPrint()
                        ),
                        requestHeaders(
//                              headerWithName("Authorization").description("firebase 토큰")
                        ),
                        pathParameters(
                                parameterWithName("id").description("아이디")
                        ),
                        responseFields(
                                fieldDescriptorList.toArray(FieldDescriptor[]::new)
                        )
                ));
    }


    @Test
    @DisplayName("계정 그룹 정보 수정")
    public void ag004() throws Exception {
        // 계정 생성
        TestAccountInfo account00 = this.setRandomAccount();
        TestAccountInfo account01 = this.setRandomAccount();
        // 게시판 생성
        TestBoardInfo board00 = this.setRandomBoard();
        TestBoardInfo board01 = this.setRandomBoard();
        // 계정 그룹 생성
        TestAccountGroupInfo accountGroup = setRandomAccountGroup(Collections.singletonList(account00), Collections.singletonList(board00));

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("title", "Update group title");
        requestBody.put("description", "account group description");

        List<String> accountIdList = Collections.singletonList(account01.getId());
        requestBody.put("accountIds", accountIdList);

        List<String> boardIdList = Collections.singletonList(board01.getId());
        requestBody.put("boardIds", boardIdList);

        MockHttpServletRequestBuilder requestBuilder = patch("/v1/group/account/{id}", accountGroup.getId())
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
                .andDo(document("AG_004",
                        preprocessRequest(
                                modifyUris().scheme("https").host("api.iricom.com").removePort(),
                                removeHeaders("Authorization"),
                                prettyPrint()
                        ),
                        preprocessResponse(
                                prettyPrint()
                        ),
                        requestHeaders(
//                              headerWithName("Authorization").description("firebase 토큰")
                        ),
                        pathParameters(
                                parameterWithName("id").description("아이디")
                        ),
                        requestFields(
                                fieldWithPath("title").description("제목"),
                                fieldWithPath("description").description("설명"),
                                fieldWithPath("accountIds").description("계정 그룹에 포함 할 계정의 아이디 목록"),
                                fieldWithPath("boardIds").description("계정 그룹에 포함 할 게시판의 아이디 목록")
                        ),
                        responseFields(
                                fieldDescriptorList.toArray(FieldDescriptor[]::new)
                        ))
                );
    }

    @Test
    @DisplayName("계정 그룹 정보 삭제")
    public void ag005() throws Exception {
        // 계정 생성
        TestAccountInfo account00 = this.setRandomAccount();
        TestAccountInfo account01 = this.setRandomAccount();
        // 게시판 생성
        TestBoardInfo board00 = this.setRandomBoard();
        TestBoardInfo board01 = this.setRandomBoard();
        // 계정 그룹 생성
        TestAccountGroupInfo accountGroup = setRandomAccountGroup(Arrays.asList(account00, account01), Arrays.asList(board00, board01));

        MockHttpServletRequestBuilder requestBuilder = delete("/v1/group/account/{id}", accountGroup.getId());
        setAuthToken(requestBuilder, systemAdmin);

        List<FieldDescriptor> fieldDescriptorList = new LinkedList<>();
        fieldDescriptorList.addAll(IricomFieldsSnippet.getAccountGroup(""));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getBoard("boards.[]."));
        fieldDescriptorList.addAll(IricomFieldsSnippet.getAccount("accounts.[]."));

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(200))
                .andDo(print())
                .andDo(document("AG_005",
                        preprocessRequest(
                                modifyUris().scheme("https").host("api.iricom.com").removePort(),
                                removeHeaders("Authorization"),
                                prettyPrint()
                        ),
                        preprocessResponse(
                                prettyPrint()
                        ),
                        requestHeaders(
//                              headerWithName("Authorization").description("firebase 토큰")
                        ),
                        pathParameters(
                                parameterWithName("id").description("아이디")
                        ),
                        responseFields(
                                fieldDescriptorList.toArray(FieldDescriptor[]::new)
                        ))
                );
    }
}
