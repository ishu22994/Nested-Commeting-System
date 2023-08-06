package com.example.nestedcommentservice.entities;

import com.example.nestedcommentservice.enums.Action;
import com.example.nestedcommentservice.enums.ContentType;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * UserAction - This is the entity class for user actions - like / dislike *
 */

@NoArgsConstructor
@Data
@Document(collection = "user-action")
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
public class UserAction extends CommonEntity {

    @Id
    private String id;

    @NonNull
    private ContentType contentType;

    @NonNull
    private String contentId;

    @NonNull
    private Action action;

    @NonNull
    private String userId;

    public UserAction(@NonNull ContentType contentType, @NonNull String contentId,
                      @NonNull Action action, @NonNull String userId) {
        this.contentType = contentType;
        this.contentId = contentId;
        this.action = action;
        this.userId = userId;
    }
}
