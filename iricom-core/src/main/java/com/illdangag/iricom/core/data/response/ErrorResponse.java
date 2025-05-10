package com.illdangag.iricom.core.data.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    @Builder.Default
    private String code = "99999999";

    @Builder.Default
    private String message = "Unknown server error.";
}
