package com.example.nestedcommentservice.entities;

import com.example.nestedcommentservice.enums.ContentType;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Content - This is the entity class for content - content can be post / comment based on contentType *
 */

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(collection = "content")
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper=false)
public class Content extends CommonEntity{

    @Id
    private String id;

    @NonNull
    private String userId;

    @NonNull
    private ContentType contentType;

    @Builder.Default
    private Integer likeCount = 0;

    @Builder.Default
    private Integer disLikeCount = 0;

    @NonNull
    private Integer level;

    @NonNull
    private String parentContentId;

    @NonNull
    private String contentText;

}
