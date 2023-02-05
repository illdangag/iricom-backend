package com.illdangag.iricom.server.controller;

import com.illdangag.iricom.server.data.response.ErrorResponse;
import com.illdangag.iricom.server.exception.IricomErrorCode;
import com.illdangag.iricom.server.exception.IricomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Set;

/**
 * Controller 예외 처리
 */
@Slf4j
@RestControllerAdvice("com.illdangag.iricom.server.controller")
public class ControllerExceptionAdvice {

    /**
     * 예상한 예외
     */
    @ExceptionHandler(IricomException.class)
    public ResponseEntity<ErrorResponse> httpIricomException(IricomException exception) {
        return ResponseEntity.status(exception.getStatusCode())
                .contentType(exception.getHttpContentType())
                .body(exception.getErrorResponse());
    }

    /**
     * 요청에 대한 유효성 검사 실패
     */
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<ErrorResponse> methodArgumentNotValidException(MethodArgumentNotValidException exception) {
        String message = null;
        if (exception.getFieldError() != null && exception.getFieldError().getDefaultMessage() != null) {
            message = exception.getFieldError().getDefaultMessage();
        } else {
            message = IricomErrorCode.INVALID_REQUEST.getMessage();
        }

        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(IricomErrorCode.INVALID_REQUEST.getCode())
                .message(message).build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorResponse);
    }

    /**
     * 요청에 대한 유효성 검사 실패
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> constraintViolationException(ConstraintViolationException exception) {
        String message = null;
        Set<ConstraintViolation<?>> set = exception.getConstraintViolations();
        if (set != null && !set.isEmpty()) {
            ConstraintViolation<?>[] violations = set.toArray(new ConstraintViolation[0]);
            message = violations[0].getMessage();
        } else {
            message = IricomErrorCode.INVALID_REQUEST.getMessage();
        }

        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(IricomErrorCode.INVALID_REQUEST.getCode())
                .message(message).build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorResponse);
    }

    /**
     * 예상하지 못한 예외
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> unknownException(Exception exception) {
        log.error("Unknown Exception", exception);
        ErrorResponse errorResponse = ErrorResponse.builder().build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorResponse);
    }
}
