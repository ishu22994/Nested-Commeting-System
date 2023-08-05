package com.example.nestedcommentservice.controller;

import com.example.nestedcommentservice.enums.Action;
import com.example.nestedcommentservice.model.content.ContentRequestModel;
import com.example.nestedcommentservice.service.ContentService;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/content")
public class ContentController {

    @Autowired
    private ContentService contentService;

    @RequestMapping(value = "/fetch", method = RequestMethod.GET)
    public ResponseEntity getContent(@RequestParam @NonNull String parentContentId,
                                     @RequestParam(defaultValue = "0") Integer page,
                                     @RequestParam(defaultValue = "5") Integer size) throws Exception {
        try {
            return new ResponseEntity<>(contentService.getContent(parentContentId, page, size), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/fetch/hierarchy", method = RequestMethod.GET)
    public ResponseEntity getHierarchyContent(@RequestParam @NonNull String parentContentId, @RequestParam @NonNull Integer level,
                                     @RequestParam Integer page, @RequestParam Integer size) throws Exception {
        try {
            return new ResponseEntity<>(contentService.getHierarchyContent(parentContentId, level, page, size), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

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

    @RequestMapping(value = "/user-action/names", method = RequestMethod.GET)
    public ResponseEntity getUserActionNames(@RequestParam String contentId, @RequestParam Action action) throws Exception {
        try {
            return new ResponseEntity<>(contentService.getUserActionNames(contentId, action), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}
