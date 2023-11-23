package com.project.traveldiary.exception;

import com.project.traveldiary.type.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ResponseError {

    private ErrorCode errorCode;
    private String message;
}
