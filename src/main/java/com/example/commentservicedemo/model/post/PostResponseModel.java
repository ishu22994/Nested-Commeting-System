package com.example.commentservicedemo.model.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PostResponseModel {

    private String postId;
    private String postText;
    private Date createdOn;

}
