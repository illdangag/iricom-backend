package com.illdangag.iricom.storage.test;

import com.illdangag.iricom.server.test.IricomTestSuite;
import com.illdangag.iricom.storage.controller.v1.StorageControllerTest;
import com.illdangag.iricom.storage.data.entity.FileMetadata;
import com.illdangag.iricom.storage.repository.FileRepository;
import com.illdangag.iricom.storage.test.data.wrapper.TestFileMetadataInfo;
import org.springframework.context.ApplicationContext;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

public class IricomTestSuiteEx extends IricomTestSuite {
    protected final String IMAGE_FILE_NAME = "spring_boot_icon.png";

    protected final String IMAGE_FILE_CONTENT_TYPE = "image/png";

    private final FileRepository fileRepository;

    private final List<TestFileMetadataInfo> testFileMetadataInfoList = new ArrayList<>();

    private final Map<TestFileMetadataInfo, FileMetadata> fileMetadataMap = new HashMap<>();

    public IricomTestSuiteEx(ApplicationContext context) {
        super(context);

        this.fileRepository = context.getBean(FileRepository.class);
    }

    protected void addTestFileMetadataInfo(TestFileMetadataInfo ...testFileMetadataInfos) {
        this.addTestFileMetadataInfo(Arrays.asList(testFileMetadataInfos));
    }

    protected void addTestFileMetadataInfo(List<TestFileMetadataInfo> testFileMetadataInfoList) {
        this.testFileMetadataInfoList.addAll(testFileMetadataInfoList);
    }

    protected String getFileMetadataId(TestFileMetadataInfo testFileMetadataInfo) {
        return String.valueOf(this.fileMetadataMap.get(testFileMetadataInfo).getId());
    }

    @Override
    protected void init() {
        super.init();

        this.setFileMetadata(this.testFileMetadataInfoList);
    }

    private void setFileMetadata(List<TestFileMetadataInfo> testFileMetadataInfoList) {
        List<FileMetadata> fileMetadataList = testFileMetadataInfoList.stream()
                .map(TestFileMetadataInfo::getFileMetadata)
                .collect(Collectors.toList());

        testFileMetadataInfoList.forEach(testFileMetadataInfo -> {
            FileMetadata fileMetadata = testFileMetadataInfo.getFileMetadata();
            this.fileRepository.saveFileMetadata(fileMetadata);
            this.fileMetadataMap.put(testFileMetadataInfo, fileMetadata);
        });
    }

    protected InputStream getSampleImageInputStream() {
        return StorageControllerTest.class.getClassLoader().getResourceAsStream(IMAGE_FILE_NAME);
    }
}
