package com.example.nestedcommentservice.service;

import com.example.nestedcommentservice.entities.Content;
import com.example.nestedcommentservice.enums.Action;
import com.example.nestedcommentservice.error.CustomException;
import com.example.nestedcommentservice.error.ErrorCode;
import com.example.nestedcommentservice.model.content.ContentRequestModel;
import com.example.nestedcommentservice.model.content.ContentResponseModel;
import com.example.nestedcommentservice.repository.ContentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.example.nestedcommentservice.util.Constants.*;

@Service
public class ContentService {

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private UserService userService;

    @Autowired
    @Lazy
    private UserActionService userActionService;

    public ContentResponseModel addContent(ContentRequestModel contentRequestModel) {
        try {
            checkValidation(contentRequestModel);
            Content content = buildContent(contentRequestModel, Boolean.FALSE, null);
            content = contentRepository.save(content);
            return ContentResponseModel.builder().contentId(content.getId())
                    .createdOn(getTimeDifferenceInString(content.getCreatedOn().getTime(), System.currentTimeMillis()))
                    .contentText(content.getContentText()).parentContentId(content.getParentContentId())
                    .contentEntity(content.getContentEntity())
                    .level(content.getLevel()).userId(content.getUserId()).build();
        } catch (Exception e) {
            throw new CustomException(ErrorCode.BAD_REQUEST, e.getMessage());
        }
    }

    public ContentResponseModel updateContent(ContentRequestModel contentRequestModel) {
        try {
            checkValidation(contentRequestModel);
            Content currentContent = contentRepository.findById(contentRequestModel.getContentId()).orElse(null);
            if (Objects.isNull(currentContent)) {
                throw new CustomException(ErrorCode.BAD_REQUEST, UNABLE_TO_FIND_CONTENT);
            }
            Content updatedContent = buildContent(contentRequestModel, Boolean.TRUE, currentContent);
            updatedContent = contentRepository.save(updatedContent);
            return ContentResponseModel.builder().contentId(updatedContent.getId())
                    .createdOn(getTimeDifferenceInString(updatedContent.getCreatedOn().getTime(), System.currentTimeMillis()))
                    .lastUpdatedOn(getTimeDifferenceInString(updatedContent.getLastUpdatedOn().getTime(), System.currentTimeMillis()))
                    .contentText(updatedContent.getContentText()).parentContentId(updatedContent.getParentContentId())
                    .contentEntity(contentRequestModel.getContentEntity())
                    .level(updatedContent.getLevel()).userId(updatedContent.getUserId()).build();
        } catch (Exception e) {
            throw new CustomException(ErrorCode.BAD_REQUEST, e.getMessage());
        }
    }

    /*Logic:
    This method gives first n level comments where here n=size */
    public Page<ContentResponseModel> getContent(String parentContentId, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Content> contentPage = contentRepository.findByParentContentId(parentContentId, pageable);
        Map<String, Integer> childContentCountMap = getChildContentCountMap(contentPage.getContent());
        List<String> userIds = contentPage.getContent().stream()
                .map(Content::getUserId)
                .collect(Collectors.toList());
        Map<String, String> userNameMap = userService.getUserMap(userIds);
        return contentPage.map(content -> buildContentResponseModel(content, childContentCountMap, userNameMap));
    }

    private Map<String, Integer> getChildContentCountMap(List<Content> contentList) {
        List<String> contentIds = contentList.stream()
                .map(Content::getId)
                .collect(Collectors.toList());
        List<Object[]> childContentCounts = contentRepository.getChildContentCounts(contentIds);

        Map<String, Integer> childContentCountMap = new HashMap<>();
        for (Object[] obj : childContentCounts) {
            String parentId = (String) obj[0];
            Long count = (Long) obj[1];
            childContentCountMap.put(parentId, count.intValue());
        }
        return childContentCountMap;
    }

    private ContentResponseModel buildContentResponseModel(Content content,
                                                           Map<String, Integer> childContentCountMap,
                                                           Map<String, String> userNameMap) {
        Integer childContentCount = childContentCountMap.getOrDefault(content.getId(), 0);
        String userName = userNameMap.getOrDefault(content.getUserId(), "Unknown");
        return ContentResponseModel.builder().contentId(content.getId()).childContentCount(childContentCount)
                .createdOn(getTimeDifferenceInString(content.getCreatedOn().getTime(), System.currentTimeMillis()))
                .contentText(content.getContentText()).parentContentId(content.getParentContentId())
                .contentEntity(content.getContentEntity()).level(content.getLevel())
                .userId(content.getUserId()).userName(userName).build();
    }

    /*Logic:
    This method gives first n level comments where here n=size and also
     up to l level horizontal comments for each first n vertical level comments */
    public List<ContentResponseModel> getHierarchyContent(String parentContentId, Integer level, Integer page, Integer size) {
        return null;
    }

    public Boolean findContent(String contentId) {
        Content content = contentRepository.findById(contentId).orElse(null);
        if (Objects.isNull(content)) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    public void updateUserActionCount(Integer likeCount, Integer disLikeCount, String contentId) {
        Content content = contentRepository.findById(contentId).orElse(null);
        if (Objects.isNull(content)) {
            throw new CustomException(ErrorCode.BAD_REQUEST, UNABLE_TO_FIND_CONTENT);
        }
        content.setLikeCount(content.getLikeCount() + likeCount);
        content.setDisLikeCount(content.getDisLikeCount() + disLikeCount);
        contentRepository.save(content);
    }

    public String getUserActionNames(String contentId, Action action) {
        Content content = contentRepository.findById(contentId).orElse(null);
        if (Objects.isNull(content)) {
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

    /*Logic:
    if content is deleted
     1. related child content also removed 2. related user-action removed */
    public Boolean deleteContent(String contentId) {
        try {
            Content content = contentRepository.findById(contentId).orElse(null);
            deleteFetchedContent(content);
            return true;
        } catch (Exception e) {
            throw new CustomException(ErrorCode.BAD_REQUEST, e.getMessage());
        }
    }

    private void deleteFetchedContent(Content content) {
        if (Objects.isNull(content)) {
            throw new CustomException(ErrorCode.NOT_FOUND, UNABLE_TO_FIND_CONTENT);
        }
        List<Content> childContents = contentRepository.findByParentContentId(content.getId());
        childContents.add(content);
        for (Content childContent : childContents) {
            userActionService.deleteUserActionsForContent(childContent.getId());
        }
        contentRepository.deleteAll(childContents);
    }

    public void deleteContentForUser(String userId) throws Exception {
        List<Content> contentList = contentRepository.findByUserId(userId);
        for(Content content : contentList){
            deleteFetchedContent(content);
        }
    }

    private Content buildContent(ContentRequestModel contentRequestModel, Boolean isUpdate, Content currentContent) {
        Content content = new Content();
        content.setContentText(contentRequestModel.getContentText());
        content.setUserId(contentRequestModel.getUserId());
        content.setContentEntity(contentRequestModel.getContentEntity());
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
        if (!userService.findUser(contentRequestModel.getUserId())) {
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
