package com.tmoncorp.common.service;

import com.tmoncorp.institute.domain.InstituteSequence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static org.springframework.data.mongodb.core.FindAndModifyOptions.options;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Service
public class SequenceGeneratorService {

    @Autowired
    private MongoOperations mongoOperations;

    public String generateInstituteSequence(String seqName) {
        InstituteSequence instituteSequence = mongoOperations.findAndModify(query(where("_id").is(seqName)),
                new Update().inc("seq",1), options().returnNew(true).upsert(true),
                InstituteSequence.class);

        long seq =  !Objects.isNull(instituteSequence) ?  instituteSequence.getSeq() : 1;

        return String.format(InstituteSequence.INSTITUTE_PREFIX + "%04d", seq);
    }
}
