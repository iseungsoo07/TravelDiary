package com.project.traveldiary.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // GLOBAL
    INVALID_REQUEST("잘못된 요청입니다."),

    // TOKEN
    NEED_LOGIN("로그인 후 이용 가능한 서비스입니다."),
    EXPIRED_TOKEN("이미 만료된 토큰 정보 입니다."),
    INVALID_TOKEN("유효하지 않은 토큰 정보입니다."),

    // USER
    ALREADY_USING_ID("이미 사용중인 아이디입니다."),
    ALREADY_USING_NICKNAME("이미 사용중인 닉네임입니다."),
    NOT_FOUND_USER("일치하는 사용자 정보가 없습니다."),
    CAN_DELETE_OWN_ACCOUNT("본인 계정만 탈퇴할 수 있습니다."),
    MISMATCH_PASSWORD("비밀번호가 일치하지 않습니다."),
    CAN_UPDATE_OWN_ACCOUNT("본인 정보만 수정할 수 있습니다."),
  
    // FOLLOW
    ALREADY_FOLLOWED_USER("이미 팔로우 중인 사용자입니다."),
    NOT_FOUND_FOLLOW("일치하는 팔로우 정보가 없습니다."),

    // Diary
    FAIL_FILE_UPLOAD("파일 업로드에 실패했습니다."),
    ;

    private final String message;
}
