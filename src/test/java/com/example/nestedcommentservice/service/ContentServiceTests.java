package com.example.nestedcommentservice.service;

import com.example.nestedcommentservice.entities.Content;
import com.example.nestedcommentservice.entities.User;
import com.example.nestedcommentservice.enums.Action;
import com.example.nestedcommentservice.enums.ContentType;
import com.example.nestedcommentservice.error.CustomException;
import com.example.nestedcommentservice.model.content.ContentRequestModel;
import com.example.nestedcommentservice.model.content.ContentResponseModel;
import com.example.nestedcommentservice.repository.ContentRepository;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.*;

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
        when(userService.findUser(anyString())).thenReturn(null);
        assertThrows(CustomException.class, () -> {
            contentService.addContent(requestModel);
        });
    }

    @Test
    public void testDeleteContent() {
        String userId = "userId1";
        List<Content> contentList = Arrays.asList(Content.builder().contentType(ContentType.COMMENT).userId(userId)
                .level(1).parentContentId("NA").contentText("ii").build());
        when(contentRepository.findById(anyString())).thenReturn(Optional.of(contentList.get(0)));
        when(userService.findUser(anyString())).thenReturn(User.builder().userName("ishit").id(userId).build());
        String contentId = "contentId1";
        Boolean result = contentService.deleteContent(contentId, "userId");
        assertTrue(result);
        verify(contentRepository, times(1)).findById(anyString());
        verify(contentRepository, times(1)).delete(any());
    }

    @Test
    public void testDeleteContentForUser() {
        String userId = "userId1";
        List<Content> contentList = Arrays.asList(Content.builder().contentType(ContentType.COMMENT).userId(userId)
                .level(1).parentContentId("NA").contentText("ii").build());
        when(contentRepository.findByUserId(anyString())).thenReturn(contentList);
        assertDoesNotThrow(() -> contentService.deleteContentForUser(userId));
        verify(contentRepository, times(1)).delete(any());
    }

    @Test
    public void testAddContent_InvalidUserAndParentContent() {
        ContentRequestModel requestModel = new ContentRequestModel();
        when(userService.findUser(anyString())).thenReturn(null);
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
            contentService.deleteContent("contentId1","userId");
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

    @Test
    public void testGetContent_Positive() {
        List<Content> mockContentList = createMockContentList();
        Page<Content> mockContentPage = new PageImpl<>(mockContentList);
        Mockito.when(contentRepository.findByParentContentId(Mockito.anyString(), Mockito.any(Pageable.class)))
                .thenReturn(mockContentPage);
        Map<String, Integer> mockChildContentCountMap = createMockChildContentCountMap();
        Mockito.when(contentRepository.getChildContentCounts(Mockito.anyList()))
                .thenReturn(mockChildContentCountMap);
        Map<String, String> mockUserNameMap = createMockUserNameMap();
        Mockito.when(userService.getUserMap()).thenReturn(mockUserNameMap);
        String parentContentId = "parent_123";
        Integer page = 0;
        Integer size = 10;
        Page<ContentResponseModel> result = contentService.getContent(parentContentId, page, size);
        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.getTotalElements()); // Change the expected total elements based on your test data
    }

    @Test()
    public void testGetContent_Negative_ContentNotFound() {
        Page<Content> mockContentPage = new PageImpl<>(Collections.emptyList());
        Mockito.when(contentRepository.findByParentContentId(Mockito.anyString(), Mockito.any(Pageable.class)))
                .thenReturn(mockContentPage);
        String parentContentId = "non_existing_parent";
        Integer page = 0;
        Integer size = 10;
        contentService.getContent(parentContentId, page, size);
    }

    @Test
    public void testGetContent_Negative_ChildContentCountNotAvailable() {
        List<Content> mockContentList = createMockContentList();
        Page<Content> mockContentPage = new PageImpl<>(mockContentList);
        Mockito.when(contentRepository.findByParentContentId(Mockito.anyString(), Mockito.any(Pageable.class)))
                .thenReturn(mockContentPage);
        Mockito.when(contentRepository.getChildContentCounts(Mockito.anyList())).thenReturn(new HashMap<>());
        Map<String, String> mockUserNameMap = createMockUserNameMap();
        Mockito.when(userService.getUserMap()).thenReturn(mockUserNameMap);
        String parentContentId = "parent_123";
        Integer page = 0;
        Integer size = 10;
        Page<ContentResponseModel> result = contentService.getContent(parentContentId, page, size);
        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.getTotalElements());
        for (ContentResponseModel contentResponse : result.getContent()) {
            Assert.assertEquals(0, contentResponse.getChildContentCount().intValue());
        }
    }

    @Test
    public void testGetContent_Negative_UserMapNotAvailable() {
        List<Content> mockContentList = createMockContentList();
        Page<Content> mockContentPage = new PageImpl<>(mockContentList);
        Mockito.when(contentRepository.findByParentContentId(Mockito.anyString(), Mockito.any(Pageable.class)))
                .thenReturn(mockContentPage);
        Map<String, Integer> mockChildContentCountMap = createMockChildContentCountMap();
        Mockito.when(contentRepository.getChildContentCounts(Mockito.anyList())).thenReturn(mockChildContentCountMap);
        Mockito.when(userService.getUserMap()).thenReturn(new HashMap<>());
        String parentContentId = "parent_123";
        Integer page = 0;
        Integer size = 10;
        contentService.getContent(parentContentId, page, size);
    }

    @Test
    public void testGetContent_Negative_IncorrectPaginationParameters() {
        String parentContentId = "parent_123";
        Integer page = -1; // Incorrect value
        Integer size = 10;
        assertThrows(IllegalArgumentException.class, () -> {
            contentService.getContent(parentContentId, page, size);
        });
    }

    private List<Content> createMockContentList() {
        List<Content> contentList = new ArrayList<>();
        Content content  = Content.builder().contentText("abc").contentType(ContentType.COMMENT).parentContentId("parent_123")
                .level(1).userId("userId").id("contentId").createdOn(new Date()).lastUpdatedOn(new Date())
                .likeCount(0).disLikeCount(0).build();
        contentList.add(content);
        return contentList;
    }

    private Map<String, Integer> createMockChildContentCountMap() {
        Map<String, Integer> map = new HashMap<>();
        map.put("userId", 1);
        return map;
    }

    private Map<String, String> createMockUserNameMap() {
        Map<String, String> map = new HashMap<>();
        map.put("userId", "ishit");
        return map;
    }

}

