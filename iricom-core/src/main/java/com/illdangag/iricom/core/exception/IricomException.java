package com.illdangag.iricom.core.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.illdangag.iricom.core.data.response.ErrorResponse;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;

public class IricomException extends RuntimeException {
    private final IricomError code;
    private String message;

    public IricomException(IricomError code) {
        super(code.toString());
        this.code = code;
    }

    public IricomException(IricomError code, String message) {
        super(code.toString());
        this.code = code;
        this.message = message;
    }

    public IricomException(IricomError code, Throwable throwable) {
        super(code.toString(), throwable);
        this.code = code;
    }

    public IricomException(IricomError code, String message, Throwable throwable) {
        super(code.toString(), throwable);
        this.code = code;
        this.message = message;
    }

    public int getStatusCode() {
        return this.code.getHttpStatusCode();
    }

    public String getMessage() {
        if (StringUtils.hasText(this.message)) {
            return code.getMessage() + " - " + this.message;
        } else {
            return code.getMessage();
        }
    }

    public String getErrorCode() {
        return this.code.getCode();
    }

    public MediaType getHttpContentType() {
        return MediaType.APPLICATION_JSON;
    }

    public ErrorResponse getErrorResponse() {
        return ErrorResponse.builder()
                .code(code.getCode()).message(this.getMessage()).build();
    }

    @Override
    public String toString() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(getErrorResponse());
        } catch (JsonProcessingException exception) {
            throw new RuntimeException(exception);
        }
    }
}
