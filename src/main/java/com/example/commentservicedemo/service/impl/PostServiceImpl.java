package com.example.commentservicedemo.service.impl;

import com.example.commentservicedemo.entities.Post;
import com.example.commentservicedemo.error.CustomException;
import com.example.commentservicedemo.error.ErrorCode;
import com.example.commentservicedemo.model.post.PostRequestModel;
import com.example.commentservicedemo.model.post.PostResponseModel;
import com.example.commentservicedemo.repository.PostRepository;
import com.example.commentservicedemo.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.example.commentservicedemo.util.Constants.*;

@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private PostRepository postRepository;

    @Override
    public PostResponseModel addPost(PostRequestModel postRequestModel) throws Exception {
        try {
            Post post = new Post();
            post.setPostText(postRequestModel.getPostText());
            post.prePersist();
            post = postRepository.save(post);
            return PostResponseModel.builder().postId(post.getId()).createdOn(post.getCreatedOn())
                    .postText(post.getPostText()).build();
        } catch (Exception e) {
            throw new CustomException(ErrorCode.BAD_REQUEST, UNABLE_TO_SAVE_POST);
        }
    }

    @Override
    public List<PostResponseModel> fetchPosts() {
        try {
            List<Post> postList = postRepository.findAll();
            return postList.stream()
                    .map(post -> new PostResponseModel(post.getId(), post.getPostText(), post.getCreatedOn()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new CustomException(ErrorCode.BAD_REQUEST, UNABLE_TO_GET_POST);
        }
    }

    //ishit think what needs to do with comments ... if post has deleted
    @Override
    public Boolean deletePost(String postId) {
        try {
            Post post = postRepository.findById(postId).orElse(null);
            if (Objects.isNull(post)) {
                throw new CustomException(ErrorCode.BAD_REQUEST, UNABLE_TO_FIND_POST);
            }
            postRepository.delete(post);
            return true;
        } catch (Exception e) {
            throw new CustomException(ErrorCode.NOT_FOUND, UNABLE_TO_DELETE_POST);
        }
    }

    @Override
    public Boolean findPost(String postId) {
        Post post = postRepository.findById(postId).orElse(null);
        if (Objects.isNull(post)) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    @Override
    public void updateUserActionCount(Integer likeCount, Integer disLikeCount, String postId) {

    }

}
