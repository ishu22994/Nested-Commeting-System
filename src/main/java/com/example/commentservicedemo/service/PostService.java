package com.example.commentservicedemo.service;

import com.example.commentservicedemo.model.post.PostRequestModel;
import com.example.commentservicedemo.model.post.PostResponseModel;

import java.util.List;

public interface PostService {

    PostResponseModel addPost(PostRequestModel postRequestModel) throws Exception;

    List<PostResponseModel> fetchPosts();

    Boolean deletePost(String postId);

    Boolean findPost(String postId);

    void updateUserActionCount(Integer likeCount, Integer disLikeCount, String postId);

}
