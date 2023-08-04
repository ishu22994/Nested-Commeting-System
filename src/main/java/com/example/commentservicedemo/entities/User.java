package com.example.commentservicedemo.entities;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(collection = "user")
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper=false)
public class User extends CommonEntity{

    @NonNull
    private String userName;

}
