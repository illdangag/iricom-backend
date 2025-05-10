package com.illdangag.iricom.storage.test.wrapper;

import com.illdangag.iricom.core.test.data.wrapper.TestAccountInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.InputStream;

@Getter
@Builder
public class TestFileMetadataInfo {
    @Setter
    private String id;

    private TestAccountInfo account;

    private String name;

    private String contentType;

    private InputStream inputStream;
}
