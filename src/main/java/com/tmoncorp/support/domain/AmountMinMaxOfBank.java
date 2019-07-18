package com.tmoncorp.support.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@ToString
@Getter
@Setter
public class AmountMinMaxOfBank {
    private String bank;
    private List<SupportAmount> supportAmount;
}
