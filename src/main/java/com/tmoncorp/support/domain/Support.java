package com.tmoncorp.support.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;


@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "supports")
@CompoundIndexes({
        @CompoundIndex(def = "{'year':1, 'month': 1, 'bank' : 1}", name = "idx_year_month_bank"),
        @CompoundIndex(def = "{'year':1, 'bank': 1}", name = "idx_year_bank"),
        @CompoundIndex(def = "{'year':1, 'bank': 1, 'amount' : 1}", name = "idx_year_bank_amout")
})
public class Support {
    @Id
    private String id;
    private int year;
    private int month;
    private String bank;
    private long amount;
}
