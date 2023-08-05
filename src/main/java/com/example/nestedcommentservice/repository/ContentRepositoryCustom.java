package com.example.nestedcommentservice.repository;

import java.util.List;

public interface ContentRepositoryCustom {

    List<Object[]> getChildContentCounts(List<String> parentContentIds);

}
