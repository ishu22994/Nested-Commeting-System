package com.example.commentservicedemo.service;

import com.example.commentservicedemo.entities.UserAction;
import com.example.commentservicedemo.enums.Action;
import com.example.commentservicedemo.error.CustomException;
import com.example.commentservicedemo.error.ErrorCode;
import com.example.commentservicedemo.model.useraction.UserActionRequestModel;
import com.example.commentservicedemo.model.useraction.UserActionResponseModel;
import com.example.commentservicedemo.repository.UserActionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;

import static com.example.commentservicedemo.util.Constants.*;

@Service
public class UserActionService {

    @Autowired
    private UserActionRepository userActionRepository;

    @Autowired
    private ContentService contentService;

    @Autowired
    private UserService userService;

    private static Integer likeCount;
    private static Integer disLikeCount;

    /*logic :
    1. If a person click on like button add +1 count in like.
    2. If a person click on dislike button add +1 count in dislike.
    3. If a person has like the content and click on dislike -1 count.
    from like and +1 dislike and vice versa : at a time either like or dislike.
    4. If a person like content then do +1 like count and again if the same person
    clicks on like button -1 the like count and same with dislike also. This deletes the entry
    from user action table */

    public UserActionResponseModel addUserAction(UserActionRequestModel userActionRequestModel) throws Exception {
        try {
            checkValidations(userActionRequestModel);
            Boolean isDeleted = Boolean.FALSE;
            likeCount = 0;
            disLikeCount = 0;
            UserAction userAction = userActionRepository.findByUserIdAndContentEntityId(userActionRequestModel.getUserId(),
                    userActionRequestModel.getContentEntityId());
            if (Objects.isNull(userAction)) {
                getUserActionCount(null, userActionRequestModel.getAction(), likeCount, disLikeCount);
                userAction = new UserAction(userActionRequestModel.getContentEntity(), userActionRequestModel.getContentEntityId(),
                        userActionRequestModel.getAction(), userActionRequestModel.getUserId());
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
                    contentEntity(userActionRequestModel.getContentEntity()).userId(userActionRequestModel.getUserId()).
                    actionEntityId(userActionRequestModel.getContentEntityId()).build();
        } catch (Exception e) {
            throw new CustomException(ErrorCode.BAD_REQUEST, e.getMessage());
        }
    }

    private void updateCount(UserActionRequestModel userActionRequestModel) {
        contentService.updateUserActionCount(likeCount, disLikeCount, userActionRequestModel.getContentEntityId());
    }

    private void getUserActionCount(Action existingAction, Action newAction, Integer likeCount, Integer disLikeCount) {
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
        if (!userService.findUser(userActionRequestModel.getUserId())) {
            throw new CustomException(ErrorCode.BAD_REQUEST, UNABLE_TO_FIND_USER);
        }
        if (!contentService.findContent(userActionRequestModel.getContentEntityId())) {
            throw new CustomException(ErrorCode.BAD_REQUEST, UNABLE_TO_FIND_CONTENT);
        }
    }

}
