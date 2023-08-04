package com.example.commentservicedemo.entities;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class CommonEntity {

    @Id
    private String id;

    @CreatedDate
    protected Date lastUpdatedOn;

    @CreatedDate
    protected Date createdOn;

    // Manually set the createdOn timestamp before saving the entity
    public void prePersist() {
        createdOn = new Date();
        lastUpdatedOn = new Date();
    }

}
