package com.example.nestedcommentservice.repository;

import com.example.nestedcommentservice.entities.Content;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContentRepository extends MongoRepository<Content, String>, ContentRepositoryCustom {

    List<Content> findByParentContentId(String parentContentId);

    List<Content> findByUserId(String userId);

    Page<Content> findByParentContentId(String parentContentId, Pageable pageable);

    List<Content> findByParentContentIdIn(List<String> contentIds);

}
