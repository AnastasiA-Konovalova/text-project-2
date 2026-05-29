package org.example.controller.authors;

import lombok.RequiredArgsConstructor;
import org.example.model.Author;
import org.example.api.AuthorsApi;
import org.example.service.author.AuthorApiInterface;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AuthorsApiImpl implements AuthorsApi {

    private final AuthorApiInterface authorInterface;

    @Override
    public ResponseEntity<Author> getAuthorById(Integer authorId) {
        return ResponseEntity.ok(authorInterface.getAuthorById(authorId));
    }

    @Override
    public ResponseEntity<List<Author>> getPopularAuthors(Integer limit, Integer offset) {
        return ResponseEntity.ok(authorInterface.getPopularAuthors(limit, offset));
    }
}