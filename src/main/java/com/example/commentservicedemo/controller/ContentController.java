package com.example.commentservicedemo.controller;

import com.example.commentservicedemo.model.content.ContentRequestModel;
import com.example.commentservicedemo.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/content")
public class ContentController {

    @Autowired
    private ContentService contentService;

    @RequestMapping(value = "/add", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity addContent(@RequestBody ContentRequestModel contentRequestModel) throws Exception {
        try {
            return new ResponseEntity<>(contentService.addContent(contentRequestModel), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/update", method = RequestMethod.PUT, produces = "application/json")
    public ResponseEntity updateContent(@RequestBody ContentRequestModel contentRequestModel) throws Exception {
        try {
            return new ResponseEntity<>(contentService.updateContent(contentRequestModel), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public ResponseEntity deleteContent(@RequestParam String contentId) throws Exception {
        try {
            return new ResponseEntity<>(contentService.deleteContent(contentId), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}