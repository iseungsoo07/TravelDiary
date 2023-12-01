package com.project.traveldiary.repository;

import com.project.traveldiary.es.DiaryDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface DiarySearchRepository extends ElasticsearchRepository<DiaryDocument, Long> {

    Page<DiaryDocument> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    Page<DiaryDocument> findByWriterContainingIgnoreCase(String content, Pageable pageable);

    Page<DiaryDocument> findByHashtagsIgnoreCase(String hashtags, Pageable pageable);
}
