package com.tmoncorp.support.repository;

import com.tmoncorp.support.domain.Support;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface SupportRepository extends MongoRepository<Support, Long> {
    Optional<Support> findById(String id);
    Optional<Support> deleteById(String id);
}
