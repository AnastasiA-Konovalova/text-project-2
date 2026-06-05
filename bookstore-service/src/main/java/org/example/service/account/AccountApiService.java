package org.example.service.account;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.database.*;
import org.example.exception.NotFoundException;
import org.example.exception.UserDoesNotOwnDataException;
import org.example.mapper.BasketMapper;
import org.example.mapper.BookMapper;
import org.example.model.*;
import org.example.model.Basket;
import org.example.model.Book;
import org.example.model.BookIdRequest;
import org.example.model.Order;
import org.example.model.SortBook;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AccountApiService implements AccountApiInterface {

    private final BookRepository bookRepository;
    private final BasketRepository basketRepository;
    private final UserRepository userRepository;
    private final BasketDetailRepository basketDetailRepository;
    private final BookMapper bookMapper;
    private final BasketMapper basketMapper;

    @Transactional
    public List<Book> addFavoriteBook(Integer bookId) {
        BookEntity bookEntity = existBookEntity(bookId);

        String email = authenticationUser();
        UserEntity user = existUserEntity(email);

        user.getFavoriteBooks().add(bookEntity);

        userRepository.save(user);

        return user.getFavoriteBooks()
                .stream()
                .map(bookMapper::toDto)
                .toList();
    }

    @Override
    public List<Book> getFavoriteBooks(SortBook sortBook, Order order, Integer limit, Integer offset) {
        String email = authenticationUser();
        UserEntity user = existUserEntity(email);

        Set<BookEntity> books = user.getFavoriteBooks();

        Comparator<BookEntity> comparator = Comparator.comparing(BookEntity::getId);

        if (sortBook != null) {
            comparator = switch (sortBook) {
                case ID -> Comparator.comparing(BookEntity::getId);
                case TITLE -> Comparator.comparing(BookEntity::getTitle);
                case PRICE -> Comparator.comparing(BookEntity::getPrice);
            };
        }

        if (order == Order.DESC) comparator = comparator.reversed();

        return books.stream()
                .sorted(comparator)
                .map(bookMapper::toDto)
                .skip(offset)
                .limit(limit)
                .toList();
        }

    @Override
    public Basket addToBasket(BookIdRequest bookIdRequest) {
        BookEntity book = existBookEntity(bookIdRequest.getBookId());

        String email = authenticationUser();

        UserEntity user = existUserEntity(email);

        BasketEntity basket = user.getBasket();

        if (basket == null) {
            basket = new BasketEntity();
            basket.setUser(user);
            basket.setBasketDetails(new ArrayList<>());
            user.setBasket(basket);
        }

        BasketDetailEntity existing = basket.getBasketDetails()
                .stream()
                .filter(d -> d.getBook().getId().equals(book.getId()))
                .findFirst()
                .orElse(null);

        BigDecimal price = book.getPrice();

        if (existing != null) {
            int newQty = existing.getQuantity() + 1;
            existing.setQuantity(newQty);

            existing.setTotalPrice(price.multiply(BigDecimal.valueOf(newQty)));

        } else {
            BasketDetailEntity detail = new BasketDetailEntity();
            detail.setBasket(basket);
            detail.setBook(book);
            detail.setQuantity(1);
            detail.setPrice(price);
            detail.setTotalPrice(price);

            basket.getBasketDetails().add(detail);
        }
        recalculateBasket(basket);

        BasketEntity saved = basketRepository.save(basket);

        return basketMapper.toDto(saved);
    }

    @Override
    public Basket getBasket() {
        String email = authenticationUser();

        UserEntity user = existUserEntity(email);

        BasketEntity basketEntity = user.getBasket();

        return basketMapper.toDto(basketEntity);
    }

    @Override
    public List<Book> getPurchasedBooks(Order order, Integer limit, Integer offset, SortBook sortBook) {
        String email = authenticationUser();

        UserEntity user = existUserEntity(email);

        Set<BookEntity> books = user.getFavoriteBooks();

        Comparator<BookEntity> comparator = Comparator.comparing(BookEntity::getId);
        if (sortBook != null) {
            comparator = switch (sortBook) {
                case TITLE -> Comparator.comparing(BookEntity::getTitle);
                case ID -> Comparator.comparing(BookEntity::getId);
                case PRICE -> Comparator.comparing(BookEntity::getPrice);
            };
        }

        if (order == Order.DESC) comparator = comparator.reversed();

        return books.stream()
                .sorted(comparator)
                .map(bookMapper::toDto)
                .skip(offset)
                .limit(limit)
                .toList();
    }

    @Override
    public void removeBasketItem(Integer itemId) {
        String email = authenticationUser();

        BasketDetailEntity detail = existBasketDetailEntity(itemId);

        if (!detail.getBasket().getUser().getEmail().equals(email)) {
            throw new UserDoesNotOwnDataException("User doesn't owner data");
        }

        BasketEntity basket = detail.getBasket();

        basket.getBasketDetails().remove(detail);

        basketDetailRepository.delete(detail);

        recalculateBasket(basket);

        basketRepository.save(basket);
    }

    private void recalculateBasket(BasketEntity basket) {
        BigDecimal total = BigDecimal.ZERO;
        for (BasketDetailEntity detail : basket.getBasketDetails()) {
            if (detail.getTotalPrice() != null) {
                total = total.add(detail.getTotalPrice());
            }
        }
        int quantity = 0;
        for (BasketDetailEntity detail : basket.getBasketDetails()) {
            quantity += detail.getQuantity();
        }

        basket.setTotalPrice(total);
        basket.setQuantityBooks(quantity);
    }

    private String authenticationUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    private UserEntity existUserEntity(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found"));
    }

    private BookEntity existBookEntity(Integer id) {
        return bookRepository.findById(id).orElseThrow(() -> new NotFoundException("Book not found"));
    }

    private BasketDetailEntity existBasketDetailEntity(Integer id) {
        return basketDetailRepository.findById(id).orElseThrow(() -> new NotFoundException("Basket item not found"));
    }
}
