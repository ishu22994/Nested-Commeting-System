package com.example.nestedcommentservice.model.content;

import com.example.nestedcommentservice.enums.ContentType;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ContentResponseModel {

    private String contentId;
    private String userId;
    private String userName;
    private String contentText;
    private Integer level;
    private Integer childContentCount;
    private String parentContentId;
    private ContentType contentType;
    private String createdOn;
    private String lastUpdatedOn;

}
