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
public class DiaryUploadResponse {

    private String title;
    private String content;
    private List<String> fileName;
    private List<String> filePath;
    private List<String> hashtags;
    private String message;

}
