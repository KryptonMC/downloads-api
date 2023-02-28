package org.kryptonmc.downloads.exceptions;

public final class ProjectNotFoundException extends RuntimeException {

    final String projectId;

    public ProjectNotFoundException(final String projectId) {
        super();
        this.projectId = projectId;
    }
}
