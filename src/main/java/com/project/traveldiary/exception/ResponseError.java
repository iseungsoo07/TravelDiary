package com.project.traveldiary.exception;

import com.project.traveldiary.type.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ResponseError {

    private ErrorCode errorCode;
    private String message;
}
