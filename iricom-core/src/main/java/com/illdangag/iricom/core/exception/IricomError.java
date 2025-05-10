package com.illdangag.iricom.core.exception;

public interface IricomError {
    String getCode();

    int getHttpStatusCode();

    String getMessage();
}
