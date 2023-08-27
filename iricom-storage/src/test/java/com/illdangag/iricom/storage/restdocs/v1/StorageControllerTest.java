package com.illdangag.iricom.storage.restdocs.v1;

import com.illdangag.iricom.storage.test.IricomTestSuiteEx;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("restdoc: 파일 저장소")
public class StorageControllerTest extends IricomTestSuiteEx {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    public StorageControllerTest(ApplicationContext context) {
        super(context);
    }

    @Test
    @DisplayName("테스트")
    public void test() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = get("/storage");
        mockMvc.perform(requestBuilder)
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.branch").exists())
                .andExpect(jsonPath("$.commit").exists())
                .andExpect(jsonPath("$.tags").exists())
                .andExpect(jsonPath("$.version").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andDo(print())
                .andDo(document("STORAGE_INFO",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("branch").description("git branch"),
                                fieldWithPath("commit").description("git commit"),
                                fieldWithPath("tags").description("git tags"),
                                fieldWithPath("version").description("service version"),
                                fieldWithPath("timestamp").description("timestamp"))
                ));
    }
}
