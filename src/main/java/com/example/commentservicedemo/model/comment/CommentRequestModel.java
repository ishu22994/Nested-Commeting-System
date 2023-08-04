package com.example.commentservicedemo.model.comment;

import lombok.*;

import static com.example.commentservicedemo.util.Constants.NA;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CommentRequestModel {

    private String commentId;

    @NonNull
    private String userId;

    @NonNull
    private String postId;

    @NonNull
    private String commentText;

    @Builder.Default
    private Integer level = 1;

    @Builder.Default
    private String parentCommentId = NA;

}
