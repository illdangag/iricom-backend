package com.illdangag.iricom.storage.restdocs.v1;

import com.illdangag.iricom.storage.data.response.FileMetadataInfo;
import com.illdangag.iricom.storage.restdocs.snippet.IricomFieldsSnippetEx;
import com.illdangag.iricom.storage.test.IricomTestSuiteEx;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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
    @DisplayName("파일 업로드 및 다운로드")
    public void fl001_fl002() throws Exception {
        InputStream inputStream = this.getSampleImageInputStream();
        MockMultipartFile multipartFile = new MockMultipartFile("file", IMAGE_FILE_NAME, IMAGE_FILE_CONTENT_TYPE, inputStream);

        MockHttpServletRequestBuilder uploadRequestBuilder = multipart("/v1/file")
                .file(multipartFile);
        setAuthToken(uploadRequestBuilder, common00);

        List<FieldDescriptor> fieldDescriptorList = new LinkedList<>();
        fieldDescriptorList.addAll(IricomFieldsSnippetEx.getFileMetadata(""));

        String responseBody = mockMvc.perform(uploadRequestBuilder)
                .andExpect(status().is(200))
                .andDo(print())
                .andDo(document("FL_001",
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
                        ),
                        responseFields(fieldDescriptorList.toArray(FieldDescriptor[]::new))
                ))
                .andReturn().getResponse().getContentAsString();
        FileMetadataInfo fileMetadataInfo = this.getObject(responseBody, FileMetadataInfo.class);

        String fileId = fileMetadataInfo.getId();
        MockHttpServletRequestBuilder downloadRequestBuilder = get("/v1/file/{id}", fileId);

        mockMvc.perform(downloadRequestBuilder)
                .andExpect(status().is(200))
                .andDo(print())
                .andDo(document("FL_002",
                        preprocessRequest(
                                prettyPrint()
                        ),
                        preprocessResponse(
                                prettyPrint()
                        ),
                        pathParameters(
                                parameterWithName("id").description("아이디")
                        )
                ));
    }
}
