package com.project.traveldiary.exception;

import static com.project.traveldiary.type.ErrorCode.INVALID_REQUEST;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
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

        return new ResponseError(INVALID_REQUEST, INVALID_REQUEST.getMessage());
    }

}