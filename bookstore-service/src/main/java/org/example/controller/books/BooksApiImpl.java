package org.example.controller.books;

import org.example.model.Book;
import org.example.api.BookApi;
import org.example.model.Genre;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class BooksApiImpl implements BookApi {

    @Override
    public ResponseEntity<List<Book>> getBooks(Integer id, Integer authorId, Integer seriesId, Integer publisherId, Genre genre, String publisherName, Boolean isPopular, Boolean isNew, Integer limit, Integer offset, String sortBook, String order) {
        return ResponseEntity.ok(List.of());
    }
}