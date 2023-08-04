package com.example.commentservicedemo.entities;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(collection = "comment")
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper=false)
public class Comment extends CommonEntity{

    @NonNull
    private String userId;

    @NonNull
    private String postId;

    @Builder.Default
    private Integer likeCount = 0;

    @Builder.Default
    private Integer disLikeCount = 0;

    @NonNull
    private Integer level;

    @NonNull
    private String parentCommentId;

    @NonNull
    private String commentText;

}
