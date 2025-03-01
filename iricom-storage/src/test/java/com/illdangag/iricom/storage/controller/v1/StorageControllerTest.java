package com.illdangag.iricom.storage.controller.v1;

import com.illdangag.iricom.server.test.data.wrapper.TestAccountInfo;
import com.illdangag.iricom.storage.data.response.FileMetadataInfo;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@DisplayName("controller: 파일 저장소")
public class StorageControllerTest extends IricomTestSuiteEx {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    public StorageControllerTest(ApplicationContext context) {
        super(context);
    }

    @Test
    @DisplayName("업로드 다운로드")
    public void uploadDownloadFileTest() throws Exception {
        // 계정 생성
        TestAccountInfo account = setRandomAccount();

        InputStream inputStream = this.getSampleImageInputStream();
        MockMultipartFile multipartFile = new MockMultipartFile("file", IMAGE_FILE_NAME, IMAGE_FILE_CONTENT_TYPE, inputStream);

        MockHttpServletRequestBuilder uploadRequestBuilder = multipart("/v1/file")
                .file(multipartFile);
        setAuthToken(uploadRequestBuilder, account);

        String responseBody = mockMvc.perform(uploadRequestBuilder)
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.size").isNumber())
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.name").isString())
                .andDo(print())
                .andReturn().getResponse().getContentAsString();

        FileMetadataInfo fileMetadataInfo = this.getObject(responseBody, FileMetadataInfo.class);

        String fileId = fileMetadataInfo.getId();
        MockHttpServletRequestBuilder downloadRequestBuilder = get("/v1/file/{fileId}", fileId);

        mockMvc.perform(downloadRequestBuilder)
                .andExpect(status().is(200))
                .andExpect(header().exists("Content-Disposition"))
                .andDo(print());
    }
}
