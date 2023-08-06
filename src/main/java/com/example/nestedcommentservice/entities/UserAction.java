package com.example.nestedcommentservice.entities;

import com.example.nestedcommentservice.enums.Action;
import com.example.nestedcommentservice.enums.ContentType;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@Builder
@NoArgsConstructor
@Data
@Document(collection = "user-action")
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
public class UserAction extends CommonEntity {

    @NonNull
    private ContentType contentType;

    @NonNull
    private String contentEntityId;

    @NonNull
    private Action action;

    @NonNull
    private String userId;

    public UserAction(@NonNull ContentType contentType, @NonNull String contentEntityId,
                      @NonNull Action action, @NonNull String userId) {
        this.contentType = contentType;
        this.contentEntityId = contentEntityId;
        this.action = action;
        this.userId = userId;
    }
}
