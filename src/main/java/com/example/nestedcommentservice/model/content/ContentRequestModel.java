package com.example.nestedcommentservice.model.content;

import com.example.nestedcommentservice.enums.ContentType;
import lombok.*;

import static com.example.nestedcommentservice.util.Constants.NA;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ContentRequestModel {

    private String contentId;

    @NonNull
    private String userId;

    @NonNull
    private String contentText;

    @NonNull
    private ContentType contentType;

    @Builder.Default
    private Integer level = 1;

    @Builder.Default
    private String parentContentId = NA;

}
