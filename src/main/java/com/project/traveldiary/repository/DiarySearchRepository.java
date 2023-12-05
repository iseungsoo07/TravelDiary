package com.project.traveldiary.repository;

import com.project.traveldiary.es.DiaryDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface DiarySearchRepository extends ElasticsearchRepository<DiaryDocument, Long> {
}
