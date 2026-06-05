package org.example.controller.account;

import lombok.RequiredArgsConstructor;
import org.example.model.*;
import org.example.model.Basket;
import org.example.model.Book;
import org.example.model.BookIdRequest;
import org.example.model.Order;
import org.example.model.SortBook;
import org.example.service.account.AccountApiInterface;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.example.api.AccountApi;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AccountApiImpl implements AccountApi {

    private final AccountApiInterface accountInterface;

    @Override
    public ResponseEntity<List<Book>> addFavoriteBook(Integer bookId) {
        return ResponseEntity.ok(accountInterface.addFavoriteBook(bookId));
    }

    @Override
    public ResponseEntity<List<Book>> getFavoriteBooks(SortBook sortBook, Order order, Integer limit, Integer offset) {
        return ResponseEntity.ok(accountInterface.getFavoriteBooks(sortBook, order, limit, offset));
    }

    @Override
    public ResponseEntity<Basket> addToBasket(BookIdRequest bookIdRequest) {
        return ResponseEntity.ok(accountInterface.addToBasket(bookIdRequest));
    }

    @Override
    public ResponseEntity<Basket> getBasket() {
        return ResponseEntity.ok(accountInterface.getBasket());
    }

    @Override
    public ResponseEntity<List<Book>> getPurchasedBooks(Order order, Integer limit, Integer offset, SortBook sortBook) {
        return ResponseEntity.ok(accountInterface.getPurchasedBooks(order, limit, offset, sortBook));
    }

    @Override
    public ResponseEntity<Void> removeBasketItem(Integer itemId) {
        accountInterface.removeBasketItem(itemId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}