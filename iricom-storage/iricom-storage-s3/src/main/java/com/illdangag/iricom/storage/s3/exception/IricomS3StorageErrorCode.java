package com.illdangag.iricom.storage.s3.exception;

import com.illdangag.iricom.server.exception.IricomError;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum IricomS3StorageErrorCode implements IricomError {
    INVALID_UPLOAD_FILE("09020000", 400, "Invalid request file."),
    FAIL_TO_SAVE_OBJECT_STORAGE("", 400, ""),
    NOT_EXIST_FILE("09020001", 404, "Not exist file."),
    INVALID_AUTHORIZATION_TO_DELETE_FILE("09020001", 401, "Invalid authorization.");

    private final String code;
    private final int httpStatusCode;
    private final String message;

    @Override
    public String toString() {
        return "[" + this.code + "](" + this.httpStatusCode + ") " + this.message;
    }
}
