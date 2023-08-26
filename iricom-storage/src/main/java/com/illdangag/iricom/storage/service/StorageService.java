package com.illdangag.iricom.storage.service;

import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.storage.data.IricomFileInputStream;
import com.illdangag.iricom.storage.data.response.FileMetadataInfo;
import org.apache.commons.io.FilenameUtils;

import java.io.InputStream;
import java.util.UUID;

public interface StorageService {
    FileMetadataInfo uploadFile(Account account, String fileName, String contentType, InputStream inputStream);

    IricomFileInputStream downloadFile(String id);

    default String getFileExtension(String filePathName) {
        return FilenameUtils.getExtension(filePathName);
    }

    default String getFileOriginName(String filePathName) {
        return FilenameUtils.getBaseName(filePathName);
    }

    default String createNewFileName(String originFilePathName) {
        String extension = this.getFileExtension(originFilePathName);
        return UUID.randomUUID().toString() + "." + extension;
    }
}
