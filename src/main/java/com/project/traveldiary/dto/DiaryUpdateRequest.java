package com.project.traveldiary.dto;

import java.util.List;
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
public class DiaryUpdateRequest {

    @NotBlank(message = "제목은 필수 입력 사항입니다.")
    private String title;
    private String content;
    private List<String> hashtags;
}
