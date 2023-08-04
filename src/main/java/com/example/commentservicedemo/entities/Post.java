package com.example.commentservicedemo.entities;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(collection = "post")
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper=false)
public class Post extends CommonEntity{

    @NonNull
    private String postText;

}
