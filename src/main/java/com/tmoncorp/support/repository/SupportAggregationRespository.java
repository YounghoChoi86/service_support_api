package com.tmoncorp.support.repository;

import com.tmoncorp.support.domain.Support;
import com.tmoncorp.support.domain.SupportAmount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Repository
public class SupportAggregationRespository {
    @Autowired
    private MongoTemplate mongoTemplate;

    public List<Support> selectSupportListAggregationSumAmountGroupYearBank() {
        Aggregation agg =
                newAggregation(group("year", "bank").sum("amount").as("amount"),
                        sort(Sort.Direction.DESC, "year", "bank"));

        AggregationResults<Support> groupAmounts = mongoTemplate
                .aggregate(agg, Support.class, Support.class);

        return groupAmounts.getMappedResults();
    }

    public List<SupportAmount> selectSupportListAggregationAvgAmountGroupYearBank(String bankCode) {
        AggregationOperation match = Aggregation.match(Criteria.where("bank").is(bankCode));

        Aggregation agg =
                newAggregation(match, group("year", "bank").avg("amount").as("amount"),
                        sort(Sort.Direction.DESC, "year", "bank"));

        AggregationResults<SupportAmount> groupAmounts = mongoTemplate
                .aggregate(agg, Support.class, SupportAmount.class);

        return groupAmounts.getMappedResults();
    }
}
