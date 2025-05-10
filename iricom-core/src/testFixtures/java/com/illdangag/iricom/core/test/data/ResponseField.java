package com.illdangag.iricom.core.test.data;

import lombok.Builder;
import lombok.Getter;
import org.springframework.restdocs.payload.JsonFieldType;

@Getter
@Builder
public class ResponseField {
    private String path;
    private String description;
    @Builder.Default
    private boolean isOptional = false;
    private JsonFieldType type;
}
