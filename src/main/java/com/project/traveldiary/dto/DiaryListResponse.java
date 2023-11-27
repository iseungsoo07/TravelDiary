package com.project.traveldiary.dto;

import com.project.traveldiary.entity.Diary;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiaryListResponse {

    private String title;
    private String writer;
    private List<String> hashtags;
    private long likeCount;
    private long commentCount;
    private LocalDateTime createdAt;

    public static List<DiaryListResponse> diaryList(Page<Diary> diaries) {
        return diaries.getContent().stream()
            .map(diary -> DiaryListResponse.builder()
                .title(diary.getTitle())
                .writer(diary.getUser().getNickname())
                .hashtags(diary.getHashtags())
                .likeCount(diary.getLikeCount())
                .commentCount(diary.getCommentCount())
                .createdAt(diary.getCreatedAt())
                .build()).collect(Collectors.toList());
    }
}
