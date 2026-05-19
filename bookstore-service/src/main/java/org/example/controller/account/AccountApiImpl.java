package org.example.controller.account;

import org.example.model.AddFavoriteBookRequest;
import org.example.model.Basket;
import org.example.model.Book;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.example.api.AccountApi;

import java.util.List;

@RestController
public class AccountApiImpl implements AccountApi {

    @Override
    public ResponseEntity<List<Book>> addFavoriteBook(AddFavoriteBookRequest addFavoriteBookRequest) {
        return ResponseEntity.ok(List.of());
    }

    @Override
    public ResponseEntity<Basket> addToBasket(AddFavoriteBookRequest addFavoriteBookRequest) {
        Basket basket = new Basket();
        return ResponseEntity.ok(basket);
    }

    @Override
    public ResponseEntity<Basket> getBasket() {
        Basket basket = new Basket();
        return ResponseEntity.ok(basket);    }

    @Override
    public ResponseEntity<List<Book>> getPurchasedBooks(Integer limit, Integer offset, String sort, String order) {
        return ResponseEntity.ok(List.of());
    }

    @Override
    public ResponseEntity<Void> removeBasketItem(Integer itemId) {
        return ResponseEntity.ok().build();
    }

}
