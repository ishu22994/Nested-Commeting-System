package com.example.nestedcommentservice.entities;

import com.example.nestedcommentservice.enums.ContentEntity;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(collection = "content")
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper=false)
public class Content extends CommonEntity{

    @NonNull
    private String userId;

    @NonNull
    private ContentEntity contentEntity;

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
