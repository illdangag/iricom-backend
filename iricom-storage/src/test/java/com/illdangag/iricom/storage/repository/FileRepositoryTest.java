package com.illdangag.iricom.storage.repository;

import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.test.data.wrapper.TestAccountInfo;
import com.illdangag.iricom.storage.data.entity.FileMetadata;
import com.illdangag.iricom.storage.test.IricomTestSuiteEx;
import com.illdangag.iricom.storage.test.data.wrapper.TestFileMetadataInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

@DisplayName("repository: FileRepository")
@Slf4j
public class FileRepositoryTest extends IricomTestSuiteEx {
    @Autowired
    private FileRepository fileRepository;

    public FileRepositoryTest(ApplicationContext context) {
        super(context);
    }

    @DisplayName("saveFileMetadata")
    @Test
    public void saveFileMetadata() {
        Account account = getAccount(common00);

        FileMetadata fileMetadata = FileMetadata.builder()
                .account(account)
                .size(0L)
                .build();

        this.fileRepository.saveFileMetadata(fileMetadata);
    }

    @DisplayName("getFileMetadata")
    @Test
    public void getFileMetadata() {
        TestAccountInfo testAccountInfo = common00;

        TestFileMetadataInfo testFileMetadataInfo = TestFileMetadataInfo.builder()
                .account(testAccountInfo).path("/").size(0L).hash("HASH").build();

        this.addTestFileMetadataInfo(testFileMetadataInfo);
        this.init();

        String fileMetadataId = this.getFileMetadataId(testFileMetadataInfo);
        log.info("fileMetadata: {}", fileMetadataId);
    }
}
