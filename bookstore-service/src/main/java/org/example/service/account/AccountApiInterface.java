package org.example.service.account;

import org.example.model.Basket;
import org.example.model.Book;
import org.example.model.BookIdRequest;
import org.example.model.Order;
import org.example.model.SortBook;

import java.util.List;

public interface AccountApiInterface {

    List<Book> addFavoriteBook(Integer bookId);

    List<Book> getFavoriteBooks(SortBook sortBook, org.example.model.Order order, Integer limit, Integer offset);

    Basket addToBasket(BookIdRequest bookIdRequest);

    Basket getBasket();

    List<Book> getPurchasedBooks(Order order, Integer limit, Integer offset, SortBook sortBook);

    void removeBasketItem(Integer itemId);
}
