package org.kryptonmc.downloads.database.repository;

import org.bson.types.ObjectId;
import org.kryptonmc.downloads.database.model.ApiToken;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApiTokenCollection extends MongoRepository<ApiToken, ObjectId> {

    Optional<ApiToken> findByName(String name);

    Optional<ApiToken> findByToken(String token);

    boolean existsByName(String name);
}
