package com.illdangag.iricom.storage.file.exception;

import com.illdangag.iricom.server.exception.IricomError;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum IricomFileStorageErrorCode implements IricomError {
    INVALID_STORAGE_PATH("09010000", 500, "Invalid storage path"),
    INVALID_UPLOAD_FILE("09010001", 400, ""), // TODO
    FAIL_TO_SAVE_LOCAL_FILE("09010002", 400, ""), // TODO
    NOT_EXIST_FILE("09010003", 400, ""), // TODO
    INVALID_READ_LOCAL_FILE("09010004", 400, ""), // TODO
    ;

    private final String code;
    private final int httpStatusCode;
    private final String message;

    @Override
    public String toString() {
        return "[" + this.code + "](" + this.httpStatusCode + ") " + this.message;
    }
}
