package org.example.exception;

import org.springframework.http.HttpStatus;
import org.example.model.Error;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Error> handle(NotFoundException ex) {
        Error error = new Error();
        error.setMessage(ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(error);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Error> handle(ResponseStatusException ex) {
        Error error = new Error();
        error.setMessage(ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(error);
    }

    @ExceptionHandler(UserDoesNotOwnDataException.class)
    public ResponseEntity<Error> handle(UserDoesNotOwnDataException ex) {
        Error error = new Error();
        error.setMessage(ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(error);
    }

    @ExceptionHandler(UserAlreadyExists.class)
    public ResponseEntity<Error> handle(UserAlreadyExists ex) {
        Error error = new Error();
        error.setMessage(ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }

    @ExceptionHandler({
            jakarta.validation.ConstraintViolationException.class,
            org.springframework.web.method.annotation.HandlerMethodValidationException.class
    })
    public ResponseEntity<Map<String, String>> handleValidation(Exception ex) {

        return ResponseEntity.badRequest()
                .body(Map.of("message", ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Error> handle(IllegalArgumentException ex) {
        Error error = new Error();
        error.setMessage(ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }

    @ExceptionHandler(SumLessThenMin.class)
    public ResponseEntity<Error> handle(SumLessThenMin ex) {
        Error error = new Error();
        error.setMessage(ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }
}