package com.project.traveldiary.repository;

import com.project.traveldiary.es.DiaryDocument;
import com.project.traveldiary.es.SearchCond;
import com.project.traveldiary.type.SearchType;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DiarySearchQueryRepository {

    private final ElasticsearchOperations operations;

    public Page<DiaryDocument> searchDiariesBySearchCond(SearchCond searchCond, Pageable pageable) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        if (searchCond.getSearchType() == SearchType.TITLE) {
            boolQueryBuilder
                .must(QueryBuilders.matchPhraseQuery("title", searchCond.getContent()));
        }

        if (searchCond.getSearchType() == SearchType.WRITER) {
            boolQueryBuilder
                .filter(QueryBuilders.matchPhraseQuery("writer", searchCond.getContent()));
        }

        if (searchCond.getSearchType() == SearchType.HASHTAGS) {
            boolQueryBuilder
                .filter(QueryBuilders.matchPhraseQuery("hashtags", searchCond.getContent()));
        }

        NativeSearchQuery query = new NativeSearchQueryBuilder()
            .withQuery(boolQueryBuilder)
            .withSorts(SortBuilders.fieldSort("id").order(SortOrder.DESC))
            .build();

        SearchHits<DiaryDocument> searchHits = operations.search(query, DiaryDocument.class);

        List<DiaryDocument> list = searchHits.stream().map(SearchHit::getContent)
            .collect(Collectors.toList());

        return new PageImpl<>(list, pageable, searchHits.getTotalHits());
    }
}
