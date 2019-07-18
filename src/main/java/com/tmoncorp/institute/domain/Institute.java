package com.tmoncorp.institute.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "institutes")
public class Institute {
    @Transient
    public static final String SEQUENCE_NAME = "institutes_sequence";
    @Id
    private String instituteCode;

    @Indexed(unique = true)
    private String instituteName;

    public Institute(String instituteName) {
        this.instituteName = instituteName;
    }

    public String toString() {
        return this.instituteName;
    }
}
