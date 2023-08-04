package com.example.commentservicedemo.service.impl;

import com.example.commentservicedemo.entities.Comment;
import com.example.commentservicedemo.error.CustomException;
import com.example.commentservicedemo.error.ErrorCode;
import com.example.commentservicedemo.model.comment.CommentRequestModel;
import com.example.commentservicedemo.model.comment.CommentResponseModel;
import com.example.commentservicedemo.repository.CommentRepository;
import com.example.commentservicedemo.service.CommentService;
import com.example.commentservicedemo.service.PostService;
import com.example.commentservicedemo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;

import static com.example.commentservicedemo.util.Constants.*;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    @Override
    public CommentResponseModel addComment(CommentRequestModel commentRequestModel) {
        try {
            checkValidation(commentRequestModel);
            Comment comment = buildComment(commentRequestModel, Boolean.FALSE, null);
            comment = commentRepository.save(comment);
            return CommentResponseModel.builder().commentId(comment.getId())
                    .createdOn(getTimeDifferenceInString(comment.getCreatedOn().getTime(), System.currentTimeMillis()))
                    .commentText(comment.getCommentText()).parentCommentId(comment.getParentCommentId())
                    .level(comment.getLevel()).postId(comment.getPostId()).userId(comment.getUserId()).build();
        } catch (Exception e) {
            throw new CustomException(ErrorCode.BAD_REQUEST, e.getMessage());
        }
    }

    @Override
    public CommentResponseModel updateComment(CommentRequestModel commentRequestModel) {
        try {
            checkValidation(commentRequestModel);
            Comment currentComment = commentRepository.findById(commentRequestModel.getCommentId()).orElse(null);
            if (Objects.isNull(currentComment)) {
                throw new CustomException(ErrorCode.BAD_REQUEST, UNABLE_TO_FIND_COMMENT);
            }
            Comment updatedComment = buildComment(commentRequestModel, Boolean.TRUE, currentComment);
            updatedComment = commentRepository.save(updatedComment);
            return CommentResponseModel.builder().commentId(updatedComment.getId())
                    .createdOn(getTimeDifferenceInString(updatedComment.getCreatedOn().getTime(), System.currentTimeMillis()))
                    .lastUpdatedOn(getTimeDifferenceInString(updatedComment.getLastUpdatedOn().getTime(), System.currentTimeMillis()))
                    .commentText(updatedComment.getCommentText()).parentCommentId(updatedComment.getParentCommentId())
                    .level(updatedComment.getLevel()).postId(updatedComment.getPostId()).userId(updatedComment.getUserId()).build();
        } catch (Exception e) {
            throw new CustomException(ErrorCode.BAD_REQUEST, e.getMessage());
        }
    }

    @Override
    public Boolean findComment(String commentId) {
        Comment comment = commentRepository.findById(commentId).orElse(null);
        if (Objects.isNull(comment)) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    @Override
    public void updateUserActionCount(Integer likeCount, Integer disLikeCount, String commentId) {
        Comment comment = commentRepository.findById(commentId).orElse(null);
        if (Objects.isNull(comment)) {
            throw new CustomException(ErrorCode.BAD_REQUEST, UNABLE_TO_FIND_COMMENT);
        }
        comment.setLikeCount(comment.getLikeCount() + likeCount);
        comment.setDisLikeCount(comment.getDisLikeCount() + disLikeCount);
        commentRepository.save(comment);
    }

    private Comment buildComment(CommentRequestModel commentRequestModel, Boolean isUpdate, Comment currentComment) {
        Comment comment = new Comment();
        comment.setCommentText(commentRequestModel.getCommentText());
        comment.setPostId(commentRequestModel.getPostId());
        comment.setUserId(commentRequestModel.getUserId());
        comment.setParentCommentId(commentRequestModel.getParentCommentId());
        comment.setLevel(commentRequestModel.getLevel());
        if (Boolean.TRUE.equals(isUpdate)) {
            comment.setLastUpdatedOn(new Date());
            comment.setCreatedOn(currentComment.getCreatedOn());
            comment.setId(currentComment.getId());
        } else {
            comment.prePersist();
        }
        return comment;
    }

    private void checkValidation(CommentRequestModel commentRequestModel) {
        if (!userService.findUser(commentRequestModel.getUserId())) {
            throw new CustomException(ErrorCode.BAD_REQUEST, UNABLE_TO_FIND_USER);
        }
        if (!postService.findPost(commentRequestModel.getPostId())) {
            throw new CustomException(ErrorCode.BAD_REQUEST, UNABLE_TO_FIND_POST);
        }
        if (!NA.equals(commentRequestModel.getParentCommentId())) {
            Comment parentComment = commentRepository.findById(commentRequestModel.getParentCommentId()).orElse(null);
            if (Objects.isNull(parentComment)) {
                throw new CustomException(ErrorCode.BAD_REQUEST, UNABLE_TO_FIND_PARENT_COMMENT);
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
