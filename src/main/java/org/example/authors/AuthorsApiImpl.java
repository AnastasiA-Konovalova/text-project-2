package org.example.authors;

import org.example.api.AuthorsApi;
import org.example.model.Book;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class AuthorsApiImpl implements AuthorsApi {

    public ResponseEntity<List<Book>> authorsPopularAuthorIdGet(Integer authorId) {
        return ResponseEntity.ok(List.of());
    }

    public ResponseEntity<List<Book>> authorsNewAuthorIdGet(Integer authorId) {
        return ResponseEntity.ok(List.of());
    }

    public ResponseEntity<List<Book>> authorsBooksAuthorIdGet(Integer authorId) {
        return ResponseEntity.ok(List.of());
    }
}
