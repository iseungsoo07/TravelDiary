package com.project.traveldiary.es;

import com.project.traveldiary.entity.Diary;
import java.util.List;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Slf4j
@Document(indexName = "diaries")
@Setting(settingPath = "/static/elastic/elastic-settings.json")
@Mapping(mappingPath = "/static/elastic/diary-mappings.json")
public class DiaryDocument {

    @Id
    private Long id;

    private String writer;

    private String title;

    private List<String> hashtags;

    private long likeCount;

    private long commentCount;

    public static DiaryDocument from(Diary diary) {
        return DiaryDocument.builder()
            .id(diary.getId())
            .writer(diary.getUser().getNickname())
            .title(diary.getTitle())
            .hashtags(diary.getHashtags())
            .likeCount(diary.getLikeCount())
            .commentCount(diary.getCommentCount())
            .build();
    }
}
