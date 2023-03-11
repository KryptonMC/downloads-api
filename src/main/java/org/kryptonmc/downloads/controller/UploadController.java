package org.kryptonmc.downloads.controller;

import org.bson.types.ObjectId;
import org.kryptonmc.downloads.config.AppConfig;
import org.kryptonmc.downloads.database.model.Project;
import org.kryptonmc.downloads.database.model.Version;
import org.kryptonmc.downloads.database.repository.ProjectCollection;
import org.kryptonmc.downloads.database.repository.VersionCollection;
import org.kryptonmc.downloads.exceptions.ProjectNotFoundException;
import org.kryptonmc.downloads.exceptions.VersionAlreadyExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping(value = "downloads")
public final class UploadController {

    private final AppConfig config;
    private final ProjectCollection projects;
    private final VersionCollection versions;

    @Autowired
    public UploadController(AppConfig config, ProjectCollection projects, VersionCollection versions) {
        this.config = config;
        this.projects = projects;
        this.versions = versions;
    }

    @PostMapping(value = "/v1/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> upload(@RequestPart("data") UploadData data, @RequestPart("file") MultipartFile file) {
        final Project project = projects.findByName(data.projectId())
            .orElseThrow(() -> new ProjectNotFoundException(data.projectId()));
        if (versions.existsByName(data.version())) {
            throw new VersionAlreadyExistsException(data.projectId, data.version);
        }

        final String artifactName = data.projectId + "-" + data.version + ".jar";
        final Path path = config.storagePath()
            .resolve(data.projectId)
            .resolve(artifactName);
        try {
            Files.createDirectories(path.getParent());
            Files.deleteIfExists(path);
            file.transferTo(path);
        } catch (final IOException exception) {
            throw new RuntimeException(exception);
        }

        final ObjectId id = new ObjectId();
        final Version version = new Version(id, project._id(), data.version, Instant.now(), data.changes);
        versions.save(version);
        return ResponseEntity.ok().build();
    }

    private record UploadData(String projectId, String version, List<Version.Change> changes) {
    }
}
