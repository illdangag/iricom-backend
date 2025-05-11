package com.illdangag.iricom.storage.test;

import com.illdangag.iricom.core.test.IricomTestCoreSuite;
import com.illdangag.iricom.core.test.data.wrapper.TestAccountInfo;
import com.illdangag.iricom.storage.StorageApplication;
import com.illdangag.iricom.storage.data.response.FileMetadataInfo;
import com.illdangag.iricom.storage.service.StorageService;
import com.illdangag.iricom.storage.test.wrapper.TestFileMetadataInfo;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import java.io.InputStream;

@SpringBootTest(classes = StorageApplication.class)
public class IricomTestStorageSuite extends IricomTestCoreSuite {
    protected final String IMAGE_FILE_NAME = "spring_boot_icon.png";
    protected final String IMAGE_FILE_CONTENT_TYPE = "image/png";

    private final StorageService storageService;

    public IricomTestStorageSuite(ApplicationContext context) {
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
        return IricomTestStorageSuite.class.getClassLoader().getResourceAsStream(IMAGE_FILE_NAME);
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
