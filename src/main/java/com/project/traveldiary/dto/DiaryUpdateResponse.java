package com.project.traveldiary.dto;

import java.util.List;
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
public class DiaryUpdateResponse {
    private String title;
    private String cotent;
    private List<String> hashtags;
    private List<String> filePath;
}
