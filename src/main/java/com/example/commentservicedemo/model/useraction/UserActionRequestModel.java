package com.example.commentservicedemo.model.useraction;

import com.example.commentservicedemo.enums.Action;
import com.example.commentservicedemo.enums.ContentEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserActionRequestModel {

    private ContentEntity contentEntity;
    private String contentEntityId;
    private Action action;
    private String userId;

}
