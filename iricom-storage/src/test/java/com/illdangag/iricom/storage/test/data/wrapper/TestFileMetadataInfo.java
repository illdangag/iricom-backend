package com.illdangag.iricom.storage.test.data.wrapper;

import com.illdangag.iricom.server.test.data.wrapper.TestAccountInfo;
import com.illdangag.iricom.storage.data.entity.FileMetadata;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TestFileMetadataInfo {
    private TestAccountInfo account;

    private String path;

    private String hash;

    private Long size;

    public FileMetadata getFileMetadata() {
        return FileMetadata.builder()
                .size(this.size)
                .build();
    }
}
