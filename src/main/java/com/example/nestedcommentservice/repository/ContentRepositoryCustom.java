package com.example.nestedcommentservice.repository;

import java.util.List;

public interface ContentRepositoryCustom {

    List<Object[]> getChildContentCounts(List<String> parentContentIds);
    List<Object> findContentHierarchy(String contentId, Integer level, Integer page, Integer Size);

}
