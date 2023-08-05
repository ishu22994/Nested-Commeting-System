package com.example.nestedcommentservice.service;

import com.example.nestedcommentservice.entities.Content;
import com.example.nestedcommentservice.enums.Action;
import com.example.nestedcommentservice.enums.ContentEntity;
import com.example.nestedcommentservice.error.CustomException;
import com.example.nestedcommentservice.model.content.ContentRequestModel;
import com.example.nestedcommentservice.repository.ContentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.mongodb.assertions.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class ContentServiceTests {

    @Mock
    private ContentRepository contentRepository;

    @Mock
    private UserService userService;

    @Mock
    private UserActionService userActionService;

    @InjectMocks
    private ContentService contentService;

    @Test
    public void testFindContent_ExistingContent() {
        when(contentRepository.findById(anyString())).thenReturn(Optional.of(new Content()));
        String contentId = "contentId1";
        Boolean result = contentService.findContent(contentId);
        assertTrue(result);
        verify(contentRepository, times(1)).findById(anyString());
    }

    @Test
    public void testAddContent_InvalidUserOrParentContent() {
        ContentRequestModel requestModel = new ContentRequestModel();
        when(userService.findUser(anyString())).thenReturn(false);
        assertThrows(CustomException.class, () -> {
            contentService.addContent(requestModel);
        });
    }

    @Test
    public void testDeleteContent() {
        Content existingContent = new Content();
        when(contentRepository.findById(anyString())).thenReturn(Optional.of(existingContent));
        String contentId = "contentId1";
        Boolean result = contentService.deleteContent(contentId);
        assertTrue(result);
        verify(contentRepository, times(1)).findById(anyString());
        verify(userActionService, times(1)).deleteUserActionsForContent(null);
        verify(contentRepository, times(1)).deleteAll(anyList());
    }

    @Test
    public void testDeleteContentForUser() {
        String userId = "userId1";
        List<Content> contentList = Arrays.asList(Content.builder().contentEntity(ContentEntity.COMMENT).userId(userId)
                .level(1).parentContentId("NA").contentText("ii").build());
        when(contentRepository.findByUserId(anyString())).thenReturn(contentList);
        assertDoesNotThrow(() -> contentService.deleteContentForUser(userId));
        verify(userActionService, times(contentList.size())).deleteUserActionsForContent(null);
        verify(contentRepository, times(1)).deleteAll(anyList());
    }

    @Test
    public void testAddContent_InvalidUserAndParentContent() {
        ContentRequestModel requestModel = new ContentRequestModel();
        when(userService.findUser(anyString())).thenReturn(false);
        when(contentRepository.findById(anyString())).thenReturn(Optional.empty());
        assertThrows(CustomException.class, () -> {
            contentService.addContent(requestModel);
        });
    }

    @Test
    public void testUpdateContent_NonExistingContent() {
        ContentRequestModel requestModel = new ContentRequestModel();
        assertThrows(CustomException.class, () -> {
            contentService.updateContent(requestModel);
        });
    }

    @Test
    public void testUpdateUserActionCount_NonExistingContent() {
        Integer likeCount = 1;
        Integer disLikeCount = 0;
        when(contentRepository.findById(anyString())).thenReturn(Optional.empty());
        assertThrows(CustomException.class, () -> {
            contentService.updateUserActionCount(likeCount, disLikeCount, "contentId1");
        });
    }

    @Test
    public void testGetUserActionNames_NonExistingContent() {
        Action action = Action.LIKE;
        when(contentRepository.findById(anyString())).thenReturn(Optional.empty());
        assertThrows(CustomException.class, () -> {
            contentService.getUserActionNames("contentId1", action);
        });
    }

    @Test
    public void testDeleteContent_NonExistingContent() {
        when(contentRepository.findById(anyString())).thenReturn(Optional.empty());
        assertThrows(CustomException.class, () -> {
            contentService.deleteContent("contentId1");
        });
    }

    @Test
    public void testGetUserActionNames_UserActionServiceException() {
        Action action = Action.LIKE;
        Content existingContent = new Content();
        when(contentRepository.findById(anyString())).thenReturn(Optional.of(existingContent));
        when(userActionService.getUsersByAction(anyString(), any(Action.class))).thenThrow(new RuntimeException());
        assertThrows(RuntimeException.class, () -> {
            contentService.getUserActionNames("contentId1", action);
        });
    }

    @Test
    public void testDeleteContentForUser_NoContentFound() {
        String userId = "userId1";
        when(contentRepository.findByUserId(anyString())).thenReturn(Collections.emptyList());
        assertDoesNotThrow(() -> contentService.deleteContentForUser(userId));
        verify(contentRepository, never()).findById(anyString());
        verify(userActionService, never()).deleteUserActionsForContent(anyString());
    }



}
