package com.illdangag.iricom.storage.file.exception;

import com.illdangag.iricom.server.exception.IricomError;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum IricomFileStorageErrorCode implements IricomError {
    INVALID_STORAGE_PATH("09010000", 500, "Invalid storage path."),
    INVALID_UPLOAD_FILE("09010001", 400, "Invalid request file."), // 업로드 파일 스트림 오류
    FAIL_TO_SAVE_LOCAL_FILE("09010002", 400, "Invalid request file."), // 파일 저장 오류
    NOT_EXIST_FILE("09010003", 404, "Not exist file."), // 저장된 파일 읽기 오류
    INVALID_READ_LOCAL_FILE("09010004", 404, "Not exist file."), // 파일 읽기 오류
    INVALID_AUTHORIZATION_TO_DELETE_FILE("09010005", 401, "Invalid authorization."); // 파일 삭제 권한 없음

    private final String code;
    private final int httpStatusCode;
    private final String message;

    @Override
    public String toString() {
        return "[" + this.code + "](" + this.httpStatusCode + ") " + this.message;
    }
}
