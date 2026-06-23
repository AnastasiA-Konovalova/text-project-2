package unit;

import org.example.database.BasketRepository;
import org.example.database.BookRepository;
import org.example.database.UserRepository;
import org.example.mapper.BasketMapper;
import org.example.mapper.BookMapper;
import org.example.model.*;
import org.example.model.Basket;
import org.example.model.Book;
import org.example.model.BookIdRequest;
import org.example.model.Order;
import org.example.model.SortBook;
import org.example.service.account.AccountApiService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @InjectMocks
    private AccountApiService accountService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BasketRepository basketRepository;

    @Mock
    private BookMapper bookMapper;

    @Mock
    private BasketMapper basketMapper;


    @Test
    void postAddFavoriteBook_shouldReturnListBookDto() {
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);

        when(authentication.getName()).thenReturn("email@email.ru");
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        Integer bookId = 1;
        String email = "email@email.ru";

        BookEntity book = new BookEntity();
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail("email@email.ru");
        userEntity.setFavoriteBooks(new HashSet<>());

        Book dto = new Book();

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(userEntity));
        when(bookMapper.toDto(any(BookEntity.class)))
                .thenReturn(dto);

        BookIdRequest bookIdRequest = new BookIdRequest();
        bookIdRequest.setBookId(bookId);

        List<Book> result = accountService.addFavoriteBook(bookIdRequest.getBookId());

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(bookRepository).findById(bookId);
    }

    @Test
    void postAddToBasket_shouldReturnBasketDto() {
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);

        when(authentication.getName()).thenReturn("email@email.ru");
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        Integer bookId = 1;
        String email = "email@email.ru";

        BookEntity book = new BookEntity();
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail("email@email.ru");

        BasketEntity basket = new BasketEntity();
        Basket dtoBasket = new Basket();

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(basketRepository.save(any(BasketEntity.class))).thenReturn(basket);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(userEntity));
        when(basketMapper.toDto(any(BasketEntity.class)))
                .thenReturn(dtoBasket);

        BookIdRequest bookIdRequest = new BookIdRequest();
        bookIdRequest.setBookId(bookId);

        Basket result = accountService.addToBasket(bookIdRequest);

        assertNotNull(result);

        verify(bookRepository).findById(bookId);
        verify(basketRepository).save(any(BasketEntity.class));
    }

    @Test
    void getFavoriteBooks_shouldReturnListBook() {
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);

        when(authentication.getName()).thenReturn("email@email.ru");
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        String email = "email@email.ru";

        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(email);

        BookEntity bookOne = new BookEntity();
        bookOne.setId(1);
        BookEntity bookTwo = new BookEntity();
        bookTwo.setId(2);

        userEntity.setFavoriteBooks(new HashSet<>(List.of(bookOne, bookTwo)));

        Book dtoBookOne = new Book();
        Book dtoBookTwo = new Book();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(userEntity));
        when(bookMapper.toDto(bookOne)).thenReturn(dtoBookOne);
        when(bookMapper.toDto(bookTwo)).thenReturn(dtoBookTwo);


        List<Book> result = accountService.getFavoriteBooks(SortBook.ID, Order.ASC, 10, 0);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.containsAll(List.of(dtoBookOne, dtoBookTwo)));

        verify(userRepository).findByEmail(email);
        verify(bookMapper).toDto(bookOne);
        verify(bookMapper).toDto(bookTwo);
    }

    @Test
    void getBasket_shouldReturnBasket() {
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);

        when(authentication.getName()).thenReturn("email@email.ru");
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        String email = "email@email.ru";

        BasketEntity entity = new BasketEntity();
        entity.setId(1);

        UserEntity user = new UserEntity();
        user.setEmail("email@email.ru");
        user.setBasket(entity);
        Basket dto = new Basket();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(basketMapper.toDto(entity)).thenReturn(dto);

        Basket basket = accountService.getBasket();

        assertNotNull(basket);

        verify(userRepository).findByEmail(email);
        verify(basketMapper).toDto(entity);
    }

    @Test
    void getPurchasedBooks_shouldReturnBasket() {
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);

        when(authentication.getName()).thenReturn("email@email.ru");
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        String email = "email@email.ru";

        BasketEntity entity = new BasketEntity();
        entity.setId(1);

        UserEntity user = new UserEntity();
        user.setEmail("email@email.ru");
        user.setBasket(entity);
        BookEntity bookEntityOne = new BookEntity();
        bookEntityOne.setId(1);
        BookEntity bookEntityTwo = new BookEntity();
        bookEntityTwo.setId(2);
        user.setFavoriteBooks(new HashSet<>(List.of(bookEntityOne, bookEntityTwo)));

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        List<Book> result = accountService.getPurchasedBooks(Order.ASC, 10, 0, SortBook.ID);
        assertNotNull(result);
        assertEquals(2, result.size());

        verify(userRepository).findByEmail(email);
    }
}
