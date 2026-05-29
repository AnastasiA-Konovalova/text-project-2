package org.example.service.account;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.database.AccountApiRepository;
import org.example.database.BasketDetailRepository;
import org.example.database.BasketRepository;
import org.example.database.BookApiRepository;
import org.example.exeception.BasketItemNotFoundException;
import org.example.exeception.ClassNotFoundException;
import org.example.mapper.BasketMapper;
import org.example.mapper.BookMapper;
import org.example.model.*;
import org.example.model.Basket;
import org.example.model.Book;
import org.example.model.BookIdRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AccountApiService implements AccountApiInterface {

    public final AccountApiRepository accountRepository;
    public final BookApiRepository bookRepository;
    public final BasketRepository basketRepository;
    private final BasketDetailRepository basketDetailRepository;
    private final BookMapper bookMapper;
    private final BasketMapper basketMapper;

    public List<Book> addFavoriteBook(Integer bookId, Integer id) {
        BookEntity bookEntity = existBookEntity(bookId);
        AccountEntity accountEntity = existAccountEntity(id);

        accountEntity.getFavoriteBooks().add(bookEntity);

        accountRepository.save(accountEntity);

        return accountEntity.getFavoriteBooks()
                .stream()
                .map(bookMapper::toDto)
                .toList();
    }

    @Override
    public List<Book> getFavoriteBooks(Integer accountId,
                                             Integer limit,
                                             Integer offset,
                                             String sortBook,
                                             String order) {
        AccountEntity account = existAccountEntity(accountId);

        Set<BookEntity> books = account.getFavoriteBooks();

        Comparator<BookEntity> comparator;
        if ("title".equalsIgnoreCase(sortBook)) comparator = Comparator.comparing(BookEntity::getTitle);
        else comparator = Comparator.comparing(BookEntity::getId);

        if ("desc".equalsIgnoreCase(order)) comparator = comparator.reversed();

        return books.stream()
                .sorted(comparator)
                .map(bookMapper::toDto)
                .skip(offset)
                .limit(limit)
                .toList();
        }

//    @Transactional
    @Override
    public Basket addToBasket(BookIdRequest bookIdRequest, Integer accountId) {
        BookEntity book = existBookEntity(bookIdRequest.getBookId());
        AccountEntity account = existAccountEntity(accountId);

        BasketEntity basket = account.getBasket();

        // 1. создаём корзину, если её нет
        if (basket == null) {
            basket = new BasketEntity();
            basket.setAccount(account);
            basket.setBasketDetails(new ArrayList<>());
            account.setBasket(basket);
        }

        // 2. ищем уже существующий товар в корзине
        BasketDetailEntity existing = basket.getBasketDetails()
                .stream()
                .filter(d -> d.getBook().getId().equals(book.getId()))
                .findFirst()
                .orElse(null);

        BigDecimal price = book.getPrice();

        if (existing != null) {
            // 3. если есть — увеличиваем количество
            int newQty = existing.getQuantity() + 1;
            existing.setQuantity(newQty);

            existing.setTotalPrice(price.multiply(BigDecimal.valueOf(newQty)));

        } else {
            // 4. если нет — создаём новую строку
            BasketDetailEntity detail = new BasketDetailEntity();
            detail.setBasket(basket);
            detail.setBook(book);
            detail.setQuantity(1);
            detail.setPrice(price);
            detail.setTotalPrice(price);

            basket.getBasketDetails().add(detail);
        }

        // 5. пересчёт общей суммы корзины
        recalculateBasket(basket);

        BasketEntity saved = basketRepository.save(basket);

        return basketMapper.toDto(saved);
    }

    @Override
    public Basket getBasket(Integer accountId) {
        AccountEntity account = existAccountEntity(accountId);
        BasketEntity basketEntity = account.getBasket();

        return basketMapper.toDto(basketEntity);
    }

    @Override
    public List<Book> getPurchasedBooks(Integer limit, Integer offset, String sortBook, String order, Integer accountId) {
        AccountEntity account = existAccountEntity(accountId);
        Set<BookEntity> books = account.getFavoriteBooks();

        Comparator<BookEntity> comparator;

        if ("title".equalsIgnoreCase(sortBook)) comparator = Comparator.comparing(BookEntity::getTitle);
        else if("id".equalsIgnoreCase(sortBook)) comparator =  Comparator.comparing(BookEntity:: getId);
        else comparator = Comparator.comparing(BookEntity::getPrice);

        if ("desc".equalsIgnoreCase(order)) comparator = comparator.reversed();

        return books.stream()
                .sorted(comparator)
                .map(bookMapper::toDto)
                .skip(offset)
                .limit(limit)
                .toList();
    }

    @Override
    public void removeBasketItem(Integer itemId) {
        BasketDetailEntity detail = existBasketDetailEntity(itemId);

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

//        BigDecimal total = basket.getBasketDetails()
//                .stream()
//                .map(BasketDetailEntity::getTotalPrice)
//                .filter(Objects::nonNull)
//                .reduce(BigDecimal.ZERO, BigDecimal::add);

//        int quantity = basket.getBasketDetails()
//                .stream()
//                .mapToInt(BasketDetailEntity::getQuantity)
//                .sum();

        basket.setTotalPrice(total);
        basket.setQuantityBooks(quantity);
    }

    private AccountEntity existAccountEntity(Integer id) {
        return accountRepository.findById(id).orElseThrow(() -> new ClassNotFoundException("Account not found"));
    }

    private BookEntity existBookEntity(Integer id) {
        return bookRepository.findById(id).orElseThrow(() -> new ClassNotFoundException("Book not found"));
    }

    private BasketDetailEntity existBasketDetailEntity(Integer id) {
        return basketDetailRepository.findById(id).orElseThrow(() -> new BasketItemNotFoundException("Basket item not found"));
    }

}

