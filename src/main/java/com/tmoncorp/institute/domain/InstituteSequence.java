package com.tmoncorp.institute.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Setter
@Getter
@Document(collection = "institutes_sequence")
public class InstituteSequence {

    public static final String INSTITUTE_PREFIX = "bnk";
    @Id
    private String id;

    private long seq;

}
