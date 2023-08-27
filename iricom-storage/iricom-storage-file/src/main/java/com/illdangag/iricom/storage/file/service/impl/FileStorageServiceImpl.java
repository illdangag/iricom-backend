package com.illdangag.iricom.storage.file.service.impl;

import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.data.entity.type.AccountAuth;
import com.illdangag.iricom.server.exception.IricomException;
import com.illdangag.iricom.storage.data.IricomFileInputStream;
import com.illdangag.iricom.storage.data.entity.FileMetadata;
import com.illdangag.iricom.storage.data.response.FileMetadataInfo;
import com.illdangag.iricom.storage.file.exception.IricomFileStorageErrorCode;
import com.illdangag.iricom.storage.repository.FileRepository;
import com.illdangag.iricom.storage.service.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class FileStorageServiceImpl implements StorageService {
    private final String STORAGE_PATH;

    private final FileRepository fileRepository;

    @Autowired
    public FileStorageServiceImpl(FileRepository fileRepository, @Value("${storage.path:}") String storagePath) {
        this.fileRepository = fileRepository;
        this.STORAGE_PATH = storagePath;

        if (storagePath == null || storagePath.isEmpty()) {
            throw new IricomException(IricomFileStorageErrorCode.INVALID_STORAGE_PATH);
        }

        File storagePathFile = new File(storagePath);

        if (!storagePathFile.exists()) {
            try {
                Files.createDirectories(storagePathFile.toPath());
            } catch (Exception exception) {
                throw new IricomException(IricomFileStorageErrorCode.INVALID_STORAGE_PATH);
            }
        }

        if (!storagePathFile.isDirectory()) {
            throw new IricomException(IricomFileStorageErrorCode.INVALID_STORAGE_PATH);
        }
    }

    @Override
    public FileMetadataInfo uploadFile(Account account, String fileName, String contentType, InputStream inputStream) {
        int fileSize = 0;

        try {
            fileSize = inputStream.available();
        } catch (Exception exception) {
            throw new IricomException(IricomFileStorageErrorCode.INVALID_UPLOAD_FILE);
        }

        String newFileName = this.createNewFileName(fileName);

        FileMetadata fileMetadata = FileMetadata.builder()
                .account(account)
                .name(newFileName)
                .contentType(contentType)
                .size((long) fileSize)
                .build();

        this.fileRepository.saveFileMetadata(fileMetadata);

        String path = this.getPath(fileMetadata);
        File file = new File(path);

        try {
            FileUtils.copyInputStreamToFile(inputStream, file);
        } catch (Exception exception) {
            throw new IricomException(IricomFileStorageErrorCode.FAIL_TO_SAVE_LOCAL_FILE);
        }

        return new FileMetadataInfo(fileMetadata);
    }

    @Override
    public IricomFileInputStream downloadFile(String id) {
        UUID fileMetadataId = null;

        try {
            fileMetadataId = UUID.fromString(id);
        } catch (Exception exception) {
            throw new IricomException(IricomFileStorageErrorCode.NOT_EXIST_FILE);
        }

        Optional<FileMetadata> fileMetadataOptional = this.fileRepository.getFileMetadata(fileMetadataId);
        FileMetadata fileMetadata = fileMetadataOptional.orElseThrow(() -> new IricomException(IricomFileStorageErrorCode.NOT_EXIST_FILE));

        String path = this.getPath(fileMetadata);

        File file = new File(path);

        if (!file.exists()) {
            throw new IricomException(IricomFileStorageErrorCode.INVALID_READ_LOCAL_FILE);
        }

        FileInputStream fileInputStream;

        try {
            fileInputStream = new FileInputStream(file);
        } catch (Exception exception) {
            throw new IricomException(IricomFileStorageErrorCode.INVALID_READ_LOCAL_FILE);
        }

        return new IricomFileInputStream(fileInputStream, fileMetadata);
    }

    @Override
    public FileMetadataInfo deleteFile(Account account, String id) {
        UUID fileMetadataId = null;

        try {
            fileMetadataId = UUID.fromString(id);
        } catch (Exception exception) {
            throw new IricomException(IricomFileStorageErrorCode.NOT_EXIST_FILE);
        }

        this.fileRepository.getFileMetadata(fileMetadataId);

        Optional<FileMetadata> fileMetadataOptional = this.fileRepository.getFileMetadata(fileMetadataId);
        FileMetadata fileMetadata = fileMetadataOptional.orElseThrow(() -> new IricomException(IricomFileStorageErrorCode.NOT_EXIST_FILE));

        Account fileOwner = fileMetadata.getAccount();
        if (account.getAuth() != AccountAuth.SYSTEM_ADMIN && !account.equals(fileOwner)) {
            // 시스템 관리자도 아니고 파일의 소유자가 아닌 경우 파일 삭제가 불가능
            throw new IricomException(IricomFileStorageErrorCode.INVALID_AUTHORIZATION_TO_DELETE_FILE);
        }

        String filePath = this.getPath(fileMetadata);
        File file = new File(filePath);
        try {
            Files.delete(file.toPath());
        } catch (Exception exception) {
            // 파일 삭제 오류
            log.error("Fail to delete file. id: {}, path: {}", id, filePath, exception);
        }

        fileMetadata.setDeleted(true);
        this.fileRepository.saveFileMetadata(fileMetadata);

        return new FileMetadataInfo(fileMetadata);
    }

    private String getPath(FileMetadata fileMetadata) {
        LocalDateTime createDate = fileMetadata.getCreateDate();
        return String.format("%s/%04d-%02d-%02d/%s", STORAGE_PATH, createDate.getYear(), createDate.getMonthValue(), createDate.getDayOfMonth(), fileMetadata.getId());
    }
}
