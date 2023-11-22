package com.project.traveldiary.exception;

import static com.project.traveldiary.type.ErrorCode.INVALID_REQUEST;
import static com.project.traveldiary.type.ErrorCode.NOT_FOUND_TOKEN;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(UserException.class)
    public ResponseError handleUserException(UserException e) {
        log.warn("{} 예외 발생", e.getErrorCode());

        return new ResponseError(e.getErrorCode(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseError handleArgumentNotValidException(MethodArgumentNotValidException e) {
        log.warn("ArgumentNotValidException 예외 발생", e);

        FieldError fieldError = e.getBindingResult().getFieldError();

        if (fieldError != null) {
            return new ResponseError(INVALID_REQUEST, fieldError.getDefaultMessage());
        }

        return new ResponseError(INVALID_REQUEST, INVALID_REQUEST.getMessage());
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseError handleMissingRequestHeaderException(MissingRequestHeaderException e) {
        log.warn("MissingRequestHeaderException 예외 발생", e);

        return new ResponseError(NOT_FOUND_TOKEN, NOT_FOUND_TOKEN.getMessage());
    }

}
