package org.example.controller.books;

import org.example.model.Book;
import org.example.api.BookApi;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class BooksApiImpl implements BookApi {

    @Override
    public ResponseEntity<List<Book>> getBooks(Integer id, Integer authorId, Integer seriesId, Integer publisherId, String genre, String publisherName, Boolean isPopular, Boolean isNew, Integer limit, Integer offset, String sort, String order) {
        return ResponseEntity.ok(List.of());
    }
}