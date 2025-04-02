package com.example.demo.exception;

public class PostCreationException extends RuntimeException {

    public PostCreationException(String message) {
        super(message);
    }

    public PostCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}
