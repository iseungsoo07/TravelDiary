package com.project.traveldiary.es;

import com.project.traveldiary.type.SearchType;
import lombok.Data;

@Data
public class SearchCond {

    private String content;
    private SearchType searchType;

}
