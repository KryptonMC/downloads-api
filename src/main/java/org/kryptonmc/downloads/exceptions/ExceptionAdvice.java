package org.kryptonmc.downloads.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler(DownloadFailedException.class)
    @ResponseBody
    public ResponseEntity<?> downloadFailed(final DownloadFailedException exception) {
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "An internal error occurred while trying to download the artifact!");
    }

    @ExceptionHandler(ProjectNotFoundException.class)
    @ResponseBody
    public ResponseEntity<?> projectNotFound(final ProjectNotFoundException exception) {
        return error(HttpStatus.NOT_FOUND, "The project '" + exception.projectId + "' does not exist!");
    }

    @ExceptionHandler(VersionNotFoundException.class)
    @ResponseBody
    public ResponseEntity<?> versionNotFound(final VersionNotFoundException exception) {
        return error(HttpStatus.NOT_FOUND, "The version '" + exception.versionId + "' does not exist for project '" + exception.projectId + "'!");
    }

    private ResponseEntity<?> error(final HttpStatus status, final String error) {
        return ResponseEntity.status(status).body(new ErrorResponse(error));
    }
}
