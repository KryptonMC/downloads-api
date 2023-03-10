package org.kryptonmc.downloads.database.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "tokens")
public record ApiToken(@Id ObjectId _id, String name, String token) {
}
