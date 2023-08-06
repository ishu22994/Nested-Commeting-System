package com.example.nestedcommentservice.service;

import com.example.nestedcommentservice.entities.Content;
import com.example.nestedcommentservice.entities.User;
import com.example.nestedcommentservice.enums.Action;
import com.example.nestedcommentservice.error.CustomException;
import com.example.nestedcommentservice.error.ErrorCode;
import com.example.nestedcommentservice.model.content.ContentRequestModel;
import com.example.nestedcommentservice.model.content.ContentResponseModel;
import com.example.nestedcommentservice.repository.ContentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.example.nestedcommentservice.util.Constants.*;

/**
 * ContentService - This is the service class to write business logic for content APIs *
 */

@Service
@Slf4j
public class ContentService {

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private UserService userService;

    @Autowired
    @Lazy
    private UserActionService userActionService;

    /**
     * This method is used to add content*
     * @param contentRequestModel
     * @return
     */
    public ContentResponseModel addContent(ContentRequestModel contentRequestModel) {
        log.info("adding a content for contentRequestModel {} ", contentRequestModel);
        try {
            checkValidation(contentRequestModel);
            Content content = buildContent(contentRequestModel, Boolean.FALSE, null);
            content = contentRepository.save(content);
            return ContentResponseModel.builder().contentId(content.getId())
                    .createdOn(getTimeDifferenceInString(content.getCreatedOn().getTime(), System.currentTimeMillis()))
                    .contentText(content.getContentText()).parentContentId(content.getParentContentId())
                    .contentType(content.getContentType())
                    .level(content.getLevel()).userId(content.getUserId()).build();
        } catch (Exception e) {
            log.info("error while adding a content for contentRequestModel {} ", contentRequestModel);
            throw new CustomException(ErrorCode.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * This method is used to update content*
     * @param contentRequestModel
     * @return
     */
    public ContentResponseModel updateContent(ContentRequestModel contentRequestModel) {
        log.info("updating a content for contentRequestModel {} ", contentRequestModel);
        try {
            checkValidation(contentRequestModel);
            Content currentContent = contentRepository.findById(contentRequestModel.getContentId()).orElse(null);
            if (Objects.isNull(currentContent) || !currentContent.getUserId().equals(contentRequestModel.getUserId())) {
                throw new CustomException(ErrorCode.BAD_REQUEST, UNABLE_TO_UPDATE_CONTENT);
            }
            Content updatedContent = buildContent(contentRequestModel, Boolean.TRUE, currentContent);
            updatedContent = contentRepository.save(updatedContent);
            return ContentResponseModel.builder().contentId(updatedContent.getId())
                    .createdOn(getTimeDifferenceInString(updatedContent.getCreatedOn().getTime(), System.currentTimeMillis()))
                    .lastUpdatedOn(getTimeDifferenceInString(updatedContent.getLastUpdatedOn().getTime(), System.currentTimeMillis()))
                    .contentText(updatedContent.getContentText()).parentContentId(updatedContent.getParentContentId())
                    .contentType(contentRequestModel.getContentType())
                    .level(updatedContent.getLevel()).userId(updatedContent.getUserId()).build();
        } catch (Exception e) {
            log.info("error while updating a content for contentRequestModel {} ", contentRequestModel);
            throw new CustomException(ErrorCode.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * This method gives first N level comments using pagination where here N=size *
     * @param parentContentId
     * @param page
     * @param size
     * @return
     */
    public Page<ContentResponseModel> getContent(String parentContentId, Integer page, Integer size) {
        log.info("get content for first N level content for parentContentId {}, page {}, size {}",
                parentContentId, page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<Content> contentPage = contentRepository.findByParentContentId(parentContentId, pageable);
        Map<String, Integer> childContentCountMap = getChildContentCountMap(contentPage.getContent());
        Map<String, String> userNameMap = userService.getUserMap();
        return contentPage.map(content -> buildContentResponseModel(content, childContentCountMap, userNameMap));
    }

    private Map<String, Integer> getChildContentCountMap(List<Content> contentList) {
        List<String> contentIds = contentList.stream()
                .map(Content::getId)
                .collect(Collectors.toList());
        return contentRepository.getChildContentCounts(contentIds);
    }

    private ContentResponseModel buildContentResponseModel(Content content,
                                                           Map<String, Integer> childContentCountMap,
                                                           Map<String, String> userNameMap) {
        Integer childContentCount = childContentCountMap.getOrDefault(content.getId(), 0);
        String userName = userNameMap.getOrDefault(content.getUserId(), "Unknown");
        return ContentResponseModel.builder().contentId(content.getId()).childContentCount(childContentCount)
                .createdOn(getTimeDifferenceInString(content.getCreatedOn().getTime(), System.currentTimeMillis()))
                .contentText(content.getContentText()).parentContentId(content.getParentContentId())
                .contentType(content.getContentType()).level(content.getLevel()).likeCount(content.getLikeCount())
                .disLikeCount(content.getDisLikeCount()).userId(content.getUserId()).userName(userName).build();
    }

    /**
     * This method is used to find content from contentId *
     * @param contentId
     * @return
     */
    public Boolean findContent(String contentId) {
        log.info("finding a content for contentId {} ", contentId);
        Content content = contentRepository.findById(contentId).orElse(null);
        if (Objects.isNull(content)) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    /**
     * This method is used to update the like/dislike counts*
     * @param likeCount
     * @param disLikeCount
     * @param contentId
     */
    public void updateUserActionCount(Integer likeCount, Integer disLikeCount, String contentId) {
        log.info("updating user-action count for contentId {}", contentId);
        Content content = contentRepository.findById(contentId).orElse(null);
        if (Objects.isNull(content)) {
            log.error("error in updating user-action count for contentId {}", contentId);
            throw new CustomException(ErrorCode.BAD_REQUEST, UNABLE_TO_FIND_CONTENT);
        }
        content.setLikeCount(content.getLikeCount() + likeCount);
        content.setDisLikeCount(content.getDisLikeCount() + disLikeCount);
        contentRepository.save(content);
    }

    /**
     * This method is used to get userNames for given contentId who like/dislike*
     * @param contentId
     * @param action
     * @return
     */
    public String getUserActionNames(String contentId, Action action) {
        log.info("get user names for contentId {}, action {}", contentId, action);
        Content content = contentRepository.findById(contentId).orElse(null);
        if (Objects.isNull(content)) {
            log.info("error in getting user names for contentId {}, action {}", contentId, action);
            throw new CustomException(ErrorCode.BAD_REQUEST, UNABLE_TO_FIND_CONTENT);
        }
        List<String> userIds = userActionService.getUsersByAction(contentId, action);
        List<String> userNames = userService.getUsersByIds(userIds);
        String finalList = "";
        for (String name : userNames) {
            finalList = finalList.concat(" ").concat(name);
        }
        return finalList.trim();
    }

    /**
     * This method is for delete content for contentId*
     *
     * if content is deleted
     *     1. related child content also removed 2. related user-action removed
     * @param contentId
     * @param userId
     * @return
     */
    public Boolean deleteContent(String contentId, String userId) {
        log.info("delete content for contentId {}, userId {}", contentId, userId);
        try {
            Content content = contentRepository.findById(contentId).orElse(null);
            User user = userService.findUser(userId);
            if(Objects.isNull(content) || !user.getId().equals(content.getUserId())){
                throw new CustomException(ErrorCode.INVALID, UNABLE_TO_DELETE_CONTENT);
            }
            deleteFetchedContent(content);
            return true;
        } catch (Exception e) {
            log.info("error in deleting content for contentId {}, userId {}", contentId, userId);
            throw new CustomException(ErrorCode.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * deleting content and its child contents provided by content *
     * @param content
     */
    private void deleteFetchedContent(Content content) {
        if (Objects.isNull(content)) {
            throw new CustomException(ErrorCode.NOT_FOUND, UNABLE_TO_FIND_CONTENT);
        }
        deleteRecursive(content);
    }

    /**
     * This method deletes all child contents for given content*
     * @param content
     */
    private void deleteRecursive(Content content) {
        List<Content> childContents = contentRepository.findByParentContentId(content.getId());
        for (Content childContent : childContents) {
            deleteRecursive(childContent);
            userActionService.deleteUserActionsForContent(childContent.getId());
        }
        contentRepository.delete(content);
    }

    /**
     * This method deletes all content for given userId*
     * @param userId
     * @throws Exception
     */
    public void deleteContentForUser(String userId) throws Exception {
        log.info("delete content for userId {}", userId);
        List<Content> contentList = contentRepository.findByUserId(userId);
        for(Content content : contentList){
            deleteFetchedContent(content);
        }
    }

    private Content buildContent(ContentRequestModel contentRequestModel, Boolean isUpdate, Content currentContent) {
        Content content = new Content();
        content.setContentText(contentRequestModel.getContentText());
        content.setUserId(contentRequestModel.getUserId());
        content.setContentType(contentRequestModel.getContentType());
        content.setParentContentId(contentRequestModel.getParentContentId());
        content.setLevel(contentRequestModel.getLevel());
        if (Boolean.TRUE.equals(isUpdate)) {
            content.setLastUpdatedOn(new Date());
            content.setCreatedOn(currentContent.getCreatedOn());
            content.setId(currentContent.getId());
        } else {
            content.prePersist();
        }
        return content;
    }

    private void checkValidation(ContentRequestModel contentRequestModel) {
        User user = userService.findUser(contentRequestModel.getUserId());
        if (Objects.isNull(user)) {
            throw new CustomException(ErrorCode.BAD_REQUEST, UNABLE_TO_FIND_USER);
        }
        if (!NA.equals(contentRequestModel.getParentContentId())) {
            Content parentContent = contentRepository.findById(contentRequestModel.getParentContentId()).orElse(null);
            if (Objects.isNull(parentContent)) {
                throw new CustomException(ErrorCode.BAD_REQUEST, UNABLE_TO_FIND_PARENT_CONTENT);
            }
        }
    }

    private String getTimeDifferenceInString(Long fromTime, Long toTime) {
        if (toTime == null || fromTime == null) {
            return "-";
        }
        Long diff = toTime - fromTime;
        Long days = (diff / (1000 * 24 * 60 * 60L));
        Long hours = (diff / (1000 * 60 * 60L)) % 24;
        Long minutes = (diff / (1000 * 60L)) % 60;
        Long seconds = 1L;
        StringBuilder s = new StringBuilder();
        if (days > 0) {
            s.append(days + " Days ");
        }
        if (hours > 0) {
            s.append(hours + " Hours ");
        }
        if (minutes > 0) {
            s.append(minutes + " Minutes ");
        }
        if (seconds > 0) {
            s.append(seconds + " Second ");
        }
        s.append("ago");
        return s.toString();
    }

}
