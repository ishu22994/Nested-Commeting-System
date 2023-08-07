package com.example.nestedcommentservice.entities;

import com.example.nestedcommentservice.enums.Action;
import com.example.nestedcommentservice.enums.ContentType;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * UserAction - This is the entity class for user actions - like / dislike *
 */

@NoArgsConstructor
@Data
@Document(collection = "user-action")
@ToString(callSuper = true)
@EqualsAndHashCode()
public class UserAction {

    @Id
    private String id;

    @NonNull
    private String contentId;

    @NonNull
    private Action action;

    @NonNull
    private String userId;

    @CreatedDate
    private Date lastUpdatedOn;

    @CreatedDate
    private Date createdOn;

    public void prePersist() {
        createdOn = new Date();
        lastUpdatedOn = new Date();
    }

    public UserAction(@NonNull String contentId, @NonNull Action action, @NonNull String userId) {
        this.contentId = contentId;
        this.action = action;
        this.userId = userId;
    }
}
