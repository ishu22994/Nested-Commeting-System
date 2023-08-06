package com.example.nestedcommentservice.service;

import com.example.nestedcommentservice.entities.User;
import com.example.nestedcommentservice.entities.UserAction;
import com.example.nestedcommentservice.enums.Action;
import com.example.nestedcommentservice.enums.ContentType;
import com.example.nestedcommentservice.error.CustomException;
import com.example.nestedcommentservice.error.ErrorCode;
import com.example.nestedcommentservice.model.useraction.UserActionRequestModel;
import com.example.nestedcommentservice.model.useraction.UserActionResponseModel;
import com.example.nestedcommentservice.repository.UserActionRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static com.example.nestedcommentservice.util.Constants.UNABLE_TO_FIND_USER;
import static com.mongodb.assertions.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserActionServiceTests {

    @Mock
    private UserActionRepository userActionRepository;

    @Mock
    private ContentService contentService;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserActionService userActionService;

    @Test
    public void testAddUserAction_CreateNewUserAction() throws Exception {
        when(userActionRepository.findByUserIdAndContentId(anyString(), anyString())).thenReturn(null);
        UserActionRequestModel requestModel = UserActionRequestModel.builder().action(Action.LIKE)
                .userId("userId1").contentId("contentId1").contentType(ContentType.COMMENT).build();
        when(userService.findUser("userId1")).thenReturn(User.builder().userName("ishit").build());
        when(contentService.findContent("contentId1")).thenReturn(true);
        UserActionResponseModel responseModel = userActionService.addUserAction(requestModel);
        assertNotNull(responseModel);
        assertEquals(requestModel.getUserId(), responseModel.getUserId());
        assertEquals(requestModel.getContentId(), responseModel.getActionEntityId());
        assertEquals(requestModel.getAction(), responseModel.getAction());
        verify(userActionRepository, times(1)).save(any(UserAction.class));
    }

    @Test
    public void testAddUserAction_UpdateExistingUserAction() throws Exception {
        UserAction existingUserAction = new UserAction(ContentType.COMMENT, "contentId1", Action.DISLIKE, "userId1");
        when(userActionRepository.findByUserIdAndContentId(anyString(), anyString())).thenReturn(existingUserAction);
        when(userService.findUser("userId1")).thenReturn(User.builder().userName("ishit").build());
        when(contentService.findContent("contentId1")).thenReturn(true);
        UserActionRequestModel requestModel = UserActionRequestModel.builder().action(Action.LIKE)
                .userId("userId1").contentId("contentId1").build();
        UserActionResponseModel responseModel = userActionService.addUserAction(requestModel);
        assertNotNull(responseModel);
        assertEquals(requestModel.getUserId(), responseModel.getUserId());
        assertEquals(requestModel.getContentId(), responseModel.getActionEntityId());
        assertEquals(requestModel.getAction(), responseModel.getAction());
        verify(userActionRepository, times(1)).save(any(UserAction.class));
    }

    @Test
    public void testAddUserAction_InvalidUserOrContent() throws Exception {
        doThrow(new CustomException(ErrorCode.BAD_REQUEST, UNABLE_TO_FIND_USER))
                .when(userService).findUser(anyString());
        UserActionRequestModel requestModel = UserActionRequestModel.builder().action(Action.LIKE)
                .userId("userId1").contentId("contentId1").build();
        try {
            userActionService.addUserAction(requestModel);
        } catch (CustomException e) {
            Assertions.assertNotNull(e.getMessage());
        }
    }

    @Test
    public void testDeleteUserActionsForContent() {
        List<UserAction> userActionList = Arrays.asList(
                new UserAction(ContentType.COMMENT, "contentId1", Action.LIKE, "userId1"),
                new UserAction(ContentType.COMMENT, "contentId1", Action.DISLIKE, "userId2")
        );
        when(userActionRepository.findByContentId(anyString())).thenReturn(userActionList);
        String contentId = "contentId1";
        userActionService.deleteUserActionsForContent(contentId);
        verify(userActionRepository, times(1)).deleteAll(eq(userActionList));
    }

    @Test
    public void testDeleteUserActionsForUser() {
        List<UserAction> userActionList = Arrays.asList(
                new UserAction(ContentType.COMMENT, "contentId1", Action.LIKE, "userId1"),
                new UserAction(ContentType.COMMENT, "contentId2", Action.DISLIKE, "userId1"),
                new UserAction(ContentType.COMMENT, "contentId1", Action.DISLIKE, "userId2")
        );
        when(userActionRepository.findByUserId(anyString())).thenReturn(userActionList);
        String userId = "userId1";
        userActionService.deleteUserActionsForUser(userId);
        verify(userActionRepository, times(1)).deleteAll(eq(userActionList));
        verify(contentService, times(1)).updateUserActionCount(0, -1, "contentId1");
        verify(contentService, times(1)).updateUserActionCount(-1, 0, "contentId1");
    }

}
