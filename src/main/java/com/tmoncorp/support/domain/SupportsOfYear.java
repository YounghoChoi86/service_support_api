package com.tmoncorp.support.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SupportsOfYear {
    @JsonProperty("year")
    private int year;
    @JsonProperty("total_amount")
    private long totalAmount;
    @JsonProperty("detail_amount")
    private Map<String, Long> detailAmount;
}
