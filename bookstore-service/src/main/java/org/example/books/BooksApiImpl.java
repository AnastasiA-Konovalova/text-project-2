package org.example.books;

import org.example.api.BooksApi;
import org.example.model.Book;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class BooksApiImpl implements BooksApi {

    @Override
    public ResponseEntity<List<Book>> getBooks(Integer id, Integer authorId, Integer seriesId, Integer publisherId, String genre, Integer limit) {
        return ResponseEntity.ok(List.of());
    }

    @Override
    public ResponseEntity<List<Book>> getBooksPopular(Integer authorId, String publisher, Boolean isPopular, Boolean isNew, Integer limit) {
        return ResponseEntity.ok(List.of());
    }
}