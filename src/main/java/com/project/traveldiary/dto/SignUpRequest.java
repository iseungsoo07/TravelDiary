package com.project.traveldiary.dto;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignUpRequest {

    @NotBlank(message = "아이디는 필수 입력 사항입니다.")
    private String userId;

    @NotBlank(message = "비밀번호는 필수 입력 사항입니다.")
    private String password;

    @NotBlank(message = "닉네임은 필수 입력 사항입니다.")
    private String nickname;
}
