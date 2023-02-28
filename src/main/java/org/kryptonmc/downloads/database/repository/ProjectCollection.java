package org.kryptonmc.downloads.database.repository;

import java.util.Optional;
import org.bson.types.ObjectId;
import org.kryptonmc.downloads.database.model.Project;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectCollection extends MongoRepository<Project, ObjectId> {

    Optional<Project> findByName(String name);
}
