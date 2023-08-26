package com.illdangag.iricom.storage.data.response;

import com.illdangag.iricom.storage.data.entity.FileMetadata;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class FileMetadataInfo {
    private String id;

    private Long size;

    private String name;

    private String contentType;

    public FileMetadataInfo(FileMetadata fileMetadata) {
        this.id = fileMetadata.getId().toString();
        this.size = fileMetadata.getSize();
        this.name = fileMetadata.getName();
        this.contentType = fileMetadata.getContentType();
    }
}
