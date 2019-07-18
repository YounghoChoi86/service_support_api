package com.tmoncorp.support.repository;

import com.tmoncorp.support.domain.Support;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SupportRepository extends MongoRepository<Support, Long> {
    Iterable<Support> findAllByYear(int year);
}
