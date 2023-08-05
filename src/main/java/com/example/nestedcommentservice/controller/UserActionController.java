package com.example.nestedcommentservice.controller;

import com.example.nestedcommentservice.model.useraction.UserActionRequestModel;
import com.example.nestedcommentservice.service.UserActionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user-action")
public class UserActionController {

    @Autowired
    private UserActionService userActionService;

    @RequestMapping(value = "/add", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity addUserAction(@RequestBody UserActionRequestModel userActionRequestModel) throws Exception {
        try {
            return new ResponseEntity<>(userActionService.addUserAction(userActionRequestModel), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}
