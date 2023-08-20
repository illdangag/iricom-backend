package com.illdangag.iricom.storage.controller.v1;

import com.illdangag.iricom.storage.test.IricomTestSuiteEx;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.io.InputStream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@DisplayName("controller: 파일")
public class StorageControllerTest extends IricomTestSuiteEx {
    private final String IMAGE_FILE_NAME = "spring_boot_icon.png";

    @Autowired
    MockMvc mockMvc;

    @Autowired
    public StorageControllerTest(ApplicationContext context) {
        super(context);
    }

    @Test
    @DisplayName("업로드")
    public void uploadFileTest() throws Exception {
        InputStream inputStream = this.getSampleImageInputStream();
        MockMultipartFile multipartFile = new MockMultipartFile("file", IMAGE_FILE_NAME, "image/png", inputStream);

        MockHttpServletRequestBuilder multipart = multipart("/v1/file")
                .file(multipartFile);
        setAuthToken(multipart, common00);

        mockMvc.perform(multipart)
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.size").isNumber())
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.name").isString())
                .andExpect(jsonPath("$.name").value(IMAGE_FILE_NAME))
                .andDo(print());
    }

    private InputStream getSampleImageInputStream() {
        return StorageControllerTest.class.getClassLoader().getResourceAsStream(IMAGE_FILE_NAME);
    }
}
