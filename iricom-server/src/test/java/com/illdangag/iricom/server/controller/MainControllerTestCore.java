package com.illdangag.iricom.server.controller;

import com.illdangag.iricom.server.IricomTestServiceSuite;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("controller: 서버 정보")
public class MainControllerTestCore extends IricomTestServiceSuite {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    public MainControllerTestCore(ApplicationContext context) {
        super(context);
    }

    @Test
    @DisplayName("기본 서버 정보")
    public void testCase00() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = get("/");
        mockMvc.perform(requestBuilder)
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.branch").exists())
                .andExpect(jsonPath("$.commit").exists())
                .andExpect(jsonPath("$.tags").exists())
                .andExpect(jsonPath("$.version").exists())
                .andDo(print());
    }
}
