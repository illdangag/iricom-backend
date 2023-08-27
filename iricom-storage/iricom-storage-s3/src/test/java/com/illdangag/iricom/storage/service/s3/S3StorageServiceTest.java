package com.illdangag.iricom.storage.service.s3;

import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.test.IricomTestSuite;
import com.illdangag.iricom.storage.data.IricomFileInputStream;
import com.illdangag.iricom.storage.data.response.FileMetadataInfo;
import com.illdangag.iricom.storage.s3.service.impl.S3StorageServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.io.InputStream;

@Slf4j
@DisplayName("service: S3StorageService")
public class S3StorageServiceTest extends IricomTestSuite {
    private final S3StorageServiceImpl s3StorageServiceImpl;

    private final String IMAGE_FILE_NAME = "spring_boot_icon.png";

    private final String IMAGE_FILE_CONTENT_TYPE = "image/png";

    @Autowired
    public S3StorageServiceTest(ApplicationContext context, S3StorageServiceImpl s3StorageServiceImpl) {
        super(context);
        this.s3StorageServiceImpl = s3StorageServiceImpl;
    }

    @Test
    public void uploadFileTest() {
        Account account = getAccount(common00);
        InputStream sampleImageInputStream = this.getSampleImageInputStream();

        FileMetadataInfo fileMetadataInfo = this.s3StorageServiceImpl.uploadFile(account, IMAGE_FILE_NAME, IMAGE_FILE_CONTENT_TYPE, sampleImageInputStream);

        Assertions.assertNotNull(fileMetadataInfo);
        Assertions.assertNotNull(fileMetadataInfo.getId());
    }

    @Test
    public void downloadFileTest() {
        Account account = getAccount(common00);
        InputStream sampleImageInputStream = this.getSampleImageInputStream();

        FileMetadataInfo fileMetadataInfo = this.s3StorageServiceImpl.uploadFile(account, IMAGE_FILE_NAME, IMAGE_FILE_CONTENT_TYPE, sampleImageInputStream);

        String id = fileMetadataInfo.getId();

        try (IricomFileInputStream inputStream = this.s3StorageServiceImpl.downloadFile(id)) {
            String fileName = inputStream.getFileMetadataInfo().getName();
            Assertions.assertNotNull(inputStream);
            Assertions.assertNotEquals(0, inputStream.available());
            Assertions.assertNotNull(fileName);
            Assertions.assertFalse(fileName.isEmpty());
        } catch (Exception exception) {
            log.error("error", exception);
        }
    }

    @Test
    public void deleteFileTest() {
        Account account = getAccount(common00);
        InputStream sampleImageInputStream = this.getSampleImageInputStream();

        FileMetadataInfo fileMetadataInfo = this.s3StorageServiceImpl.uploadFile(account, IMAGE_FILE_NAME, IMAGE_FILE_CONTENT_TYPE, sampleImageInputStream);

        String id = fileMetadataInfo.getId();

        FileMetadataInfo deleteFileMetadataInfo = this.s3StorageServiceImpl.deleteFile(account, id);
        Assertions.assertNotNull(deleteFileMetadataInfo);
    }

    private InputStream getSampleImageInputStream() {
        return S3StorageServiceTest.class.getClassLoader().getResourceAsStream(IMAGE_FILE_NAME);
    }
}
