package com.example.commentservicedemo.service.impl;

import com.example.commentservicedemo.entities.Content;
import com.example.commentservicedemo.error.CustomException;
import com.example.commentservicedemo.error.ErrorCode;
import com.example.commentservicedemo.model.content.ContentRequestModel;
import com.example.commentservicedemo.model.content.ContentResponseModel;
import com.example.commentservicedemo.repository.ContentRepository;
import com.example.commentservicedemo.service.ContentService;
import com.example.commentservicedemo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;

import static com.example.commentservicedemo.util.Constants.*;

@Service
public class ContentServiceImpl implements ContentService {

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private UserService userService;

    @Override
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

    @Override
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

    @Override
    public Boolean findContent(String contentId) {
        Content content = contentRepository.findById(contentId).orElse(null);
        if (Objects.isNull(content)) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    @Override
    public void updateUserActionCount(Integer likeCount, Integer disLikeCount, String commentId) {
        Content content = contentRepository.findById(commentId).orElse(null);
        if (Objects.isNull(content)) {
            throw new CustomException(ErrorCode.BAD_REQUEST, UNABLE_TO_FIND_CONTENT);
        }
        content.setLikeCount(content.getLikeCount() + likeCount);
        content.setDisLikeCount(content.getDisLikeCount() + disLikeCount);
        contentRepository.save(content);
    }

    /*Logic:
    if content is deleted
     1. related child content also removed 2. related user-action removed */
    @Override
    public Boolean deleteContent(String contentId) {
        try {
            Content content = contentRepository.findById(contentId).orElse(null);
            if (Objects.isNull(content)) {
                throw new CustomException(ErrorCode.NOT_FOUND, UNABLE_TO_FIND_CONTENT);
            }
            // ishit do here
            contentRepository.delete(content);
            return true;
        } catch (Exception e) {
            throw new CustomException(ErrorCode.BAD_REQUEST, e.getMessage());
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