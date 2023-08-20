package com.illdangag.iricom.storage.service.file;

import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.test.IricomTestSuite;
import com.illdangag.iricom.storage.data.response.FileMetadataInfo;
import com.illdangag.iricom.storage.file.service.impl.FileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.io.InputStream;

@Slf4j
public class FileStorageServiceTest extends IricomTestSuite {
    private final FileStorageService fileStorageService;

    private final String IMAGE_FILE_NAME = "spring_boot_icon.png";

    @Autowired
    public FileStorageServiceTest(ApplicationContext context, FileStorageService fileStorageService) {
        super(context);
        this.fileStorageService = fileStorageService;
    }

    @Test
    public void uploadFileTest() {
        Account account = getAccount(common00);
        InputStream sampleImageInputStream = this.getSampleImageInputStream();

        FileMetadataInfo fileMetadataInfo = this.fileStorageService.uploadFile(account, IMAGE_FILE_NAME, sampleImageInputStream);

        Assertions.assertNotNull(fileMetadataInfo.getId());
        Assertions.assertEquals(IMAGE_FILE_NAME, fileMetadataInfo.getName());
    }

    @Test
    public void downloadFileTest() {
        Account account = getAccount(common00);
        InputStream sampleImageInputStream = this.getSampleImageInputStream();

        FileMetadataInfo fileMetadataInfo = this.fileStorageService.uploadFile(account, IMAGE_FILE_NAME, sampleImageInputStream);

        String id = fileMetadataInfo.getId();

        try (InputStream inputStream = this.fileStorageService.downloadFile(id)) {
            Assertions.assertNotNull(inputStream);
        } catch (Exception exception) {
            log.error("error", exception);
        }
    }

    private InputStream getSampleImageInputStream() {
        return FileStorageServiceTest.class.getClassLoader().getResourceAsStream(IMAGE_FILE_NAME);
    }
}
