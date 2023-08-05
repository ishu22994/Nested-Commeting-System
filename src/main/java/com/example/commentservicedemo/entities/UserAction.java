package com.example.commentservicedemo.entities;

import com.example.commentservicedemo.enums.Action;
import com.example.commentservicedemo.enums.ContentEntity;
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
    private ContentEntity contentEntity;

    @NonNull
    private String contentEntityId;

    @NonNull
    private Action action;

    @NonNull
    private String userId;

    public UserAction(@NonNull ContentEntity contentEntity, @NonNull String contentEntityId,
                      @NonNull Action action, @NonNull String userId) {
        this.contentEntity = contentEntity;
        this.contentEntityId = contentEntityId;
        this.action = action;
        this.userId = userId;
    }
}
