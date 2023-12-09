package com.project.traveldiary.dto;

import com.project.traveldiary.entity.Diary;
import java.time.LocalDateTime;
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
public class DiaryResponse {

    private String title;
    private String writer;
    private List<String> hashtags;
    private long likeCount;
    private long commentCount;
    private LocalDateTime createdAt;

    public static DiaryResponse of(Diary diary) {
        return DiaryResponse.builder()
            .title(diary.getTitle())
            .writer(diary.getUser().getNickname())
            .hashtags(diary.getHashtags())
            .likeCount(diary.getLikeCount())
            .commentCount(diary.getCommentCount())
            .createdAt(diary.getCreatedAt())
            .build();
    }
}
