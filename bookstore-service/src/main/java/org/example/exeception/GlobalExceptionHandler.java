package org.example.exeception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BasketItemNotFoundException.class)
    public ResponseEntity<Error> handle(BasketItemNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new Error());
    }

    @ExceptionHandler(ClassNotFoundException.class)
    public ResponseEntity<Error> handle(ClassNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new Error());
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Error> handle(ResponseStatusException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new Error());
    }
}
