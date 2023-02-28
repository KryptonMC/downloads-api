package org.kryptonmc.downloads.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.List;
import org.kryptonmc.downloads.database.model.Project;
import org.kryptonmc.downloads.database.repository.ProjectCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "downloads", produces = MediaType.APPLICATION_JSON_VALUE)
public class ProjectsController {

    private final ProjectCollection projects;

    @Autowired
    private ProjectsController(ProjectCollection projects) {
        this.projects = projects;
    }

    @GetMapping("/v1/projects")
    @Operation(summary = "Lists all available projects that can be downloaded")
    @ApiResponse(
        responseCode = "200",
        description = "All available projects",
        content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            array = @ArraySchema(schema = @Schema(type = "string", example = "abcdefghij"))
        )
    )
    public ResponseEntity<?> projects() {
        final List<String> projects = this.projects.findAll().stream().map(Project::name).toList();
        return ResponseEntity.ok(projects);
    }
}
