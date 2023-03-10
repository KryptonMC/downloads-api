package org.kryptonmc.downloads.database.repository;

import java.util.List;
import java.util.Optional;
import org.bson.types.ObjectId;
import org.kryptonmc.downloads.database.model.Version;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VersionCollection extends MongoRepository<Version, ObjectId> {

    List<Version> findAllByProject(ObjectId project);

    Optional<Version> findByProjectAndName(ObjectId project, String name);

    boolean existsByName(String name);
}
