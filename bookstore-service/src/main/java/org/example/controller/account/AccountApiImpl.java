package org.example.controller.account;

import org.example.model.Basket;
import org.example.model.Book;
import org.example.model.BookIdRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.example.api.AccountApi;

import java.util.List;

@RestController
public class AccountApiImpl implements AccountApi {


    @Override
    public ResponseEntity<List<Book>> addFavoriteBook(Integer bookId) {
        return ResponseEntity.ok(List.of());
    }

    @Override
    public ResponseEntity<Basket> addToBasket(BookIdRequest bookIdRequest) {
        Basket basket = new Basket();
        return ResponseEntity.ok(basket);
    }

    @Override
    public ResponseEntity<Basket> getBasket() {
        Basket basket = new Basket();
        return ResponseEntity.ok(basket);
    }

    @Override
    public ResponseEntity<List<Book>> getPurchasedBooks(Integer limit, Integer offset, String sortBook, String order) {
        return ResponseEntity.ok(List.of());
    }

    @Override
    public ResponseEntity<Void> removeBasketItem(Integer itemId) {
        return ResponseEntity.ok().build();
    }
}
