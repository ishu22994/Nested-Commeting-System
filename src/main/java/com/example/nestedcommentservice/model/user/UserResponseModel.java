package com.example.nestedcommentservice.model.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserResponseModel {

    private String userId;
    private String userName;
    private Date createdOn;

}
