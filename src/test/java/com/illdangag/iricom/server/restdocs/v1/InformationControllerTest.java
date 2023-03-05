package com.illdangag.iricom.server.restdocs.v1;

import com.illdangag.iricom.server.test.IricomTestSuite;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
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

    @Test
    @Order(1)
    @DisplayName("작성한 게시물 조회")
    public void testCase01() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = get("/v1/infos/posts")
                .param("skip", "0")
                .param("limit", "2");

        setAuthToken(requestBuilder, common00);

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
                                responseFields(
                                        fieldWithPath("total").description("모든 결과의 수"),
                                        fieldWithPath("skip").description("건너 뛸 결과 수"),
                                        fieldWithPath("limit").description("조회 할 최대 결과 수"),
                                        fieldWithPath("posts").description("게시물 목록"),
                                        fieldWithPath("posts.[].id").description("아이디"),
                                        fieldWithPath("posts.[].type").description("게시물의 종류"),
                                        fieldWithPath("posts.[].createDate").description("작성일"),
                                        fieldWithPath("posts.[].updateDate").description("수정일"),
                                        fieldWithPath("posts.[].status").description("상태"),
                                        fieldWithPath("posts.[].title").description("제목"),
                                        fieldWithPath("posts.[].viewCount").description("조회수"),
                                        fieldWithPath("posts.[].upvote").description("좋아요"),
                                        fieldWithPath("posts.[].downvote").description("싫어요"),
                                        fieldWithPath("posts.[].commentCount").description("댓글수"),
                                        fieldWithPath("posts.[].isAllowComment").description("댓글 허용 여부"),
                                        fieldWithPath("posts.[].isPublish").description("발행 여부"),
                                        fieldWithPath("posts.[].hasTemporary").description("임시 저장 여부"),
                                        fieldWithPath("posts.[].boardId").description("게시판 아이디"),
                                        fieldWithPath("posts.[].account").description("작성자"),
                                        fieldWithPath("posts.[].account.id").description("아이디"),
                                        fieldWithPath("posts.[].account.email").description("이메일"),
                                        fieldWithPath("posts.[].account.createDate").description("생성일"),
                                        fieldWithPath("posts.[].account.lastActivityDate").description("최근 활동일"),
                                        fieldWithPath("posts.[].account.nickname").description("닉네임"),
                                        fieldWithPath("posts.[].account.description").description("설명"),
                                        fieldWithPath("posts.[].account.auth").description("권한")
                                )
                        )
                );
    }
}
