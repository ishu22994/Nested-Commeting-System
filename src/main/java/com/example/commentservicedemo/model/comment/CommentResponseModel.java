package com.example.commentservicedemo.model.comment;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CommentResponseModel {

    private String commentId;
    private String userId;
    private String postId;
    private String commentText;
    private Integer level;
    private String parentCommentId;
    private String createdOn;
    private String lastUpdatedOn;

}
