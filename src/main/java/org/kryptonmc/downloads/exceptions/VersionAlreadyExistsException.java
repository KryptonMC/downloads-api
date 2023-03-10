package org.kryptonmc.downloads.exceptions;

public final class VersionAlreadyExistsException extends RuntimeException {

    final String projectId;
    final String versionId;

    public VersionAlreadyExistsException(final String projectId, final String versionId) {
        this.projectId = projectId;
        this.versionId = versionId;
    }
}
