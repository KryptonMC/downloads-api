package org.kryptonmc.downloads.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.time.Instant;
import java.util.List;
import org.kryptonmc.downloads.database.model.Project;
import org.kryptonmc.downloads.database.model.Version;
import org.kryptonmc.downloads.database.repository.ProjectCollection;
import org.kryptonmc.downloads.database.repository.VersionCollection;
import org.kryptonmc.downloads.exceptions.ErrorResponse;
import org.kryptonmc.downloads.exceptions.ProjectNotFoundException;
import org.kryptonmc.downloads.exceptions.VersionNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "downloads", produces = MediaType.APPLICATION_JSON_VALUE)
public class VersionController {

    private final ProjectCollection projects;
    private final VersionCollection versions;

    @Autowired
    private VersionController(ProjectCollection projects, VersionCollection versions) {
        this.projects = projects;
        this.versions = versions;
    }

    @GetMapping("/v1/{project}/{version}")
    @Operation(summary = "Gets the information for a specific version of a project")
    @ApiResponse(
        responseCode = "200",
        description = "The version information",
        content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = VersionResponse.class))
    )
    @ApiResponse(
        responseCode = "404",
        description = "The project or version does not exist",
        content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))
    )
    public ResponseEntity<?> version(
        @Parameter(
            name = "project",
            in = ParameterIn.PATH,
            description = "The project ID",
            required = true,
            schema = @Schema(type = "string"),
            example = "krypton"
        )
        @PathVariable("project")
        String projectId,
        @Parameter(
            name = "version",
            in = ParameterIn.PATH,
            description = "The version ID",
            required = true,
            schema = @Schema(type = "string"),
            example = "abcdefghij"
        )
        @PathVariable("version")
        String versionId
    ) {
        final Project project = projects.findByName(projectId).orElseThrow(() -> new ProjectNotFoundException(projectId));
        final Version version = versions.findByProjectAndName(project._id(), versionId)
            .orElseThrow(() -> new VersionNotFoundException(projectId, versionId));
        return ResponseEntity.ok(VersionResponse.from(project, version));
    }

    @GetMapping("/v1/{project}/latest")
    @Operation(summary = "Gets the information for the latest version of a project")
    @ApiResponse(
        responseCode = "200",
        description = "The version information",
        content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = VersionResponse.class))
    )
    @ApiResponse(
        responseCode = "404",
        description = "The project does not exist or has no versions",
        content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))
    )
    public ResponseEntity<?> version(
        @Parameter(
            name = "project",
            in = ParameterIn.PATH,
            description = "The project ID",
            required = true,
            schema = @Schema(type = "string"),
            example = "krypton"
        )
        @PathVariable("project")
        String projectId
    ) {
        final Project project = projects.findByName(projectId).orElseThrow(() -> new ProjectNotFoundException(projectId));
        final Version version = versions.findAllByProject(project._id())
                .stream()
                .max(Version.COMPARATOR)
                .orElseThrow(() -> new VersionNotFoundException(projectId, "latest"));
        return ResponseEntity.ok(VersionResponse.from(project, version));
    }

    @Schema(name = "Version", type = "object")
    private record VersionResponse(
        @Schema(name = "project", type = "string", example = "krypton")
        String project,
        @Schema(name = "id", type = "string", example = "abcdefghij")
        String id,
        @Schema(name = "time", type = "string", format = "date-time", example = "2023-02-27T19:25:55Z")
        Instant time,
        @ArraySchema(
            schema = @Schema(type = "object", implementation = Version.Change.class),
            arraySchema = @Schema(name = "changes", type = "array")
        )
        List<Version.Change> changes
    ) {

        static VersionResponse from(final Project project, final Version version) {
            return new VersionResponse(project.name(), version.name(), version.time(), version.changes());
        }
    }
}
