package com.example.nestedcommentservice.service;

import com.example.nestedcommentservice.entities.User;
import com.example.nestedcommentservice.error.CustomException;
import com.example.nestedcommentservice.error.ErrorCode;
import com.example.nestedcommentservice.model.user.UserRequestModel;
import com.example.nestedcommentservice.model.user.UserResponseModel;
import com.example.nestedcommentservice.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.example.nestedcommentservice.util.Constants.*;

/**
 * UserService - This is the service class to write business logic for user APIs *
 */

@Service
@Slf4j
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    @Lazy
    private UserActionService userActionService;

    @Autowired
    @Lazy
    private ContentService contentService;

    /**
     * This method is adding user*
     * @param userRequestModel
     * @return
     */
    public UserResponseModel addUser(UserRequestModel userRequestModel) {
        log.info("adding a new user {} ", userRequestModel);
        try {
            User user = new User();
            user.setUserName(userRequestModel.getUserName());
            user.prePersist();
            user = userRepository.save(user);
            return UserResponseModel.builder().userId(user.getId()).createdOn(user.getCreatedOn())
                    .userName(user.getUserName()).build();
        } catch (Exception e) {
            log.error("error while adding a new user {} ", userRequestModel);
            throw new CustomException(ErrorCode.BAD_REQUEST, UNABLE_TO_SAVE_USER);
        }
    }

    /**
     * This method fetch all users*
     * @return
     */
    public List<UserResponseModel> fetchUser() {
        log.info("fetching all users");
        try {
            List<User> userList = userRepository.findAll();
            return userList.stream()
                    .map(user -> new UserResponseModel(user.getId(), user.getUserName(), user.getCreatedOn()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("error while fetching users");
            throw new CustomException(ErrorCode.BAD_REQUEST, UNABLE_TO_GET_USER);
        }
    }

    /**
     * This method delete the user
     * if user is deleted
     *      1. related content also removed 2. related independent user-action removed but
     *      also decrease count of like/dislike for content*
     * @param userId
     * @return
     */
    public Boolean deleteUser(String userId) {
        log.info("deleting a user with userId {} ", userId);
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
            log.error("error while deleting a user with userId {} ", userId);
            throw new CustomException(ErrorCode.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * This method is used to find user based on userId*
     * @param userId
     * @return
     */
    public User findUser(String userId) {
        log.info("find a user with userId {} ", userId);
        User user = userRepository.findById(userId).orElse(null);
        if (Objects.isNull(user)) {
            return null;
        }
        return user;
    }

    /**
     * This method gives userNames for given userIds*
     * @param userIds
     * @return
     */
    public List<String> getUsersByIds(List<String> userIds) {
        log.info("get user names with userIds {} ", userIds);
        List<User> userList = userRepository.findAll();
        return userList.stream().filter(user -> userIds.contains(user.getId())).map(User::getUserName)
                .collect(Collectors.toList());
    }

    /**
     * This method gives userId to username map for all users*
     * @return
     */
    public Map<String, String> getUserMap() {
        List<User> userList = userRepository.findAll();
        return userList.stream().collect(Collectors.toMap(User::getId, User::getUserName));
    }

}
