package com.project.traveldiary.entity;

import com.project.traveldiary.dto.DiaryUpdateRequest;
import com.vladmihalcea.hibernate.type.json.JsonStringType;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@TypeDef(name = "json", typeClass = JsonStringType.class)
public class Diary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String title;

    private String content;

    private String filePath;

    private String fileName;

    @Type(type = "json")
    @Column(columnDefinition = "longtext")
    private List<String> hashtags;

    @OneToMany(mappedBy = "diary")
    private List<Likes> likes = new ArrayList<>();

    @OneToMany(mappedBy = "diary")
    private List<Comment> comments = new ArrayList<>();

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime modifiedAt;

    public void update(DiaryUpdateRequest diaryUpdateRequest, List<String> filePaths,
        List<String> fileNames) {
        this.title = diaryUpdateRequest.getTitle();
        this.content = diaryUpdateRequest.getContent();
        this.hashtags = diaryUpdateRequest.getHashtags();
        this.filePath = filePaths.toString();
        this.fileName = fileNames.toString();
    }

}
