package com.example.commentservicedemo.repository;

import com.example.commentservicedemo.entities.UserAction;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserActionRepository extends MongoRepository<UserAction, String> {

    UserAction findByUserIdAndContentEntityId(String userId, String contentEntityId);

}
