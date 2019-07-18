package com.tmoncorp.institute.repository;

import com.tmoncorp.institute.domain.Institute;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface InstituteRepository extends MongoRepository<Institute, String> {
    Optional<Institute> findFirstByInstituteName(final String InstituteName);
}
