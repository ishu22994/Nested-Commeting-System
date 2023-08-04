package com.example.commentservicedemo.service.impl;

import com.example.commentservicedemo.entities.UserAction;
import com.example.commentservicedemo.enums.Action;
import com.example.commentservicedemo.enums.ActionEntity;
import com.example.commentservicedemo.error.CustomException;
import com.example.commentservicedemo.error.ErrorCode;
import com.example.commentservicedemo.model.useraction.UserActionRequestModel;
import com.example.commentservicedemo.model.useraction.UserActionResponseModel;
import com.example.commentservicedemo.repository.UserActionRepository;
import com.example.commentservicedemo.service.CommentService;
import com.example.commentservicedemo.service.PostService;
import com.example.commentservicedemo.service.UserActionService;
import com.example.commentservicedemo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;

import static com.example.commentservicedemo.util.Constants.*;

@Service
public class UserActionServiceImpl implements UserActionService {

    @Autowired
    private UserActionRepository userActionRepository;

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    private static Integer likeCount;
    private static Integer disLikeCount;

    /*logic :
    1. If a person click on like button add +1 count in like.
    2. If a person click on dislike button add +1 count in dislike.
    3. If a person has like the comment and click on dislike -1 count.
    from like and +1 dislike and vice versa : at a time either like or dislike.
    4. If a person like comment then do +1 like count and again if the same person
    clicks on like button -1 the like count and same with dislike also. This deletes the entry
    from user action table */

    @Override
    public UserActionResponseModel addUserAction(UserActionRequestModel userActionRequestModel) throws Exception {
        try {
            checkValidations(userActionRequestModel);
            Boolean isDeleted = Boolean.FALSE;
            likeCount = 0;
            disLikeCount = 0;
            UserAction userAction = userActionRepository.findByUserIdAndActionEntityId(userActionRequestModel.getUserId(),
                    userActionRequestModel.getActionEntityId());
            if (Objects.isNull(userAction)) {
                getUserActionCount(null, userActionRequestModel.getAction(), likeCount, disLikeCount);
                userAction = new UserAction(userActionRequestModel.getActionEntity(), userActionRequestModel.getActionEntityId(),
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
                    actionEntity(userActionRequestModel.getActionEntity()).userId(userActionRequestModel.getUserId()).
                    actionEntityId(userActionRequestModel.getActionEntityId()).build();
        } catch (Exception e) {
            throw new CustomException(ErrorCode.BAD_REQUEST, e.getMessage());
        }
    }

    private void updateCount(UserActionRequestModel userActionRequestModel) {
        if (ActionEntity.COMMENT.equals(userActionRequestModel.getActionEntity())) {
            commentService.updateUserActionCount(likeCount, disLikeCount, userActionRequestModel.getActionEntityId());
        } else if (ActionEntity.POST.equals(userActionRequestModel.getActionEntity())) {
            postService.updateUserActionCount(likeCount, disLikeCount, userActionRequestModel.getActionEntityId());
        }
    }

    private void getUserActionCount(Action existingAction, Action newAction, Integer likeCount, Integer disLikeCount) {
        if (Objects.isNull(existingAction)) {
            if (Action.DISLIKE.equals(newAction)) {
               UserActionServiceImpl.disLikeCount = disLikeCount + 1;
            } else if (Action.LIKE.equals(newAction)) {
                UserActionServiceImpl.likeCount = likeCount + 1;
            }
        } else {
            if (Action.LIKE.equals(existingAction) && Action.LIKE.equals(newAction)) {
                UserActionServiceImpl.likeCount = likeCount - 1;
            } else if (Action.DISLIKE.equals(existingAction) && Action.DISLIKE.equals(newAction)) {
                UserActionServiceImpl.disLikeCount = disLikeCount - 1;
            } else if (Action.LIKE.equals(existingAction) && Action.DISLIKE.equals(newAction)) {
                UserActionServiceImpl.likeCount = likeCount - 1;
                UserActionServiceImpl.disLikeCount = disLikeCount + 1;
            } else if (Action.DISLIKE.equals(existingAction) && Action.LIKE.equals(newAction)) {
                UserActionServiceImpl.likeCount = likeCount + 1;
                UserActionServiceImpl.disLikeCount = disLikeCount - 1;
            }
        }
    }

    private void checkValidations(UserActionRequestModel userActionRequestModel) {
        if (!userService.findUser(userActionRequestModel.getUserId())) {
            throw new CustomException(ErrorCode.BAD_REQUEST, UNABLE_TO_FIND_USER);
        }
        if (ActionEntity.COMMENT.equals(userActionRequestModel.getActionEntity())) {
            if (!commentService.findComment(userActionRequestModel.getActionEntityId())) {
                throw new CustomException(ErrorCode.BAD_REQUEST, UNABLE_TO_FIND_COMMENT);
            }
        } else if (ActionEntity.POST.equals(userActionRequestModel.getActionEntity())) {
            if (!postService.findPost(userActionRequestModel.getActionEntityId())) {
                throw new CustomException(ErrorCode.BAD_REQUEST, UNABLE_TO_FIND_POST);
            }
        }
    }

}
