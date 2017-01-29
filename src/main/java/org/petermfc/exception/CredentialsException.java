package org.petermfc.exception;

public class CredentialsException extends Exception {
    public CredentialsException(String message) {
        super(message);
    }

    public CredentialsException(String message, Throwable cause) {
        super(message, cause);
    }

    public CredentialsException(Throwable cause) {
        super(cause);
    }
}
