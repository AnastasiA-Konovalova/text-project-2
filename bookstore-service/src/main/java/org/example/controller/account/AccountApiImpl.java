package org.example.controller.account;

import lombok.RequiredArgsConstructor;
import org.example.model.Basket;
import org.example.model.BookIdRequest;
import org.example.service.account.AccountApiInterface;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.example.api.AccountApi;
import org.example.model.Book;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AccountApiImpl implements AccountApi {

    private final AccountApiInterface accountInterface;

    @Override
    public ResponseEntity<List<Book>> addFavoriteBook(Integer bookId, Integer id) {
        return ResponseEntity.ok(accountInterface.addFavoriteBook(bookId, id));
    }

    @Override
    public ResponseEntity<List<Book>> getFavoriteBooks(Integer accountId, Integer limit, Integer offset, String sortBook, String order) {
        return ResponseEntity.ok(accountInterface.getFavoriteBooks(accountId, limit, offset, sortBook, order));
    }

    @Override
    public ResponseEntity<Basket> addToBasket(BookIdRequest bookIdRequest, Integer accountId) {

        return ResponseEntity.ok(accountInterface.addToBasket(bookIdRequest, accountId));
    }

    @Override
    public ResponseEntity<Basket> getBasket(Integer accountId) {
        return ResponseEntity.ok(accountInterface.getBasket(accountId));
    }

    @Override
    public ResponseEntity<List<Book>> getPurchasedBooks(Integer limit, Integer offset, String sortBook, String order, Integer accountId) {
        return ResponseEntity.ok(accountInterface.getPurchasedBooks(limit, offset, sortBook, order, accountId));
    }

    @Override
    public ResponseEntity<Void> removeBasketItem(Integer itemId) {
        accountInterface.removeBasketItem(itemId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}