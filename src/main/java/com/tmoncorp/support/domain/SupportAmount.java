package com.tmoncorp.support.domain;

import lombok.*;

@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SupportAmount {
    private int year;
    private long amount;
}
