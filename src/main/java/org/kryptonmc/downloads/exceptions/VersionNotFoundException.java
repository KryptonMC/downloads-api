package org.kryptonmc.downloads.exceptions;

public final class VersionNotFoundException extends RuntimeException {

    final String projectId;
    final String versionId;

    public VersionNotFoundException(final String projectId, final String versionId) {
        this.projectId = projectId;
        this.versionId = versionId;
    }
}
