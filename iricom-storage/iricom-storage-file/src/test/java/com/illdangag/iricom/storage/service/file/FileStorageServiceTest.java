package com.illdangag.iricom.storage.service.file;

import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.test.IricomTestSuite;
import com.illdangag.iricom.storage.data.response.FileMetadataInfo;
import com.illdangag.iricom.storage.file.service.impl.FileStorageServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.io.InputStream;

@Slf4j
public class FileStorageServiceTest extends IricomTestSuite {
    private final FileStorageServiceImpl fileStorageServiceImpl;

    private final String IMAGE_FILE_NAME = "spring_boot_icon.png";

    @Autowired
    public FileStorageServiceTest(ApplicationContext context, FileStorageServiceImpl fileStorageServiceImpl) {
        super(context);
        this.fileStorageServiceImpl = fileStorageServiceImpl;
    }

    @Test
    public void uploadFileTest() {
        Account account = getAccount(common00);
        InputStream sampleImageInputStream = this.getSampleImageInputStream();

        FileMetadataInfo fileMetadataInfo = this.fileStorageServiceImpl.uploadFile(account, IMAGE_FILE_NAME, sampleImageInputStream);

        Assertions.assertNotNull(fileMetadataInfo);
        Assertions.assertNotNull(fileMetadataInfo.getId());
        Assertions.assertEquals(IMAGE_FILE_NAME, fileMetadataInfo.getName());
    }

    @Test
    public void downloadFileTest() {
        Account account = getAccount(common00);
        InputStream sampleImageInputStream = this.getSampleImageInputStream();

        FileMetadataInfo fileMetadataInfo = this.fileStorageServiceImpl.uploadFile(account, IMAGE_FILE_NAME, sampleImageInputStream);

        String id = fileMetadataInfo.getId();

        try (InputStream inputStream = this.fileStorageServiceImpl.downloadFile(id)) {
            Assertions.assertNotNull(inputStream);
            Assertions.assertNotEquals(0, inputStream.available());
        } catch (Exception exception) {
            log.error("error", exception);
        }
    }

    private InputStream getSampleImageInputStream() {
        return FileStorageServiceTest.class.getClassLoader().getResourceAsStream(IMAGE_FILE_NAME);
    }
}
