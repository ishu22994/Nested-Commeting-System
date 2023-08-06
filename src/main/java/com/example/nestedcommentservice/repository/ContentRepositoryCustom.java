package com.example.nestedcommentservice.repository;

import java.util.List;
import java.util.Map;

public interface ContentRepositoryCustom {

    Map<String, Integer> getChildContentCounts(List<String> parentContentIds);
    List<Object> findContentHierarchy(String contentId, Integer level, Integer page, Integer Size);

}
