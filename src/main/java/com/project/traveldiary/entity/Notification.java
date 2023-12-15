package com.project.traveldiary.entity;

import com.project.traveldiary.type.AlarmType;
import com.vladmihalcea.hibernate.type.json.JsonStringType;
import java.time.LocalDateTime;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@TypeDef(name = "json", typeClass = JsonStringType.class)
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    private User receiver;

    @Enumerated(EnumType.STRING)
    private AlarmType alarmType;

    @Type(type = "json")
    @Column(columnDefinition = "longtext")
    private Map<String, String> params;

    private String path;

    @CreatedDate
    private LocalDateTime createdAt;

    private LocalDateTime checkedAt;
}
