package com.illdangag.iricom.storage.test.data.wrapper;

import com.illdangag.iricom.server.test.data.wrapper.TestAccountInfo;
import lombok.Builder;
import lombok.Getter;

import java.io.InputStream;

@Getter
@Builder
public class TestFileMetadataInfo {
    private TestAccountInfo account;

    private String name;

    private String contentType;

    private InputStream inputStream;
}
