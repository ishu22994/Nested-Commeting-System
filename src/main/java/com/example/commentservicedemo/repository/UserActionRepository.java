package com.example.commentservicedemo.repository;

import com.example.commentservicedemo.entities.UserAction;
import com.example.commentservicedemo.enums.Action;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserActionRepository extends MongoRepository<UserAction, String> {

    UserAction findByUserIdAndContentEntityId(String userId, String contentEntityId);

    List<UserAction> findByContentEntityId(String contentEntityId);

    List<UserAction> findByUserId(String userId);

    List<UserAction> findByContentEntityIdAndAction(String contentId, Action action);

}
