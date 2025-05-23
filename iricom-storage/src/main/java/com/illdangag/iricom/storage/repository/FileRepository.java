package com.illdangag.iricom.storage.repository;


import com.illdangag.iricom.storage.data.entity.FileMetadata;

import java.util.Optional;
import java.util.UUID;

public interface FileRepository {
    Optional<FileMetadata> getFileMetadata(UUID id);

    Optional<FileMetadata> getFileMetadataByFileName(String fileName);

    void saveFileMetadata(FileMetadata fileMetadata);
}
