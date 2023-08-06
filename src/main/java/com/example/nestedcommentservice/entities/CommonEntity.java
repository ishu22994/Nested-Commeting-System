package com.example.nestedcommentservice.entities;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.util.Date;

/**
 * CommonEntity - This is the common property class *
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class CommonEntity {

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
