package com.example.nestedcommentservice.repository.impl;

import com.example.nestedcommentservice.repository.ContentRepositoryCustom;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
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

        AggregationResults<Object[]> result = mongoTemplate.aggregate(aggregation, "content", Object[].class);
        return result.getMappedResults();
    }

}
