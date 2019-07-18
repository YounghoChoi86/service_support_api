package com.tmoncorp.common.error.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ErrorInfo {
    private int code;
    private String message;
}
