package com.example.commentservicedemo.service;

import com.example.commentservicedemo.model.useraction.UserActionRequestModel;
import com.example.commentservicedemo.model.useraction.UserActionResponseModel;

public interface UserActionService {

    UserActionResponseModel addUserAction(UserActionRequestModel userActionRequestModel) throws Exception;

}
