package com.example.commentservicedemo.controller;

import com.example.commentservicedemo.model.post.PostRequestModel;
import com.example.commentservicedemo.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/post")
public class PostController {

    @Autowired
    private PostService postService;

    @RequestMapping(value = "/add", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity addPost(@RequestBody PostRequestModel postRequestModel) throws Exception {
        try {
            return new ResponseEntity<>(postService.addPost(postRequestModel), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/fetch", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity fetchPosts() throws Exception {
        try {
            return new ResponseEntity<>(postService.fetchPosts(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public ResponseEntity deletePost(@RequestParam String postId) throws Exception {
        try {
            return new ResponseEntity<>(postService.deletePost(postId), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}
