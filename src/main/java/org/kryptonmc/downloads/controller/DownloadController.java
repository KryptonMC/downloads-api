package org.kryptonmc.downloads.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.kryptonmc.downloads.config.AppConfig;
import org.kryptonmc.downloads.database.model.Project;
import org.kryptonmc.downloads.database.model.Version;
import org.kryptonmc.downloads.database.repository.ProjectCollection;
import org.kryptonmc.downloads.database.repository.VersionCollection;
import org.kryptonmc.downloads.exceptions.DownloadFailedException;
import org.kryptonmc.downloads.exceptions.ErrorResponse;
import org.kryptonmc.downloads.exceptions.ProjectNotFoundException;
import org.kryptonmc.downloads.exceptions.VersionNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "downloads", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
public class DownloadController {

    private final AppConfig config;
    private final ProjectCollection projects;
    private final VersionCollection versions;

    @Autowired
    public DownloadController(AppConfig config, ProjectCollection projects, VersionCollection versions) {
        this.config = config;
        this.projects = projects;
        this.versions = versions;
    }

    @GetMapping(value = "/v1/{project}/{version}/download")
    @Operation(summary = "Downloads a specific version of a project")
    @ApiResponse(
        responseCode = "200",
        description = "The downloaded artifact",
        content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE),
        headers = {
            @Header(
                name = HttpHeaders.CONTENT_DISPOSITION,
                description = "A header indicating that the content is expected to be displayed as an attachment, " +
                    "that is downloaded and saved locally.",
                schema = @Schema(type = "string")
            ),
            @Header(
                name = HttpHeaders.ETAG,
                description = "An identifier for a specific version of a resource. It lets caches be more efficient " +
                    "and save bandwidth, as a web server does not need to resend a full response if the content " +
                    "has not changed.",
                schema = @Schema(type = "string")
            ),
            @Header(
                name = HttpHeaders.LAST_MODIFIED,
                description = "The date and time at which the origin server believes the resource was last modified.",
                schema = @Schema(type = "string")
            )
        }
    )
    @ApiResponse(
        responseCode = "404",
        description = "The project or version does not exist",
        content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))
    )
    @ApiResponse(
        responseCode = "500",
        description = "An internal error occurred trying to retrieve the artifact to download",
        content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))
    )
    public ResponseEntity<?> download(
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
        final String artifactName = project.name() + "-" + version.name() + ".jar";
        try {
            return new JavaArchive(config.storagePath()
                .resolve(project.name())
                .resolve(artifactName));
        } catch (final IOException exception) {
            throw new DownloadFailedException(exception);
        }
    }

    @GetMapping(value = "/v1/{project}/latest/download")
    @Operation(summary = "Downloads the latest version of a project")
    @ApiResponse(
        responseCode = "200",
        description = "The downloaded artifact",
        content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE),
        headers = {
            @Header(
                name = HttpHeaders.CONTENT_DISPOSITION,
                description = "A header indicating that the content is expected to be displayed as an attachment, " +
                    "that is downloaded and saved locally.",
                schema = @Schema(type = "string")
            ),
            @Header(
                name = HttpHeaders.ETAG,
                description = "An identifier for a specific version of a resource. It lets caches be more efficient " +
                    "and save bandwidth, as a web server does not need to resend a full response if the content " +
                    "has not changed.",
                schema = @Schema(type = "string")
            ),
            @Header(
                name = HttpHeaders.LAST_MODIFIED,
                description = "The date and time at which the origin server believes the resource was last modified.",
                schema = @Schema(type = "string")
            )
        }
    )
    @ApiResponse(
        responseCode = "404",
        description = "The project does not exist or has no versions",
        content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))
    )
    @ApiResponse(
        responseCode = "500",
        description = "An internal error occurred trying to retrieve the artifact to download",
        content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))
    )
    public ResponseEntity<?> download(
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
        final String artifactName = project.name() + "-" + version.name() + ".jar";
        try {
            return new JavaArchive(config.storagePath()
                .resolve(project.name())
                .resolve(artifactName));
        } catch (final IOException exception) {
            throw new DownloadFailedException(exception);
        }
    }

    private static final class JavaArchive extends ResponseEntity<FileSystemResource> {

        JavaArchive(final Path path) throws IOException {
            super(new FileSystemResource(path), headersFor(path), HttpStatus.OK);
        }

        private static HttpHeaders headersFor(final Path path) throws IOException {
            final HttpHeaders headers = new HttpHeaders();
            headers.setContentDisposition(ContentDisposition.attachment().filename(path.getFileName().toString(), StandardCharsets.UTF_8).build());
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setLastModified(Files.getLastModifiedTime(path).toInstant());
            return headers;
        }
    }
}
