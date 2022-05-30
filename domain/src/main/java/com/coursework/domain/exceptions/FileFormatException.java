package com.coursework.domain.exceptions;

public class FileFormatException extends RuntimeException {

    public FileFormatException() {
    }

    public FileFormatException(String message) {
        super(message);
    }

    public FileFormatException(String message, Throwable cause) {
        super(message, cause);
    }

}
