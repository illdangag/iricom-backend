package com.illdangag.iricom.server.exception;

public interface IricomError {
    String getCode();
    int getHttpStatusCode();
    String getMessage();
}
