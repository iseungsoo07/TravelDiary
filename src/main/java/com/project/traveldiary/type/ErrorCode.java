package com.project.traveldiary.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // GLOBAL
    INVALID_REQUEST("잘못된 요청입니다."),

    // USER
    ALREADY_USING_ID("이미 사용중인 아이디입니다."),
    ALREADY_USING_NICKNAME("이미 사용중인 닉네임입니다."),

    ;

    private final String message;
}
