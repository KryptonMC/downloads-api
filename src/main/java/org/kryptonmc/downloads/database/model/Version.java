package org.kryptonmc.downloads.database.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@CompoundIndex(def = "{'project': 1, 'name': 1}")
@Document(collection = "versions")
public record Version(@Id ObjectId _id, ObjectId project, String name, Instant time, List<Change> changes,
                      String artifact) {

    public static final Comparator<Version> COMPARATOR = Comparator.comparing(Version::time);

    @Schema
    public record Change(@Schema(name = "commit") String commit, @Schema(name = "summary") String summary,
                         @Schema(name = "message") String message) {
    }
}
