package com.project.traveldiary.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class DiaryDetailResponse {

    private String title;
    private String content;
    private List<String> filePath;
    private List<String> hashtags;
    private String writer;
    private long likeCount;
    private long commentCount;
    private LocalDateTime createdAt;
}
