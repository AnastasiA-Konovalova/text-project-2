package integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.example.Main;
import org.example.database.*;
import org.example.model.*;
import org.example.model.BookIdRequest;
import org.example.model.Genre;
import org.example.model.Order;
import org.example.model.SortBook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = Main.class, properties = {
        "JWT_SECRET=test-secret-key-test-secret-key-test-secret-key"
})
@AutoConfigureMockMvc(addFilters = false)
@Transactional
@WithMockUser(username = "email3@mail.ru")
public class AccountServiceIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private PublisherRepository publisherRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private BasketRepository basketRepository;

    @Autowired
    private BasketDetailRepository basketDetailRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private UserEntity savedUser;

    private BasketEntity basket;

    private BasketDetailEntity detail;

    private UserEntity user;

    private BookEntity book;

    @BeforeEach
    void setUp() {
        user = new UserEntity();
        user.setName("Ivan");
        user.setSurname("Ivanov");
        user.setEmail("email3@mail.ru");
        user.setMiddleName(null);
        user.setPassword("password1");
        user.setPhoneNumber("89123568854");

        AuthorEntity author = new AuthorEntity();
        author.setName("Aizek");
        author.setSurname("Azimov");
        author = authorRepository.save(author);

        book = new BookEntity();
        book.setTitle("Test Book");
        book.setAuthor(author);
        book.setGenre(Genre.COMEDY);
        book.setPrice(BigDecimal.valueOf(5000.0));
        book.setDescription("Description1");
        book.setPages(300);
        book.setReviewCount(5);
        book.setAverageRating(4);
        book.setReleaseDate(LocalDateTime.now());

        PublisherEntity publisher = new PublisherEntity();
        publisher.setName("Line");
        publisher.setCountry("USA");
        publisher = publisherRepository.save(publisher);

        book.setPublisher(publisher);

        BookEntity savedBook = bookRepository.save(book);

        basket = new BasketEntity();

        detail = new BasketDetailEntity();
        detail.setBook(savedBook);
        detail.setTitle(savedBook.getTitle());
        detail.setPrice(savedBook.getPrice());
        detail.setQuantity(1);
        detail.setTotalPrice(savedBook.getPrice());

        detail.setBasket(basket);
        basket.getBasketDetails().add(detail);
        basket.setUser(user);
        basket = basketRepository.save(basket);

        user.setBasket(basket);
        savedUser = userRepository.save(user);
    }

    @Test
    void postFavoriteBook_ShouldReturnListBook() throws Exception {
        mockMvc.perform(post("/account/favorite")
                        .param("bookId", String.valueOf(book.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(book.getId()))
                .andExpect(jsonPath("$[0].title").value("Test Book"))
                .andExpect(jsonPath("$[0].description").value("Description1"))
                .andExpect(jsonPath("$[0].pages").value(300))
                .andExpect(jsonPath("$[0].reviewCount").value(5))
                .andExpect(jsonPath("$[0].averageRating").value(4));
    }

    @Test
    void postFavoriteBook_ShouldThrowNotFound() throws Exception {
        mockMvc.perform(post("/account/favorite"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getFavoriteBooks_ShouldReturnListBook() throws Exception {
        int limit = 10;
        int offset = 0;

        mockMvc.perform(get("/account/favorite")
                        .param("limit", String.valueOf(limit))
                        .param("sortBook", SortBook.ID.name())
                        .param("order", Order.DESC.name())
                        .param("offset", String.valueOf(offset)))
                .andExpect(status().isOk());
    }

    @Test
    void getFavoriteBooks_ShouldReturnEmptyList() throws Exception {
        mockMvc.perform(get("/account/favorite")
                        .param("limit", String.valueOf(10)))
                .andExpect(status().isOk());
    }

    @Test
    void addToBasket_ShouldCreateBasketAndAddBook() throws Exception {
        BookIdRequest request = new BookIdRequest();
        request.setBookId(book.getId());

        mockMvc.perform(post("/account/basket/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.basketDetails", hasSize(1)))
                .andExpect(jsonPath("$.basketDetails[0].bookId").value(book.getId()))
                .andExpect(jsonPath("$.basketDetails[0].title").value("Test Book"))
                .andExpect(jsonPath("$.basketDetails[0].price").value(5000.0))
                .andExpect(jsonPath("$.basketDetails[0].quantity").value(2));
    }

    @Test
    void addToBasket_ShouldThrowExceptionNotFound() throws Exception {
        BookIdRequest request = new BookIdRequest();
        request.setBookId(999999);

        mockMvc.perform(post("/account/basket/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void getBasket_ShouldReturnBasket() throws Exception {
        mockMvc.perform(get("/account/basket")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.basketDetails[0].title").value("Test Book"))
                .andExpect(jsonPath("$.basketDetails[0].price").value(5000.0))
                .andExpect(jsonPath("$.basketDetails[0].quantity").value(1))
                .andExpect(jsonPath("$.basketDetails[0].totalPrice").value(5000.0));
    }

    @Test
    void getBasket_EmptyBasket_ShouldReturnEmptyList() throws Exception {
        BasketEntity basket = new BasketEntity();
        basket = basketRepository.save(basket);

        user.setBasket(basket);
        userRepository.save(user);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        user.getEmail(),
                        null,
                        List.of()
                )
        );
        mockMvc.perform(get("/account/basket"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.basketDetails").isEmpty());
    }

    @Test
    void removeBasketItem_ShouldRemoveItemById() throws Exception {
        Integer itemId = basket.getBasketDetails().get(0).getId();


        mockMvc.perform(delete("/account/basket/items/{itemId}", itemId))
                .andExpect(status().isCreated());
        assertFalse(basketDetailRepository.existsById(detail.getId()));
    }

    @Test
    void removeBasketItem_WhenIdIsNotExists() throws Exception {
        mockMvc.perform(delete("/account/basket/items/{itemId}", 9999))
                .andExpect(status().isNotFound());
    }
}


