package com.example.nestedcommentservice.service;

import com.example.nestedcommentservice.entities.User;
import com.example.nestedcommentservice.entities.UserAction;
import com.example.nestedcommentservice.enums.Action;
import com.example.nestedcommentservice.error.CustomException;
import com.example.nestedcommentservice.error.ErrorCode;
import com.example.nestedcommentservice.model.useraction.UserActionRequestModel;
import com.example.nestedcommentservice.model.useraction.UserActionResponseModel;
import com.example.nestedcommentservice.repository.UserActionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.example.nestedcommentservice.util.Constants.*;

/**
 * UserActionService - This is the service class to write business logic for user-action APIs *
 */

@Service
@Slf4j
public class UserActionService {

    @Autowired
    private UserActionRepository userActionRepository;

    @Autowired
    private ContentService contentService;

    @Autowired
    private UserService userService;

    private static Integer likeCount;
    private static Integer disLikeCount;

    /**
     * This method adds the user-action on given contentId
     *
     * * 1. If a person click on like button add +1 count in like.
     *     2. If a person click on dislike button add +1 count in dislike.
     *     3. If a person has like the content and click on dislike -1 count.
     *     from like and +1 dislike and vice versa : at a time either like or dislike.
     *     4. If a person like content then do +1 like count and again if the same person
     *     clicks on like button -1 the like count and same with dislike also. This deletes the entry
     *     from user action table *
     * @param userActionRequestModel
     * @return
     * @throws Exception
     */
    public UserActionResponseModel addUserAction(UserActionRequestModel userActionRequestModel) throws Exception {
        log.info("adding a new user-action for {} ", userActionRequestModel);
        try {
            checkValidations(userActionRequestModel);
            Boolean isDeleted = Boolean.FALSE;
            likeCount = 0;
            disLikeCount = 0;
            UserAction userAction = userActionRepository.findByUserIdAndContentId(userActionRequestModel.getUserId(),
                    userActionRequestModel.getContentId());
            if (Objects.isNull(userAction)) {
                getUserActionCount(null, userActionRequestModel.getAction(), likeCount, disLikeCount);
                userAction = new UserAction( userActionRequestModel.getContentId(), userActionRequestModel.getAction(),
                        userActionRequestModel.getUserId());
                userAction.prePersist();
            } else {
                getUserActionCount(userAction.getAction(), userActionRequestModel.getAction(), likeCount, disLikeCount);
                if (userAction.getAction().equals(userActionRequestModel.getAction())) {
                    userActionRepository.delete(userAction);
                    isDeleted = Boolean.TRUE;
                } else {
                    userAction.setAction(userActionRequestModel.getAction());
                    userAction.setLastUpdatedOn(new Date());
                }
            }
            if (Boolean.FALSE.equals(isDeleted)) {
                userActionRepository.save(userAction);
            }
            updateCount(userActionRequestModel);
            return UserActionResponseModel.builder().action(userActionRequestModel.getAction()).
                    userId(userActionRequestModel.getUserId()).actionEntityId(userActionRequestModel.getContentId())
                    .build();
        } catch (Exception e) {
            log.error("error while adding a new user-action for {} ", userActionRequestModel);
            throw new CustomException(ErrorCode.BAD_REQUEST, e.getMessage());
        }
    }

    private void updateCount(UserActionRequestModel userActionRequestModel) {
        contentService.updateUserActionCount(likeCount, disLikeCount, userActionRequestModel.getContentId());
    }

    private void getUserActionCount(Action existingAction, Action newAction, Integer likeCount, Integer disLikeCount) {
        log.info("getting user action count for newAction {}, existingAction {} ", newAction, existingAction);
        if (Objects.isNull(existingAction)) {
            if (Action.DISLIKE.equals(newAction)) {
                UserActionService.disLikeCount = disLikeCount + 1;
            } else if (Action.LIKE.equals(newAction)) {
                UserActionService.likeCount = likeCount + 1;
            }
        } else {
            if (Action.LIKE.equals(existingAction) && Action.LIKE.equals(newAction)) {
                UserActionService.likeCount = likeCount - 1;
            } else if (Action.DISLIKE.equals(existingAction) && Action.DISLIKE.equals(newAction)) {
                UserActionService.disLikeCount = disLikeCount - 1;
            } else if (Action.LIKE.equals(existingAction) && Action.DISLIKE.equals(newAction)) {
                UserActionService.likeCount = likeCount - 1;
                UserActionService.disLikeCount = disLikeCount + 1;
            } else if (Action.DISLIKE.equals(existingAction) && Action.LIKE.equals(newAction)) {
                UserActionService.likeCount = likeCount + 1;
                UserActionService.disLikeCount = disLikeCount - 1;
            }
        }
    }

    private void checkValidations(UserActionRequestModel userActionRequestModel) {
        User user = userService.findUser(userActionRequestModel.getUserId());
        if (Objects.isNull(user)) {
            throw new CustomException(ErrorCode.BAD_REQUEST, UNABLE_TO_FIND_USER);
        }
        if (!contentService.findContent(userActionRequestModel.getContentId())) {
            throw new CustomException(ErrorCode.BAD_REQUEST, UNABLE_TO_FIND_CONTENT);
        }
    }

    /**
     * This method is deleting the user-actions based on contentId*
     * @param contentId
     */
    public void deleteUserActionsForContent(String contentId) {
        log.info("deleting all user-actions for given content id {} ", contentId);
        List<UserAction> userActionList = userActionRepository.findByContentId(contentId);
        userActionRepository.deleteAll(userActionList);
    }

    /**
     * This method is deleting the user-actions based on userId**
     * @param userId
     */
    public void deleteUserActionsForUser(String userId) {
        log.info("deleting all user-actions for given user id {} ", userId);
        List<UserAction> userActionList = userActionRepository.findByUserId(userId);
        for (UserAction userAction : userActionList) {
            if (Action.LIKE.equals(userAction.getAction())) {
                likeCount = -1;
                disLikeCount = 0;
            } else if (Action.DISLIKE.equals(userAction.getAction())) {
                likeCount = 0;
                disLikeCount = -1;
            }
            try {
                contentService.updateUserActionCount(likeCount, disLikeCount, userAction.getContentId());
            }catch (CustomException e){
                if(!e.getMessage().equals(UNABLE_TO_FIND_CONTENT)){
                    log.error("error in deleting user-action for userId {}", userId);
                    throw new CustomException(ErrorCode.BAD_REQUEST, UNABLE_TO_DELETE_ACTION);
                }
            }
        }
        userActionRepository.deleteAll(userActionList);
    }

    /**
     * This method is used to fetch all userIds for given action and contentId*
     * @param contentId
     * @param action
     * @return
     */
    public List<String> getUsersByAction(String contentId, Action action) {
        log.info("fetching all user ids for given contentId {} and action {} ", contentId, action);
        List<UserAction> userActionList = userActionRepository.findByContentIdAndAction(contentId, action);
        return userActionList.stream().map(UserAction::getUserId).collect(Collectors.toList());
    }
}
