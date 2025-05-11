package com.illdangag.iricom.storage.exception;

import com.illdangag.iricom.core.exception.IricomError;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum IricomStorageErrorCode implements IricomError {
    INVALID_REQUEST_FILE_INPUT_STREAM("09000000", 400, ""); // TODO

    private final String code;
    private final int httpStatusCode;
    private final String message;

    @Override
    public String toString() {
        return "[" + this.code + "](" + this.httpStatusCode + ") " + this.message;
    }
}
