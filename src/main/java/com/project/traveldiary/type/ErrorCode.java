package com.project.traveldiary.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // GLOBAL
    INVALID_REQUEST("잘못된 요청입니다."),

    // TOKEN
    EXPIRED_TOKEN("이미 만료된 토큰 정보 입니다."),

    // USER
    ALREADY_USING_ID("이미 사용중인 아이디입니다."),
    ALREADY_USING_NICKNAME("이미 사용중인 닉네임입니다."),
    NOT_FOUND_USER("일치하는 사용자 정보가 없습니다."),
    CAN_UPDATE_OWN_ACCOUNT("본인 정보만 수정할 수 있습니다."),


    ;

    private final String message;
}
