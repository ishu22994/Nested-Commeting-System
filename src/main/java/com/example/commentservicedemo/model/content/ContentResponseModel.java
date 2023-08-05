package com.example.commentservicedemo.model.content;

import com.example.commentservicedemo.enums.ContentEntity;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ContentResponseModel {

    private String contentId;
    private String userId;
    private String contentText;
    private Integer level;
    private String parentContentId;
    private ContentEntity contentEntity;
    private String createdOn;
    private String lastUpdatedOn;

}
