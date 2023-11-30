package com.project.traveldiary.repository;

import com.project.traveldiary.es.DiaryDocument;
import java.util.List;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface DiarySearchRepository extends ElasticsearchRepository<DiaryDocument, Long> {

    List<DiaryDocument> findByTitleContainingIgnoreCase(String title);

}
