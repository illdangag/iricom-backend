package com.illdangag.iricom.storage.service;

import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.storage.data.response.FileMetadataInfo;
import com.illdangag.iricom.storage.test.IricomTestSuiteEx;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.io.InputStream;

@Slf4j
public class StorageServiceTest extends IricomTestSuiteEx {
    private final StorageService storageService;

    private final String IMAGE_FILE_NAME = "spring_boot_icon.png";

    @Autowired
    public StorageServiceTest(ApplicationContext context, StorageService storageService) {
        super(context);
        this.storageService = storageService;
    }

    @Test
    public void uploadFileTest() {
        Account account = getAccount(common00);
        InputStream sampleImageInputStream = this.getSampleImageInputStream();

        FileMetadataInfo fileMetadataInfo = this.storageService.uploadFile(account, IMAGE_FILE_NAME, sampleImageInputStream);

        Assertions.assertNotNull(fileMetadataInfo.getId());
        Assertions.assertEquals(IMAGE_FILE_NAME, fileMetadataInfo.getName());
    }

    @Test
    public void downloadFileTest() {
        Account account = getAccount(common00);
        InputStream sampleImageInputStream = this.getSampleImageInputStream();

        FileMetadataInfo fileMetadataInfo = this.storageService.uploadFile(account, IMAGE_FILE_NAME, sampleImageInputStream);

        String id = fileMetadataInfo.getId();

        try (InputStream inputStream = this.storageService.downloadFile(id)) {
            Assertions.assertNotNull(inputStream);
        } catch (Exception exception) {
            log.error("error", exception);
        }
    }

    private InputStream getSampleImageInputStream() {
        return StorageServiceTest.class.getClassLoader().getResourceAsStream(IMAGE_FILE_NAME);
    }
}
