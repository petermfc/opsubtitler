package org.petermfc.exception;

public class DownloadException extends Exception {
    public DownloadException(Throwable t) {
        super(t);
    }
    public DownloadException(String message, Throwable t) {
        super(message, t);
    }
}
