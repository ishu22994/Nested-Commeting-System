package com.example.commentservicedemo.service;

import com.example.commentservicedemo.enums.ActionEntity;
import com.example.commentservicedemo.model.comment.CommentRequestModel;
import com.example.commentservicedemo.model.comment.CommentResponseModel;

public interface CommentService {

    CommentResponseModel addComment(CommentRequestModel commentRequestModel);

    CommentResponseModel updateComment(CommentRequestModel commentRequestModel);

    Boolean findComment(String actionEntityId);

    void updateUserActionCount(Integer likeCount, Integer disLikeCount, String commentId);

}
