package org.kryptonmc.downloads.exceptions;

public final class DownloadFailedException extends RuntimeException {

    public DownloadFailedException(final Throwable cause) {
        super(cause);
    }
}
