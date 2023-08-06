package com.example.nestedcommentservice.entities;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * User - This is the entity class for user *
 */

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(collection = "user")
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
public class User extends CommonEntity {

    @Id
    private String id;

    @NonNull
    private String userName;

}
