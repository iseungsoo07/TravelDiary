package com.project.traveldiary.exception;

import com.project.traveldiary.type.ErrorCode;
import lombok.Getter;

@Getter
public class LockException extends RuntimeException {

    private final ErrorCode errorCode;
    private final String message;

    public LockException(ErrorCode errorCode) {
        this.errorCode = errorCode;
        this.message = errorCode.getMessage();
    }

}
