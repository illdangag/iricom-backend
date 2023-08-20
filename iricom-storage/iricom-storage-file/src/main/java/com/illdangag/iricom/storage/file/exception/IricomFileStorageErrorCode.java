package com.illdangag.iricom.storage.file.exception;

import com.illdangag.iricom.server.exception.IricomError;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum IricomFileStorageErrorCode implements IricomError {
    INVALID_STORAGE_PATH("09000000", 500, "Invalid storage path"),
    INVALID_UPLOAD_FILE("09000001", 400, ""),
    FAIL_TO_SAVE_LOCAL_FILE("09000002", 400, ""),
    NOT_EXIST_FILE("09000003", 400, ""),
    INVALID_READ_LOCAL_FILE("09000004", 400, ""),
    ;

    private final String code;
    private final int httpStatusCode;
    private final String message;

    @Override
    public String toString() {
        return "[" + this.code + "](" + this.httpStatusCode + ") " + this.message;
    }
}
