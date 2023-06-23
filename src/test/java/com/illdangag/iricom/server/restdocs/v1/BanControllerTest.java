package com.illdangag.iricom.server.restdocs.v1;

import com.illdangag.iricom.server.test.IricomTestSuite;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;

@DisplayName("restdoc: 차단")
public class BanControllerTest extends IricomTestSuite {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    public BanControllerTest(ApplicationContext context) {
        super(context);
    }

    @Test
    @DisplayName("게시물 차단")
    public void bp001() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = get("/v1/boards")
                .param("skip", "0")
                .param("limit", "20")
                .param("keyword", "Board");
        setAuthToken(requestBuilder, common00);
    }
}
