package org.example.account;

import org.example.model.Book;
import org.example.model.GetFavoriteBooksRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.example.api.AccountApi;

import java.util.List;

@RestController
public class AccountApiImpl implements AccountApi {

    @Override
    public ResponseEntity<Void> deleteBookById(Integer bookId) {
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<List<Book>> getBasket() {
        return ResponseEntity.ok(List.of());
    }

    @Override
    public ResponseEntity<Book> getFavoriteBooks(GetFavoriteBooksRequest getFavoriteBooksRequest) {
        Book book = new Book();
        book.setId(getFavoriteBooksRequest.getBookId());
        book.setTitle("Book");

        return ResponseEntity.ok(book);
    }

    @Override
    public ResponseEntity<List<Book>> getPurchasedBooks() {
        return ResponseEntity.ok(List.of());
    }

    @Override
    public ResponseEntity<Book> postIntoBasket(GetFavoriteBooksRequest getFavoriteBooksRequest) {
        Book book = new Book();
        book.setId(getFavoriteBooksRequest.getBookId());
        book.setTitle("Book");

        return ResponseEntity.ok(book);
    }
}