package org.example.books;


import org.example.api.BooksApi;
import org.example.model.Author;
import org.example.model.Book;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class BooksApiImpl implements BooksApi {

    @Override
    public ResponseEntity<List<Book>> booksNewGet() {
//        Book book = new Book();
//
//        book.setId(1);
//        book.setTitle("Title");
//        book.setAuthor("Author");
//        book.setGenre("Programming");
//        book.setPrice(30.0);

        return ResponseEntity.ok(List.of());
    }

    @Override
    public ResponseEntity<List<Book>> booksPopularGet() {
        return ResponseEntity.ok(List.of());
    }

    @Override
    public  ResponseEntity<List<Book>> booksGenreGenreGet(String genre) {
        return ResponseEntity.ok(List.of());
    }

    @Override
    public ResponseEntity<List<Author>> booksAuthorsPopularGet() {
        return ResponseEntity.ok(List.of());
    }
}
