package com.illdangag.iricom.storage.test.restdocs.snippet;

import com.illdangag.iricom.core.restdocs.snippet.IricomFieldsSnippet;
import com.illdangag.iricom.core.test.data.ResponseField;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;

import java.util.Arrays;
import java.util.List;

public class IricomStorageFieldsSnippet extends IricomFieldsSnippet {
    private static final List<ResponseField> fileMetadataFieldList = Arrays.asList(
            ResponseField.builder().path("id").description("id").type(JsonFieldType.STRING).build(),
            ResponseField.builder().path("size").description("크기").type(JsonFieldType.NUMBER).build(),
            ResponseField.builder().path("name").description("이름").type(JsonFieldType.STRING).build(),
            ResponseField.builder().path("contentType").description("형식").type(JsonFieldType.STRING).build()
    );

    public static List<FieldDescriptor> getFileMetadata(String keyPrefix) {
        return getFieldDescriptors(fileMetadataFieldList, keyPrefix);
    }
}
