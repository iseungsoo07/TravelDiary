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
import org.springframework.data.domain.PageImpl;

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

    public static Page<DiaryResponse> diaryList(Page<Diary> diaries) {
        return new PageImpl<>(diaries.getContent().stream()
            .map(diary -> DiaryResponse.builder()
                .title(diary.getTitle())
                .writer(diary.getUser().getNickname())
                .hashtags(diary.getHashtags())
                .likeCount(diary.getLikeCount())
                .commentCount(diary.getCommentCount())
                .createdAt(diary.getCreatedAt())
                .build()).collect(Collectors.toList()));
    }
}
