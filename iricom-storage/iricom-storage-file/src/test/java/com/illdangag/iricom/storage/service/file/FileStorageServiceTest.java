package com.illdangag.iricom.storage.service.file;

import com.illdangag.iricom.server.test.IricomTestSuite;
import com.illdangag.iricom.server.test.data.wrapper.TestAccountInfo;
import com.illdangag.iricom.storage.data.IricomFileInputStream;
import com.illdangag.iricom.storage.data.response.FileMetadataInfo;
import com.illdangag.iricom.storage.file.service.impl.FileStorageServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.io.InputStream;

@Slf4j
@DisplayName("service: FileStorageService")
public class FileStorageServiceTest extends IricomTestSuite {
    private final FileStorageServiceImpl fileStorageServiceImpl;

    private final String IMAGE_FILE_NAME = "spring_boot_icon.png";

    private final String IMAGE_FILE_CONTENT_TYPE = "image/png";

    @Autowired
    public FileStorageServiceTest(ApplicationContext context, FileStorageServiceImpl fileStorageServiceImpl) {
        super(context);
        this.fileStorageServiceImpl = fileStorageServiceImpl;
    }

    @Test
    public void uploadFileTest() {
        // 계정 생성
        TestAccountInfo account = setRandomAccount();
        InputStream sampleImageInputStream = this.getSampleImageInputStream();

        FileMetadataInfo fileMetadataInfo = this.fileStorageServiceImpl.uploadFile(account.getId(), IMAGE_FILE_NAME, IMAGE_FILE_CONTENT_TYPE, sampleImageInputStream);
        String fileName = fileMetadataInfo.getName();

        Assertions.assertNotNull(fileMetadataInfo.getId());
        Assertions.assertNotNull(fileName);
        Assertions.assertFalse(fileName.isEmpty());
    }

    @Test
    public void downloadFileTest() {
        // 계정 생성
        TestAccountInfo account = setRandomAccount();

        InputStream sampleImageInputStream = this.getSampleImageInputStream();

        FileMetadataInfo fileMetadataInfo = this.fileStorageServiceImpl.uploadFile(account.getId(), IMAGE_FILE_NAME, IMAGE_FILE_CONTENT_TYPE, sampleImageInputStream);

        String id = fileMetadataInfo.getId();

        try (IricomFileInputStream inputStream = this.fileStorageServiceImpl.downloadFile(id)) {
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
        // 계정 생성
        TestAccountInfo account = setRandomAccount();
        InputStream sampleImageInputStream = this.getSampleImageInputStream();

        FileMetadataInfo fileMetadataInfo = this.fileStorageServiceImpl.uploadFile(account.getId(), IMAGE_FILE_NAME, IMAGE_FILE_CONTENT_TYPE, sampleImageInputStream);

        String id = fileMetadataInfo.getId();

        FileMetadataInfo deleteFileMetadataInfo = this.fileStorageServiceImpl.deleteFile(account.getId(), id);
        Assertions.assertNotNull(deleteFileMetadataInfo);
    }

    private InputStream getSampleImageInputStream() {
        return FileStorageServiceTest.class.getClassLoader().getResourceAsStream(IMAGE_FILE_NAME);
    }
}
