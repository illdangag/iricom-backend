package com.illdangag.iricom.storage.test;

import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.test.IricomTestSuite;
import com.illdangag.iricom.server.test.data.wrapper.TestAccountInfo;
import com.illdangag.iricom.storage.controller.v1.StorageControllerTest;
import com.illdangag.iricom.storage.data.response.FileMetadataInfo;
import com.illdangag.iricom.storage.repository.FileRepository;
import com.illdangag.iricom.storage.service.StorageService;
import com.illdangag.iricom.storage.test.data.wrapper.TestFileMetadataInfo;
import org.springframework.context.ApplicationContext;

import java.io.InputStream;
import java.util.*;

public class IricomTestSuiteEx extends IricomTestSuite {
    protected final String IMAGE_FILE_NAME = "spring_boot_icon.png";
    protected final String IMAGE_FILE_CONTENT_TYPE = "image/png";

    private final StorageService storageService;

    private final FileRepository fileRepository;

    private final List<TestFileMetadataInfo> testFileMetadataInfoList = new ArrayList<>();

    private final Map<TestFileMetadataInfo, FileMetadataInfo> fileMetadataInfoMap = new HashMap<>();

    public IricomTestSuiteEx(ApplicationContext context) {
        super(context);

        this.storageService = context.getBean(StorageService.class);

        this.fileRepository = context.getBean(FileRepository.class);
    }

    @Override
    protected void init() {
        super.init();

        this.setFileMetadata(this.testFileMetadataInfoList);
    }

    protected void addTestFileMetadataInfo(TestFileMetadataInfo ...testFileMetadataInfos) {
        this.addTestFileMetadataInfo(Arrays.asList(testFileMetadataInfos));
    }

    protected void addTestFileMetadataInfo(List<TestFileMetadataInfo> testFileMetadataInfoList) {
        this.testFileMetadataInfoList.addAll(testFileMetadataInfoList);
    }

    protected String getFileMetadataInfo(TestFileMetadataInfo testFileMetadataInfo) {
        return String.valueOf(this.fileMetadataInfoMap.get(testFileMetadataInfo).getId());
    }

    private void setFileMetadata(List<TestFileMetadataInfo> testFileMetadataInfoList) {
        testFileMetadataInfoList.forEach(testFileMetadataInfo -> {
            TestAccountInfo testAccountInfo = testFileMetadataInfo.getAccount();
            Account account = this.getAccount(testAccountInfo);
            String fileName = testFileMetadataInfo.getName();
            String contentType = testFileMetadataInfo.getContentType();
            InputStream inputStream = testFileMetadataInfo.getInputStream();

            FileMetadataInfo fileMetadataInfo = this.storageService.uploadFile(account, fileName, contentType, inputStream);
            this.fileMetadataInfoMap.put(testFileMetadataInfo, fileMetadataInfo);
        });
    }

    protected InputStream getSampleImageInputStream() {
        return StorageControllerTest.class.getClassLoader().getResourceAsStream(IMAGE_FILE_NAME);
    }
}
