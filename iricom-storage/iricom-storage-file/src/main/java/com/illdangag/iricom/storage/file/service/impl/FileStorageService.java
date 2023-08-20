package com.illdangag.iricom.storage.file.service.impl;

import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.exception.IricomException;
import com.illdangag.iricom.storage.data.entity.FileMetadata;
import com.illdangag.iricom.storage.data.response.FileMetadataInfo;
import com.illdangag.iricom.server.util.YamlLoadFactory;
import com.illdangag.iricom.storage.file.exception.IricomFileStorageErrorCode;
import com.illdangag.iricom.storage.repository.FileRepository;
import com.illdangag.iricom.storage.service.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
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
@PropertySource(value = "classpath:storage.yml", factory = YamlLoadFactory.class)
public class FileStorageService implements StorageService {
    private final String STORAGE_PATH;

    private final FileRepository fileRepository;

    @Autowired
    public FileStorageService(@Value("${storage.path:}") String storagePath, FileRepository fileRepository) {
        this.STORAGE_PATH = storagePath;
        this.fileRepository = fileRepository;

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
    public FileMetadataInfo uploadFile(Account account, String fileName, InputStream inputStream) {
        int fileSize = 0;

        try {
            fileSize = inputStream.available();
        } catch (Exception exception) {
            throw new IricomException(IricomFileStorageErrorCode.INVALID_UPLOAD_FILE);
        }

        FileMetadata fileMetadata = FileMetadata.builder()
                .account(account)
                .name(fileName)
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
    public InputStream downloadFile(String id) {
        UUID fileMetadataId = UUID.fromString(id);

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

        return fileInputStream;
    }

    private String getPath(FileMetadata fileMetadata) {
        LocalDateTime createDate = fileMetadata.getCreateDate();
        return String.format("%s/%04d-%02d-%02d/%s", STORAGE_PATH, createDate.getYear(), createDate.getMonthValue(), createDate.getDayOfMonth(), fileMetadata.getId());
    }
}
