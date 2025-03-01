package com.illdangag.iricom.storage.test;

import com.illdangag.iricom.server.test.IricomTestSuite;
import com.illdangag.iricom.server.test.data.wrapper.TestAccountInfo;
import com.illdangag.iricom.storage.controller.v1.StorageControllerTest;
import com.illdangag.iricom.storage.data.response.FileMetadataInfo;
import com.illdangag.iricom.storage.service.StorageService;
import com.illdangag.iricom.storage.test.data.wrapper.TestFileMetadataInfo;
import org.springframework.context.ApplicationContext;

import java.io.InputStream;

public class IricomTestSuiteEx extends IricomTestSuite {
    protected final String IMAGE_FILE_NAME = "spring_boot_icon.png";
    protected final String IMAGE_FILE_CONTENT_TYPE = "image/png";

    private final StorageService storageService;

    public IricomTestSuiteEx(ApplicationContext context) {
        super(context);

        this.storageService = context.getBean(StorageService.class);
    }

    private void setFileMetadata(TestFileMetadataInfo testFileMetadataInfo) {
        TestAccountInfo testAccountInfo = testFileMetadataInfo.getAccount();
        String accountId = testAccountInfo.getId();
        String fileName = testFileMetadataInfo.getName();
        String contentType = testFileMetadataInfo.getContentType();
        InputStream inputStream = testFileMetadataInfo.getInputStream();

        FileMetadataInfo fileMetadataInfo = this.storageService.uploadFile(accountId, fileName, contentType, inputStream);
        testFileMetadataInfo.setId(fileMetadataInfo.getId());
    }

    protected InputStream getSampleImageInputStream() {
        return StorageControllerTest.class.getClassLoader().getResourceAsStream(IMAGE_FILE_NAME);
    }

    protected TestFileMetadataInfo getRandomTestFileMetadataInfo(TestAccountInfo account) {
        TestFileMetadataInfo testFileMetadataInfo = TestFileMetadataInfo.builder()
                .account(account)
                .name(IMAGE_FILE_NAME)
                .contentType(IMAGE_FILE_CONTENT_TYPE)
                .inputStream(this.getSampleImageInputStream())
                .build();
        this.setFileMetadata(testFileMetadataInfo);
        return testFileMetadataInfo;
    }
}
