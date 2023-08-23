package com.illdangag.iricom.storage.s3.exception;

import com.illdangag.iricom.server.exception.IricomError;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum IricomS3StorageErrorCode implements IricomError {
    INVALID_UPLOAD_FILE("09020000", 400, ""), // TODO
    NOT_EXIST_FILE("09020001", 400, ""),
    ;
    private final String code;
    private final int httpStatusCode;
    private final String message;

    @Override
    public String toString() {
        return "[" + this.code + "](" + this.httpStatusCode + ") " + this.message;
    }
}
