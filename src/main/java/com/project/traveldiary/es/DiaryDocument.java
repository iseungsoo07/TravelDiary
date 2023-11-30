package com.project.traveldiary.es;

import com.project.traveldiary.entity.Diary;
import com.project.traveldiary.entity.User;
import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Mapping;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(indexName = "diaries")
//@Setting(settingPath = "/static/elastic/elastic-settings.json")
@Mapping(mappingPath = "/static/elastic/diary-mappings.json")
public class DiaryDocument {

    @Id
    private Long id;

    private User user;

    private String title;

    private String content;

    private List<String> filePath;

    private List<String> hashtags;

    private long likeCount;

    private long commentCount;

    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;

    public static DiaryDocument from(Diary diary) {
        return DiaryDocument.builder()
            .id(diary.getId())
            .user(diary.getUser())
            .title(diary.getTitle())
            .content(diary.getContent())
            .filePath(diary.getFilePath())
            .hashtags(diary.getHashtags())
            .likeCount(diary.getLikeCount())
            .commentCount(diary.getCommentCount())
            .createdAt(diary.getCreatedAt())
            .modifiedAt(diary.getModifiedAt())
            .build();
    }
}
