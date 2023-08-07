package com.example.nestedcommentservice.entities;

import com.example.nestedcommentservice.enums.ContentType;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * Content - This is the entity class for content - content can be post / comment based on contentType *
 */

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(collection = "content")
@ToString()
@EqualsAndHashCode()
public class Content {

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

    @CreatedDate
    private Date lastUpdatedOn;

    @CreatedDate
    private Date createdOn;

    public void prePersist() {
        createdOn = new Date();
        lastUpdatedOn = new Date();
    }

}
