package com.example.nestedcommentservice.repository;

import com.example.nestedcommentservice.entities.Content;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContentRepository extends MongoRepository<Content, String> {

    List<Content> findByParentContentId(String id);

    List<Content> findByUserId(String userId);

}
