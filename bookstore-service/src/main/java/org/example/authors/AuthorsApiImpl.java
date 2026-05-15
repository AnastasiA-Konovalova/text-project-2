package org.example.authors;

import org.example.api.AuthorsApi;
import org.example.model.Author;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AuthorsApiImpl implements AuthorsApi {

    @Override
    public ResponseEntity<Author> getAuthorById(Integer authorId) {
        Author author = new Author();
        return ResponseEntity.ok(author);
    }

    @Override
    public ResponseEntity<List<Author>> getAuthorsPopular() {
        return ResponseEntity.ok(List.of());
    }
}