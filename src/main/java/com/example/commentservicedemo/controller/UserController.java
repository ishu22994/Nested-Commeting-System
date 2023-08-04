package com.example.commentservicedemo.controller;

import com.example.commentservicedemo.model.user.UserRequestModel;
import com.example.commentservicedemo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/add", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity addUser(@RequestBody UserRequestModel userRequestModel) throws Exception {
        try {
            return new ResponseEntity<>(userService.addUser(userRequestModel), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/fetch", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity fetchUser() throws Exception {
        try {
            return new ResponseEntity<>(userService.fetchUser(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public ResponseEntity deleteUser(@RequestParam String userId) throws Exception {
        try {
            return new ResponseEntity<>(userService.deleteUser(userId), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }



}
