package com.example.nestedcommentservice.entities;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * User - This is the entity class for user *
 */

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(collection = "user")
@ToString(callSuper = true)
@EqualsAndHashCode()
public class User {

    @Id
    private String id;

    @NonNull
    private String userName;

    @CreatedDate
    private Date lastUpdatedOn;

    @CreatedDate
    private Date createdOn;

    public void prePersist() {
        createdOn = new Date();
        lastUpdatedOn = new Date();
    }

}
