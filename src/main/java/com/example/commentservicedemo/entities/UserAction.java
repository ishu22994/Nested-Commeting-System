package com.example.commentservicedemo.entities;

import com.example.commentservicedemo.enums.Action;
import com.example.commentservicedemo.enums.ActionEntity;
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
    private ActionEntity actionEntity;

    @NonNull
    private String actionEntityId;

    @NonNull
    private Action action;

    @NonNull
    private String userId;

    public UserAction(@NonNull ActionEntity actionEntity, @NonNull String actionEntityId,
                      @NonNull Action action, @NonNull String userId) {
        this.actionEntity = actionEntity;
        this.actionEntityId = actionEntityId;
        this.action = action;
        this.userId = userId;
    }
}
