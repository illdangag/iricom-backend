package com.illdangag.iricom.storage.service;

import com.illdangag.iricom.core.test.data.wrapper.TestAccountInfo;
import com.illdangag.iricom.storage.data.IricomFileInputStream;
import com.illdangag.iricom.storage.data.response.FileMetadataInfo;
import com.illdangag.iricom.storage.test.IricomTestStorageSuite;
import com.illdangag.iricom.storage.test.wrapper.TestFileMetadataInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.io.InputStream;

@Slf4j
@DisplayName("service: 파일 저장소")
public class StorageServiceTestStorage extends IricomTestStorageSuite {
    private final StorageService storageService;

    @Autowired
    public StorageServiceTestStorage(ApplicationContext context, StorageService storageService) {
        super(context);
        this.storageService = storageService;
    }

    @Test
    @DisplayName("업로드")
    public void uploadFileTest() {
        // 계정 생성
        TestAccountInfo account = setRandomAccount();
        InputStream sampleImageInputStream = this.getSampleImageInputStream();

        FileMetadataInfo fileMetadataInfo = this.storageService.uploadFile(account.getId(), IMAGE_FILE_NAME, IMAGE_FILE_CONTENT_TYPE, sampleImageInputStream);
        String fileName = fileMetadataInfo.getName();

        Assertions.assertNotNull(fileMetadataInfo.getId());
        Assertions.assertNotNull(fileName);
        Assertions.assertFalse(fileName.isEmpty());
    }

    @Test
    @DisplayName("다운로드")
    public void downloadFileTest() {
        // 계정 생성
        TestAccountInfo account = setRandomAccount();
        // 파일 생성
        TestFileMetadataInfo fileMetadata = getRandomTestFileMetadataInfo(account);

        try (IricomFileInputStream inputStream = this.storageService.downloadFile(fileMetadata.getId())) {
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
    @DisplayName("파일 확장자")
    public void getFileExtensionTest() {
        String filePathName = "/test/file/path/filename.png";
        String fileExtension = this.storageService.getFileExtension(filePathName);

        Assertions.assertEquals("png", fileExtension);
    }

    @Test
    @DisplayName("파일 이름")
    public void getFileOriginNameTest() {
        String filePathName = "/test/file/path/filename.png";
        String fileOriginName = this.storageService.getFileOriginName(filePathName);

        Assertions.assertEquals("filename", fileOriginName);
    }

    @Test
    @DisplayName("업로드 파일 이름 생성")
    public void createNewFileNameTest() {
        String filePathName = "/test/file/path/filename.png";
        String newFileName = this.storageService.createNewFileName(filePathName);

        Assertions.assertTrue(newFileName.endsWith("png"));
    }
}
