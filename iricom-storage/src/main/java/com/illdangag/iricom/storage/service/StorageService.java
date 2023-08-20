package com.illdangag.iricom.storage.service;

import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.storage.data.response.FileMetadataInfo;

import java.io.InputStream;

public interface StorageService {
    FileMetadataInfo uploadFile(Account account, String fileName, InputStream inputStream);

    InputStream downloadFile(String id);
}
