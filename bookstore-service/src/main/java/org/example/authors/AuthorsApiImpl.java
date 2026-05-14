package org.example.authors;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import org.example.api.AuthorsApi;
import org.example.model.Author;
import org.example.model.Book;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AuthorsApiImpl implements AuthorsApi {

    public ResponseEntity<Author> authorsAuthorIdGet(Integer authorId) {
        Author author = new Author();
        author.setName("Author");
        return ResponseEntity.ok(author);
    }

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
