package com.illdangag.iricom.storage.service.s3;

import com.illdangag.iricom.core.test.data.wrapper.TestAccountInfo;
import com.illdangag.iricom.storage.data.IricomFileInputStream;
import com.illdangag.iricom.storage.data.response.FileMetadataInfo;
import com.illdangag.iricom.storage.s3.service.impl.S3StorageServiceImpl;
import com.illdangag.iricom.storage.service.s3.test.IricomTestS3StorageSuite;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.io.InputStream;

@Slf4j
@DisplayName("service: S3StorageService")
public class S3StorageServiceTest extends IricomTestS3StorageSuite {
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
        // 계정 생성
        TestAccountInfo account = setRandomAccount();

        InputStream sampleImageInputStream = this.getSampleImageInputStream();

        FileMetadataInfo fileMetadataInfo = this.s3StorageServiceImpl.uploadFile(account.getId(), IMAGE_FILE_NAME, IMAGE_FILE_CONTENT_TYPE, sampleImageInputStream);

        Assertions.assertNotNull(fileMetadataInfo);
        Assertions.assertNotNull(fileMetadataInfo.getId());
    }

    @Test
    public void downloadFileTest() {
        // 계정 생성
        TestAccountInfo account = setRandomAccount();

        InputStream sampleImageInputStream = this.getSampleImageInputStream();

        FileMetadataInfo fileMetadataInfo = this.s3StorageServiceImpl.uploadFile(account.getId(), IMAGE_FILE_NAME, IMAGE_FILE_CONTENT_TYPE, sampleImageInputStream);

        String fileName = fileMetadataInfo.getName();

        try (IricomFileInputStream inputStream = this.s3StorageServiceImpl.downloadFile(fileName)) {
            String id = fileMetadataInfo.getId();
            Assertions.assertNotNull(id);
            Assertions.assertNotNull(inputStream);
            Assertions.assertNotEquals(0, inputStream.available());
            Assertions.assertNotNull(fileName);
            Assertions.assertFalse(fileName.isEmpty());

            byte[] bytes = inputStream.readAllBytes();
            Assertions.assertNotNull(bytes);
            Assertions.assertNotEquals(0, bytes.length);
        } catch (Exception exception) {
            log.error("error", exception);
        }
    }

    @Test
    public void deleteFileTest() {
        // 계정 생성
        TestAccountInfo account = setRandomAccount();

        InputStream sampleImageInputStream = this.getSampleImageInputStream();

        FileMetadataInfo fileMetadataInfo = this.s3StorageServiceImpl.uploadFile(account.getId(), IMAGE_FILE_NAME, IMAGE_FILE_CONTENT_TYPE, sampleImageInputStream);

        String id = fileMetadataInfo.getId();

        FileMetadataInfo deleteFileMetadataInfo = this.s3StorageServiceImpl.deleteFile(account.getId(), id);
        Assertions.assertNotNull(deleteFileMetadataInfo);
    }

    protected InputStream getSampleImageInputStream() {
        return S3StorageServiceTest.class.getClassLoader().getResourceAsStream(IMAGE_FILE_NAME);
    }
}
