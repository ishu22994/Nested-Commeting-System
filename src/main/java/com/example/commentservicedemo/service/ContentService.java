package com.example.commentservicedemo.service;

import com.example.commentservicedemo.model.content.ContentRequestModel;
import com.example.commentservicedemo.model.content.ContentResponseModel;

public interface ContentService {

    ContentResponseModel addContent(ContentRequestModel contentRequestModel);

    ContentResponseModel updateContent(ContentRequestModel contentRequestModel);

    Boolean findContent(String actionEntityId);

    void updateUserActionCount(Integer likeCount, Integer disLikeCount, String commentId);

    Boolean deleteContent(String contentId);

}
