package com.tmoncorp.support.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;


@ToString
@Getter
@Setter
public class SupportBulkInfo {
    private int year;
    private int month;
    @JsonProperty("detail_amount")
    Map<String, Long> detailAmount;
}