package com.example.nestedcommentservice.model.useraction;

import com.example.nestedcommentservice.enums.Action;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserActionResponseModel {

    private String actionEntityId;
    private Action action;
    private String userId;

}
