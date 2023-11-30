package com.project.traveldiary.repository;

import com.project.traveldiary.es.DiaryDocument;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DiarySearchQueryRepository {

    private final ElasticsearchOperations operations;

    public List<DiaryDocument> findByCondition(String searchCond, Pageable pageable) {
        CriteriaQuery query = createConditionCriteriaQuery(searchCond).setPageable(pageable);

        SearchHits<DiaryDocument> search = operations.search(query, DiaryDocument.class);

        return search.stream()
            .map(SearchHit::getContent)
            .collect(Collectors.toList());
    }

    private CriteriaQuery createConditionCriteriaQuery(String searchCond) {
        CriteriaQuery query = new CriteriaQuery(new Criteria());

        if (searchCond == null) {
            return query;
        }

        if("title".equals(searchCond)) {
            query.addCriteria(Criteria.where("title").is(searchCond));
        }

        return query;
    }
}
