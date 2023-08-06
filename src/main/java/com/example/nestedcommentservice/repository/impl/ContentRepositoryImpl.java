package com.example.nestedcommentservice.repository.impl;

import com.example.nestedcommentservice.entities.Content;
import com.example.nestedcommentservice.repository.ContentRepositoryCustom;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Repository
public class ContentRepositoryImpl implements ContentRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    public ContentRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<Object[]> getChildContentCounts(List<String> parentContentIds) {
        Aggregation aggregation = newAggregation(
                match(Criteria.where("parentContentId").in(parentContentIds)),
                group("parentContentId").count().as("childCount")
        );
        aggregation = aggregation.withOptions(AggregationOptions.builder().explain(true).build());
        AggregationResults<Object[]> result = mongoTemplate.aggregate(aggregation, "content", Object[].class);
        return result.getMappedResults();
    }

    @Override
    public List<Object> findContentHierarchy(String contentId, Integer level, Integer page, Integer Size) {
        LimitOperation limitOperation = Aggregation.limit(Size);
        SkipOperation skipOperation = Aggregation.skip((long) page * Size);
        TypedAggregation<Content> agg = Aggregation.newAggregation(Content.class,
                match(Criteria.where("_id").is(contentId)),
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
