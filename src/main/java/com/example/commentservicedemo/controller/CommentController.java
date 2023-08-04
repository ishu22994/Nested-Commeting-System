package com.example.commentservicedemo.controller;

import com.example.commentservicedemo.model.comment.CommentRequestModel;
import com.example.commentservicedemo.model.post.PostRequestModel;
import com.example.commentservicedemo.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @RequestMapping(value = "/add", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity addComment(@RequestBody CommentRequestModel commentRequestModel) throws Exception {
        try {
            return new ResponseEntity<>(commentService.addComment(commentRequestModel), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/update", method = RequestMethod.PUT, produces = "application/json")
    public ResponseEntity updateComment(@RequestBody CommentRequestModel commentRequestModel) throws Exception {
        try {
            return new ResponseEntity<>(commentService.updateComment(commentRequestModel), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}
