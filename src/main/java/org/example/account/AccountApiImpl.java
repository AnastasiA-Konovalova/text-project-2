package org.example.account;

import org.example.api.AccountApi;
import org.example.model.AccountFavoritesPostRequest;
import org.example.model.Book;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class AccountApiImpl implements AccountApi {

    @Override
    public ResponseEntity<List<Book>> accountPurchasedBooksGet() {
        return ResponseEntity.ok(List.of());
    }

    @Override
    public ResponseEntity<Book> accountFavoritesPost(AccountFavoritesPostRequest req) {
        Book book = new Book();
        book.setId(req.getBookId());
        book.setTitle("Book");

        return ResponseEntity.status(201).body(book);
    }

    @Override
    public ResponseEntity<List<Book>> accountBasketGet() {
        return ResponseEntity.ok(List.of());
    }


}
