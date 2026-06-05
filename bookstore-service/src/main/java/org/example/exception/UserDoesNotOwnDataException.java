package org.example.exception;

public class UserDoesNotOwnDataException extends RuntimeException {
    public UserDoesNotOwnDataException(String message) {
        super(message);
    }
}
