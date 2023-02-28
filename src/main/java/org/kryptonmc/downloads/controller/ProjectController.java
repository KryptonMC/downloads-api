package org.kryptonmc.downloads.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.List;
import java.util.Optional;
import org.kryptonmc.downloads.database.model.Project;
import org.kryptonmc.downloads.database.model.Version;
import org.kryptonmc.downloads.database.repository.ProjectCollection;
import org.kryptonmc.downloads.database.repository.VersionCollection;
import org.kryptonmc.downloads.exceptions.ErrorResponse;
import org.kryptonmc.downloads.exceptions.ProjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "downloads", produces = MediaType.APPLICATION_JSON_VALUE)
public class ProjectController {

    private final ProjectCollection projects;
    private final VersionCollection versions;

    @Autowired
    private ProjectController(ProjectCollection projects, VersionCollection versions) {
        this.projects = projects;
        this.versions = versions;
    }

    @GetMapping("/v1/{project}")
    @Operation(summary = "Gets the information for a project")
    @ApiResponse(
        responseCode = "200",
        description = "The project information",
        content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProjectResponse.class))
    )
    @ApiResponse(
        responseCode = "404",
        description = "The project does not exist",
        content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))
    )
    public ResponseEntity<?> project(
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
        final List<Version> versions = this.versions.findAllByProject(project._id());
        final Optional<Version> latest = findLatest(versions);
        return ResponseEntity.ok(ProjectResponse.from(project, versions, latest));
    }

    private Optional<Version> findLatest(final List<Version> versions) {
        return versions.stream().max(Version.COMPARATOR);
    }

    @Schema(name = "Project", type = "object")
    private record ProjectResponse(
        @Schema(name = "id", type = "string", example = "krypton")
        String id,
        @ArraySchema(schema = @Schema(type = "string", example = "abcdefghij"), arraySchema = @Schema(name = "versions", type = "array"))
        List<String> versions,
        @Schema(name = "latest_version", type = "string", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "abcdefghij")
        @JsonProperty("latest_version")
        String latestVersion
    ) {

        static ProjectResponse from(final Project project, final List<Version> versions, final Optional<Version> latest) {
            return new ProjectResponse(project.name(), versions.stream().map(Version::name).toList(), latest.map(Version::name).orElse(null));
        }
    }
}
