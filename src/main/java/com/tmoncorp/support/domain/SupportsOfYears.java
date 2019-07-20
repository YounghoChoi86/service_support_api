package com.tmoncorp.support.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SupportsOfYears  {
    private final String name = "주택기금정보";
    @JsonProperty("supports_of_years")
    private List<SupportsOfYear> supportsOfYears;
}
