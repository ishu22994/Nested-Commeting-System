package com.example.nestedcommentservice.service;

import com.example.nestedcommentservice.entities.User;
import com.example.nestedcommentservice.error.CustomException;
import com.example.nestedcommentservice.error.ErrorCode;
import com.example.nestedcommentservice.model.user.UserRequestModel;
import com.example.nestedcommentservice.model.user.UserResponseModel;
import com.example.nestedcommentservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.example.nestedcommentservice.util.Constants.*;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    @Lazy
    private UserActionService userActionService;

    @Autowired
    @Lazy
    private ContentService contentService;

    public UserResponseModel addUser(UserRequestModel userRequestModel) {
        try {
            User user = new User();
            user.setUserName(userRequestModel.getUserName());
            user.prePersist();
            user = userRepository.save(user);
            return UserResponseModel.builder().userId(user.getId()).createdOn(user.getCreatedOn())
                    .userName(user.getUserName()).build();
        } catch (Exception e) {
            throw new CustomException(ErrorCode.BAD_REQUEST, UNABLE_TO_SAVE_USER);
        }
    }

    public List<UserResponseModel> fetchUser() {
        try {
            List<User> userList = userRepository.findAll();
            return userList.stream()
                    .map(user -> new UserResponseModel(user.getId(), user.getUserName(), user.getCreatedOn()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new CustomException(ErrorCode.BAD_REQUEST, UNABLE_TO_GET_USER);
        }
    }

    /*Logic:
    if user is deleted
     1. related content also removed 2. related user-action removed but
     also decrease count of like/dislike */
    public Boolean deleteUser(String userId) {
        try {
            User user = userRepository.findById(userId).orElse(null);
            if (Objects.isNull(user)) {
                throw new CustomException(ErrorCode.NOT_FOUND, UNABLE_TO_FIND_USER);
            }
            contentService.deleteContentForUser(userId);
            userActionService.deleteUserActionsForUser(userId);
            userRepository.delete(user);
            return true;
        } catch (Exception e) {
            throw new CustomException(ErrorCode.BAD_REQUEST, e.getMessage());
        }
    }

    public Boolean findUser(String userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (Objects.isNull(user)) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    public List<String> getUsersByIds(List<String> userIds) {
        List<User> userList = userRepository.findAll();
        return userList.stream().filter(user -> userIds.contains(user.getId())).map(User::getUserName)
                .collect(Collectors.toList());
    }

}