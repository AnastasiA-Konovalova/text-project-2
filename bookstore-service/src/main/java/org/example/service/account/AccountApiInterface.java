package org.example.service.account;

import org.example.model.Basket;
import org.example.model.Book;
import org.example.model.BookIdRequest;

import java.util.List;

public interface AccountApiInterface {

    List<Book> addFavoriteBook(Integer bookId, Integer id);

    List<Book> getFavoriteBooks(Integer accountId, Integer limit, Integer offset, String sortBook, String order);

    Basket addToBasket(BookIdRequest bookIdRequest, Integer accountId);

    Basket getBasket(Integer accountId);

    List<Book> getPurchasedBooks(Integer limit, Integer offset, String sortBook, String order, Integer accountId);

    void removeBasketItem(Integer itemId);

}
