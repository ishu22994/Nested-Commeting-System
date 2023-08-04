package com.example.commentservicedemo.service;

import com.example.commentservicedemo.model.user.UserRequestModel;
import com.example.commentservicedemo.model.user.UserResponseModel;

import java.util.List;

public interface UserService {

    UserResponseModel addUser(UserRequestModel userRequestModel);

    List<UserResponseModel> fetchUser();

    Boolean deleteUser(String userId);

    Boolean findUser(String userId);

}
