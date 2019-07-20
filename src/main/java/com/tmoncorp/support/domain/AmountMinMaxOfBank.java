package com.tmoncorp.support.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@ToString
@Getter
@Setter
public class AmountMinMaxOfBank {
    private String bank;
    @JsonProperty("support_amount")
    private List<SupportAmount> supportAmount;
}
