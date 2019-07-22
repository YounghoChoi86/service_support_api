package com.tmoncorp.support.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SupportAmount {
    private int year;
    @JsonIgnore
    private double amount;
    @JsonProperty("amount")
    private long roundedAmount;
}
