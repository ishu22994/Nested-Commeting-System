package com.example.nestedcommentservice.repository.impl;

import com.example.nestedcommentservice.entities.Content;
import com.example.nestedcommentservice.repository.ContentRepositoryCustom;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import static org.springframework.data.mongodb.core.query.Criteria.where;

@Repository
@Slf4j
public class ContentRepositoryImpl implements ContentRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    public ContentRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Map<String, Integer> getChildContentCounts(List<String> parentContentIds) {
        Aggregation aggregation = newAggregation(
                match(where("parentContentId").in(parentContentIds)),
                group("parentContentId").count().as("childCount")
        );
        log.info("aggregation query for getting getChildContentCounts : {}", aggregation);
        AggregationResults<Object> result = mongoTemplate.aggregate(aggregation, Content.class, Object.class);
        Map<String, Integer> childContentCountMap = new HashMap<>();
        for (Object obj : result.getMappedResults()) {
            LinkedHashMap linkedHashMap = (LinkedHashMap) obj;
            String parentId = (String) linkedHashMap.get("_id");
            Integer count = (Integer) linkedHashMap.get("childCount");
            childContentCountMap.put(parentId, count);
        }
        return childContentCountMap;
    }

    @Override
    public List<Object> findContentHierarchy(String contentId, Integer level, Integer page, Integer Size) {
        LimitOperation limitOperation = Aggregation.limit(Size);
        SkipOperation skipOperation = Aggregation.skip((long) page * Size);
        TypedAggregation<Content> agg = Aggregation.newAggregation(Content.class,
                match(where("_id").is(contentId)),
                Aggregation.graphLookup("content")
                        .startWith("$_id")
                        .connectFrom("parentContentId")
                        .connectTo("parentContentId")
                        .depthField("depth")
                        .maxDepth(level)
                        .as("contentHierarchy"), skipOperation, limitOperation);
        AggregationResults<Document> result = mongoTemplate.aggregate(agg, Document.class);
        Document object = result.getUniqueMappedResult();
        return (List<Object>) object.get("contentHierarchy");
    }

}
