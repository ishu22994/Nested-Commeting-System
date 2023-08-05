package com.example.commentservicedemo.repository;

import com.example.commentservicedemo.entities.Content;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContentRepository extends MongoRepository<Content, String> {



}
